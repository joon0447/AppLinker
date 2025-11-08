package org.joon.appLinker.Constant

import org.bukkit.Color

object Message {
    private val prefix = "${Color.GREEN} [ ! ] ${Color.WHITE}"
    val LINK_CODE = "${prefix}앱 연동 코드는 %s 입니다."

    fun codeMessage(code: String): String {
        return LINK_CODE.format(code)
    }
}