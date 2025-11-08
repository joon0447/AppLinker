package org.joon.appLinker

import org.bukkit.plugin.java.JavaPlugin
import org.joon.appLinker.Command.LinkCommand

class AppLinker : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        loadCommands()
        println("AppLinker enabled")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun loadCommands() {
        getCommand("앱")?.setExecutor(LinkCommand())
    }
}
