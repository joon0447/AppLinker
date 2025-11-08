package org.joon.appLinker

import org.bukkit.plugin.java.JavaPlugin

class AppLinker : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic

    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun loadCommands() {
        getCommand("앱")?.setExecutor(AppLinker())
    }
}
