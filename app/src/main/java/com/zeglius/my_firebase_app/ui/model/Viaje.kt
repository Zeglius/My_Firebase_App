package com.zeglius.my_firebase_app.ui.model

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

data class Viaje(
    var idViaje: String? = null,
    var origen: String? = null,
    var destino: String? = null,
    var imageBitmapStoragePath: String? = null,
) {
    val storage by lazy { Firebase.storage.reference }
}

fun Viaje.imageUrl(): String? {
    return runBlocking { this@imageUrl.imageBitmapStoragePath?.let { storage.child(it).downloadUrl.await().toString() } }

}