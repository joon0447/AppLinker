package org.joon.appLinker.Util

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.joon.appLinker.AppLinker
import org.joon.appLinker.Constant.LogMessage
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
            plugin.logger.warning(LogMessage.FIREBASE_FILE_NOT_EXIST.toString())
            return
        }

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(keyFile)))
            .setProjectId("minecraft-applinker")
            .build()

        FirebaseApp.initializeApp(options)
        firestoreInstance = FirestoreClient.getFirestore()
        initialized = true

        plugin.logger.info(LogMessage.FIREBASE_CONN_COMPLETE.toString())
    }

    fun getFirestore(): Firestore {
        if (!initialized) {
            throw IllegalArgumentException(LogMessage.FIREBASE_NOT_INIT.toString())
        }
        return firestoreInstance
    }

    fun saveCode(uuid: String, code: String): Boolean {
        val data: Map<String, Any> = mapOf(
            "uuid" to uuid,
            "code" to code,
            "expiresAt" to System.currentTimeMillis() + 24 * 60 * 60 * 1000
        )
        try {
            val collection = firestoreInstance.collection("linkCodes")
                .document(code)
                .set(data)

            collection.get()
            return true
        } catch(e: Exception) {
            plugin.logger.warning(e.message)
            return false
        }
    }
}
