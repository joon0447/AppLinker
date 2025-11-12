package org.joon.appLinker.Constant

import org.bukkit.ChatColor

object PlayerMessage {
    private val prefix = "${ChatColor.GREEN} [ ! ] ${ChatColor.WHITE}"
    val LINK_CODE = "${prefix}앱 연동 코드는 %s 입니다."

    val FRIEND_USAGE = "${prefix}사용법 : /친구 추가 <이름>"
    val FRIEND_NOT_MYSELF = "${prefix}자기 자신은 친구로 추가할 수 없습니다."
    val FRIEND_ALREADY = "${prefix}이미 친구입니다."
    val FRIEND_ADD = "${prefix}친구 추가가 완료되었습니다."
    val PLAYER_NOT_EXIST = "${prefix}플레이어가 존재하지 않습니다."

    fun codeMessage(code: String): String {
        return LINK_CODE.format(code)
    }
}