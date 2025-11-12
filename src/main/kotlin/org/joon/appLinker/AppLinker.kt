package org.joon.appLinker

import org.bukkit.plugin.java.JavaPlugin
import org.joon.appLinker.Command.FriendCommand
import org.joon.appLinker.Command.LinkCommand
import org.joon.appLinker.Server.ApiServer
import org.joon.appLinker.Util.DataFolder
import org.joon.appLinker.Util.Firebase

class AppLinker : JavaPlugin() {

    private lateinit var apiServer : ApiServer

    override fun onEnable() {
        plugin = this
        DataFolder.checkDataFolderExists()
        Firebase.initFirebase()
        loadCommands()

        apiServer = ApiServer(8000)
        apiServer.start()
        println("AppLinker enabled")
    }

    override fun onDisable() {
        apiServer.stop()
    }

    private fun loadCommands() {
        getCommand("앱")?.setExecutor(LinkCommand(Firebase.getFirestore()))
        getCommand("친구")?.setExecutor(FriendCommand())
    }

    companion object {
        lateinit var plugin : AppLinker
            private set
    }
}
