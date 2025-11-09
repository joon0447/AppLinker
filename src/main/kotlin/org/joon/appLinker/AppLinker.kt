package org.joon.appLinker

import org.bukkit.plugin.java.JavaPlugin
import org.joon.appLinker.Command.LinkCommand
import org.joon.appLinker.Util.DataFolder
import org.joon.appLinker.Util.Firebase

class AppLinker : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        DataFolder.checkDataFolderExists()
        Firebase.initFirebase()
        loadCommands()
        println("AppLinker enabled")
    }

    override fun onDisable() {

    }

    private fun loadCommands() {
        getCommand("앱")?.setExecutor(LinkCommand(Firebase.getFirestore()))
    }

    companion object {
        lateinit var plugin : AppLinker
            private set
    }
}
