package com.github.smykla.monokaiislands.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Service
@State(
    name = MonokaiIslandsSettings.SERVICE_NAME,
    storages = [Storage(MonokaiIslandsSettings.STORAGE_FILE)]
)
class MonokaiIslandsSettings : PersistentStateComponent<MonokaiIslandsSettings> {

    var enableMarkdownCss: Boolean = false

    override fun getState(): MonokaiIslandsSettings = this

    override fun loadState(state: MonokaiIslandsSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        const val SERVICE_NAME = "com.github.smykla.monokaiislands.settings.MonokaiIslandsSettings"
        const val STORAGE_FILE = "monokai-islands.xml"

        fun getInstance(): MonokaiIslandsSettings =
            ApplicationManager.getApplication().getService(MonokaiIslandsSettings::class.java)
    }
}
