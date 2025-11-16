package org.joon.appLinker.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.joon.appLinker.AppLinker.Companion.plugin
import org.joon.appLinker.constant.PlayerMessage
import org.joon.appLinker.util.Firebase

class FriendCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return false

        val arg = args[0]
        if (args.isEmpty() || arg != "추가" || args.size < 2) {
            sender.sendMessage(PlayerMessage.FRIEND_USAGE)
            return true
        }

        val targetName = args[1]
        val targetPlayer = Bukkit.getPlayer(targetName)
        if (targetPlayer == null) {
            sender.sendMessage(PlayerMessage.PLAYER_NOT_EXIST)
            return true
        }
        val targetUUID = targetPlayer.uniqueId.toString()
        val senderUUID = sender.uniqueId.toString()
        if (targetUUID == senderUUID) {
            sender.sendMessage(PlayerMessage.FRIEND_NOT_MYSELF)
            return true
        }

        if (Firebase.isFriend(senderUUID, targetUUID)) {
            sender.sendMessage(PlayerMessage.FRIEND_ALREADY)
            return true
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val ok = Firebase.addFriend(senderUUID, targetUUID)
            Bukkit.getScheduler().runTask(plugin, Runnable {
                if (ok) sender.sendMessage(PlayerMessage.FRIEND_ADD)
            })
        })

        return true
    }

}