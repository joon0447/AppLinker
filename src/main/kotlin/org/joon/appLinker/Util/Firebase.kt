package org.joon.appLinker.Util

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.joon.appLinker.AppLinker
import java.io.File
import java.io.FileInputStream

object Firebase {
    private var initialized = false
    private lateinit var firestoreInstance: Firestore
    private val plugin = AppLinker.plugin
    fun initFirebase() {
        val dataFolder = plugin.dataFolder

        val keyFile = File(dataFolder, "serviceAccount.json")
        if (!keyFile.exists()) {
            plugin.logger.warning("파이어베이스 계정 파일이 존재하지 않습니다.")
            return
        }

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(keyFile)))
            .setProjectId("minecraft-applinker")
            .build()

        FirebaseApp.initializeApp(options)
        firestoreInstance = FirestoreClient.getFirestore()
        initialized = true

        plugin.logger.info("파이어베이스 연결이 완료되었습니다.")
    }

    fun getFirestore(): Firestore {
        if (!initialized) {
            throw IllegalArgumentException("파이어베이스가 초기화되지 않았습니다.")
        }
        return firestoreInstance
    }
}
