#!/usr/bin/env bash
# Theme development - runs sandbox IDE, palette watcher, and hot reload together
#
# Manages three concurrent processes:
# 1. Sandbox GoLand IDE (runIde task)
# 2. Palette file watcher (triggers theme regeneration)
# 3. Build watcher (continuous rebuild for hot reload)

set -euo pipefail

# Change to the project root directory
SCRIPT_DIR=""
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly SCRIPT_DIR

PROJECT_ROOT=""
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
readonly PROJECT_ROOT

cd "${PROJECT_ROOT}"

# Configuration
readonly RUNIDE_LOG="/tmp/monokai-islands-runide.log"
readonly PALETTE_FILE="palettes/monokai-dark.json"
readonly GENERATOR_SCRIPT="scripts/generate-themes.py"

# Process IDs (global for cleanup)
RUNIDE_PID=""
PALETTE_WATCH_PID=""
BUILD_WATCH_PID=""

cleanup() {
    local exit_code=$?

    echo ""
    echo "🛑 Stopping development environment..."

    # Graceful shutdown first
  local pid
    for pid in "${RUNIDE_PID}" "${PALETTE_WATCH_PID}" "${BUILD_WATCH_PID}"; do
        if [[ -n "${pid}" ]] && kill -0 "${pid}" 2>/dev/null; then
            kill "${pid}" 2>/dev/null || true
        fi
    done

    sleep 1

    # Force kill if still running
    for pid in "${RUNIDE_PID}" "${PALETTE_WATCH_PID}" "${BUILD_WATCH_PID}"; do
        if [[ -n "${pid}" ]] && kill -0 "${pid}" 2>/dev/null; then
            kill -9 "${pid}" 2>/dev/null || true
        fi
    done

    exit "${exit_code}"
}

watch_palette_fswatch() {
    fswatch -o "${PALETTE_FILE}" | while IFS= read -r; do
        echo "🔄 Palette changed, regenerating theme..."
        if python3 "${GENERATOR_SCRIPT}"; then
            echo "✓ Theme regenerated successfully"
        else
            echo "✗ Theme regeneration failed" >&2
        fi
    done
}

# Fallback when fswatch not available
watch_palette_polling() {
    # Get file modification time (cross-platform)
    get_mtime() {
        if command -v gstat >/dev/null 2>&1; then
            # GNU stat (from coreutils)
            gstat -c %Y "$1"
        elif [[ "$(uname -s)" == "Darwin" ]]; then
            # BSD stat (macOS default)
            stat -f %m "$1"
        else
            # Linux stat
            stat -c %Y "$1"
        fi
    }

    local last_modified
    last_modified=$(get_mtime "${PALETTE_FILE}")

    while true; do
        sleep 1

        local current_modified
        current_modified=$(get_mtime "${PALETTE_FILE}")

        if [[ "${current_modified}" != "${last_modified}" ]]; then
            echo "🔄 Palette changed, regenerating theme..."
            if python3 "${GENERATOR_SCRIPT}"; then
                echo "✓ Theme regenerated successfully"
            else
                echo "✗ Theme regeneration failed" >&2
            fi
            last_modified="${current_modified}"
        fi
    done
}

validate_requirements() {
    local errors=0

    if [[ ! -f "${PALETTE_FILE}" ]]; then
        echo "✗ Error: Palette file not found: ${PALETTE_FILE}" >&2
        errors=1
    fi

    if [[ ! -f "${GENERATOR_SCRIPT}" ]]; then
        echo "✗ Error: Generator script not found: ${GENERATOR_SCRIPT}" >&2
        errors=1
    fi

    if ! command -v python3 >/dev/null 2>&1; then
        echo "✗ Error: python3 not found in PATH" >&2
        errors=1
    fi

    if [[ ! -x "./gradlew" ]]; then
        echo "✗ Error: gradlew not found or not executable" >&2
        errors=1
    fi

    return "${errors}"
}

main() {
    trap cleanup EXIT INT TERM

    if ! validate_requirements; then
        exit 1
    fi

    echo "🎨 Starting Theme Development Environment"
    echo "=========================================="
    echo ""

    # Start sandbox IDE in background (with devMode flag for hot reload)
    echo "📦 Starting sandbox IDE..."
    ./gradlew runIde -PdevMode > "${RUNIDE_LOG}" 2>&1 &
    RUNIDE_PID=$!

    # Start palette file watcher in background
    echo "🎨 Starting palette watcher..."
    if command -v fswatch >/dev/null 2>&1; then
        echo "   Using fswatch for file monitoring"
        watch_palette_fswatch &
        PALETTE_WATCH_PID=$!
    else
        echo "   fswatch not found, using polling fallback"
        echo "   (Install fswatch for better performance: brew install fswatch)"
        watch_palette_polling &
        PALETTE_WATCH_PID=$!
    fi

    # Wait for IDE to initialize
    sleep 2

    # Start build watcher in foreground
    echo "👀 Starting build watcher..."
    echo ""
    echo "✅ Ready! Edit palette files - changes auto-reload in sandbox IDE"
    echo "📝 RunIDE logs: ${RUNIDE_LOG}"
    echo "📂 Watching: ${PALETTE_FILE}"
    echo ""
    echo "Press Ctrl+C to stop all processes"
    echo ""

    ./gradlew buildPlugin --continuous --quiet -PdevMode &
    BUILD_WATCH_PID=$!

    # Wait for all background processes
    wait
}

main "$@"
