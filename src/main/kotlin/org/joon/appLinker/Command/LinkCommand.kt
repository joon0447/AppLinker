package org.joon.appLinker.Command

import com.google.cloud.firestore.Firestore
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.joon.appLinker.Constant.PlayerMessage
import org.joon.appLinker.Util.Firebase

class LinkCommand(firestore: Firestore) : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if(sender !is Player) return false
        val code = generateCode(CODE_LENGTH)
        val uuid = sender.uniqueId.toString()
        sender.sendMessage(PlayerMessage.codeMessage(code))
        Firebase.saveCode(uuid, code)
        return true
    }

    private fun generateCode(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    companion object {
        const val CODE_LENGTH = 6
    }
}