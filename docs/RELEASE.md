# Release Automation

This project uses automated CI/CD for releases.

## Overview

- **CI Workflow**: Runs on pull requests (`mise run lint`, `./gradlew verifyPlugin`, `./gradlew buildPlugin`)
- **Release Workflow**: Runs daily at 03:00 UTC via scheduled cron job or manually via `workflow_dispatch`
- **Versioning**: Managed by [semantic-release](https://semantic-release.gitbook.io/) based on [Conventional Commits](https://www.conventionalcommits.org/)

## Release Process

Releases are fully automated:

1. Commits following Conventional Commits format are analyzed
2. If releasable changes exist (`feat`, `fix`, or `BREAKING CHANGE`), semantic-release:
   - Determines the next version (major/minor/patch)
   - Updates `gradle.properties` version
   - Generates `CHANGELOG.md`
   - Builds and signs the plugin
   - Publishes to JetBrains Marketplace
   - Creates a GitHub release with plugin artifacts

## Required GitHub Secrets

The following GitHub secrets must be configured for automated plugin signing and publishing to JetBrains Marketplace.

### Setup Instructions

Add these secrets in your repository settings: **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

### JETBRAINS_CERTIFICATE_CHAIN

**Description**: Full certificate chain in PEM format for signing the plugin.

**How to obtain**:

1. Generate a certificate chain following [JetBrains Plugin Signing documentation](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html)
2. Export the full certificate chain to PEM format
3. Copy the entire PEM content (including `-----BEGIN CERTIFICATE-----` and `-----END CERTIFICATE-----` headers)

**Format**: Multi-line PEM certificate chain

### JETBRAINS_PRIVATE_KEY

**Description**: Private key corresponding to the certificate in PEM format.

**How to obtain**:

1. Export your private key to PEM format
2. Copy the entire PEM content (including `-----BEGIN PRIVATE KEY-----` and `-----END PRIVATE KEY-----` headers)

**Format**: Multi-line PEM private key

### JETBRAINS_PRIVATE_KEY_PASSWORD

**Description**: Password protecting the private key (if applicable).

**How to obtain**:

- If your private key is password-protected, use that password
- If your private key is not password-protected, set this to an empty string

**Format**: String (can be empty)

### JETBRAINS_MARKETPLACE_TOKEN

**Description**: Permanent token for publishing to JetBrains Marketplace.

**How to obtain**:

1. Log in to [JetBrains Marketplace](https://plugins.jetbrains.com/)
2. Go to your profile settings
3. Navigate to the **Tokens** section
4. Generate a new permanent token with publishing permissions
5. Copy the token value

**Format**: Token string

### Security Notes

- Never commit these secrets to the repository
- Rotate tokens and certificates periodically
- Use repository secrets, not environment secrets, for better isolation
- Only repository maintainers should have access to these secrets

### Validation

To verify the secrets are correctly configured, trigger the release workflow manually:

```bash
gh workflow run release.yml
```

Check the workflow run logs to ensure authentication succeeds with JetBrains services.

## Manual Release

Trigger a release manually:

```bash
gh workflow run release.yml
```

## Commit Message Format

Follow [Conventional Commits](https://www.conventionalcommits.org/) format:

```text
type(scope): description
```

### Release Triggers

- `feat:` → minor version bump (e.g., 1.0.0 → 1.1.0)
- `fix:` → patch version bump (e.g., 1.0.0 → 1.0.1)
- `feat!:` or `BREAKING CHANGE:` → major version bump (e.g., 1.0.0 → 2.0.0)
- `chore:`, `docs:`, `ci:`, `test:` → no release

### Examples

```bash
git commit -sS -m "feat(theme): add light theme variant"
git commit -sS -m "fix(colors): improve contrast for comments"
git commit -sS -m "feat(ui)!: redesign toolbar with new icons

BREAKING CHANGE: Toolbar layout has changed significantly"
```
