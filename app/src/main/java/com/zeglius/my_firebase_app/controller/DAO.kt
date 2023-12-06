package com.zeglius.my_firebase_app.controller

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.SuccessContinuation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storageMetadata
import com.zeglius.my_firebase_app.ui.model.Viaje
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

const val TAG: String = "DAO"

object DAO {
    suspend fun handleTravelCreation(
        origen: String,
        destino: String,
        bitmap: Bitmap,
    ) {
        val storageRef = Firebase.storage.reference
        if (Firebase.auth.currentUser == null) return
        val email = Firebase.auth.currentUser!!.email!!
        val userViajesCollection =
            Firebase.firestore.collection("viajes").document(email).collection("userViajes")

        // Get image bytes
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageBytes = stream.toByteArray()


        // Create viaje
        var myViaje = Viaje(origen = origen, destino = destino)


        // Upload viaje
        val viajeRef: DocumentReference = userViajesCollection.document()
        myViaje = myViaje.copy(idViaje = viajeRef.id)

        viajeRef.set(myViaje)


        // Upload image and obtain id
        lateinit var imageRef: StorageReference
        myViaje.idViaje.let { id ->
            imageRef = storageRef.child("images/${id}")
            imageRef.putBytes(imageBytes, storageMetadata {
                contentType = "image/jpg"
            })
                .addOnFailureListener { e ->
                    Log.e(TAG, "handleTravelCreation: Couldnt upload image", e)
                }
                .addOnSuccessListener {
                    Log.d(TAG, "handleTravelCreation: Uploaded file ${it.uploadSessionUri}")
                }
        }.await()

        // Update imageRef in the viaje entry
        viajeRef.update("imageBitmapStoragePath", imageRef.downloadUrl.await())
    }
}

