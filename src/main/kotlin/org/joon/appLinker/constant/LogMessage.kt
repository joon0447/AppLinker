package org.joon.appLinker.constant

enum class LogMessage(val text: String) {
    FIREBASE_FILE_NOT_EXIST("파이어베이스 계정 파일이 존재하지 않습니다."),
    FIREBASE_CONN_COMPLETE("파이어베이스 연결이 완료되었습니다."),
    FIREBASE_NOT_INIT("파이어베이스가 초기화되지 않았습니다.");
    override fun toString(): String = text
}