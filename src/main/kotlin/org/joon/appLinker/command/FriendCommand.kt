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
        if(!argValidate(args, sender)) return false
        val targetName = args[1]

        if(!targetValidate(targetName, sender)) return false
        val targetUUID = Bukkit.getPlayer(targetName)?.uniqueId.toString()

        if(args[0] == "추가"){
            if(!friendValidate(targetName, sender)) return false
            addFriend(sender, targetUUID)
        }
        if(args[0] == "삭제") removeFriend(sender, targetUUID)
        return true
    }
}

private fun argValidate(
    args: Array<out String>,
    sender: Player
): Boolean {
    if (args.isEmpty() || args.size < 2) {
        sender.sendMessage(PlayerMessage.FRIEND_USAGE_1)
        sender.sendMessage(PlayerMessage.FRIEND_USAGE_2)
        return false
    }
    if (args[0] != "추가" && args[0] != "삭제") {
        sender.sendMessage(PlayerMessage.FRIEND_USAGE_1)
        sender.sendMessage(PlayerMessage.FRIEND_USAGE_2)
        return false
    }
    return true
}

private fun targetValidate(targetName: String, sender: Player): Boolean {
    val targetPlayer = Bukkit.getPlayer(targetName)
    if (targetPlayer == null) {
        sender.sendMessage(PlayerMessage.PLAYER_NOT_EXIST)
        return false
    }
    val targetUUID = targetPlayer.uniqueId.toString()
    val senderUUID = sender.uniqueId.toString()
    if (targetUUID == senderUUID) {
        sender.sendMessage(PlayerMessage.FRIEND_NOT_MYSELF)
        return false
    }
    return true
}

private fun friendValidate(targetName: String, sender: Player): Boolean {
    val targetPlayer = Bukkit.getPlayer(targetName)
    val targetUUID = targetPlayer?.uniqueId.toString()
    val senderUUID = sender.uniqueId.toString()
    if (Firebase.isFriend(senderUUID, targetUUID)) {
        sender.sendMessage(PlayerMessage.FRIEND_ALREADY)
        return false
    }
    return true
}

private fun addFriend(sender: Player, targetUUID: String) {
    val senderUUID = sender.uniqueId.toString()
    Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
        val ok = Firebase.addFriend(senderUUID, targetUUID)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (ok) sender.sendMessage(PlayerMessage.FRIEND_ADD)
        })
    })
}

private fun removeFriend(sender: Player, targetUUID: String) {
    val senderUUID = sender.uniqueId.toString()
    Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
        val ok = Firebase.removeFriend(senderUUID, targetUUID)
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (ok) sender.sendMessage(PlayerMessage.FRIEND_REMOVE)
        })
    })
}