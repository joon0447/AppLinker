package org.joon.appLinker.Util

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
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
                .document(uuid)
                .set(data)

            collection.get()
            return true
        } catch(e: Exception) {
            plugin.logger.warning(e.message)
            return false
        }
    }

    fun addFriend(ownerUuid: String, friendUuid: String): Boolean {
        return try{
            val docRef = firestoreInstance.collection("friends").document(ownerUuid)
            val updates = mapOf("friends" to FieldValue.arrayUnion(friendUuid))
            docRef.set(updates, SetOptions.merge()).get()
            true
        } catch(e: Exception) {
            plugin.logger.warning(e.message)
            false
        }
    }

    fun removeFriend(ownerUuid: String, friendUuid: String): Boolean {
        return try {
        val docRef = firestoreInstance.collection("friends").document(ownerUuid)
        val updates = mapOf("friends" to FieldValue.arrayRemove(friendUuid))
        docRef.set(updates, SetOptions.merge()).get()
        true
        } catch (e: Exception) {
            plugin.logger.warning("removeFriend error: ${e.message}")
            false
        }
    }

    fun isFriend(ownerUuid: String, friendUuid: String): Boolean {
        return try {
            val friends = getFriends(ownerUuid)
            friendUuid in friends
        } catch (e: Exception) {
            plugin.logger.warning("isFriend error: ${e.message}")
            false
        }
    }

    fun getFriends(ownerUuid: String): List<String> {
        return try {
            val snap = firestoreInstance.collection("friends").document(ownerUuid).get().get()
            @Suppress("UNCHECKED_CAST")
            (snap.get("friends") as? List<String>)?.toList() ?: emptyList()
        } catch (e: Exception) {
            plugin.logger.warning("getFriends error: ${e.message}")
            emptyList()
        }
    }
}



