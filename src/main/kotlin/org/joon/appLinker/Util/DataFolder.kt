package org.joon.appLinker.Util

import org.joon.appLinker.AppLinker

object DataFolder {
    private val dataFolder = AppLinker.plugin.dataFolder
    private val plugin = AppLinker.plugin

    fun checkDataFolderExists() {
        if(!dataFolder.exists()){
            plugin.logger.info("데이터 폴더가 존재하지 않아 새로 생성합니다..")
            createDataFolder()
        }
    }

    fun createDataFolder() {
        if(dataFolder.mkdirs()) {
            plugin.logger.info("플러그인 데이터 폴더가 생성되었습니다.")
        } else {
            plugin.logger.severe("플러그인 데이터 폴더가 생성되지 않았습니다.")
        }
    }
}