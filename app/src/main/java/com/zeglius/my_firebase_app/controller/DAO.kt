package com.zeglius.my_firebase_app.controller

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storageMetadata
import com.zeglius.my_firebase_app.ui.model.Viaje
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

const val TAG: String = "DAO"

object DAO {
    private val email: String by lazy { Firebase.auth.currentUser!!.email!! }

    suspend fun createTravel(
        origen: String,
        destino: String,
        bitmap: Bitmap,
    ) {
        val storageRef = Firebase.storage.reference
        if (Firebase.auth.currentUser == null) return
        val userViajesCollection =
            Firebase.firestore.collection("viajes").document(email).collection("userViajes")

        // Get image bytes
        val imageBytes = bitmap.toByteArray()


        // Create viaje
        var myViaje = Viaje(origen = origen, destino = destino)


        // Upload viaje
        val viajeRef: DocumentReference = userViajesCollection.document()
        myViaje = myViaje.copy(idViaje = viajeRef.id)

        viajeRef.set(myViaje)


        uploadImageToViaje(viajeRef, imageBytes)

    }

    private fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    // Upload image
    private fun uploadImageToViaje(
        viajeRef: DocumentReference,
        imageBytes: ByteArray,
    ): Uri? {
        val storageRef = Firebase.storage.reference
        lateinit var imageRef: StorageReference
        var bitmapUrl: Uri? = null
        viajeRef.id.let { id ->
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
        }.onSuccessTask {
            // Update imageRef in the viaje entry
            val downloadUrl: Uri?
            runBlocking {
                downloadUrl = imageRef.downloadUrl.await()
                bitmapUrl = downloadUrl
            }

            viajeRef.update("imageBitmapStoragePath", downloadUrl)
        }
        return bitmapUrl
    }

    suspend fun updateTravel(viaje: Viaje, bitMap: Bitmap) = runBlocking {
        val viajeRef = Firebase.firestore.collection("viajes")
            .document(email)
            .collection("userViajes")
            .document(viaje.idViaje!!)

        viajeRef
            .set(
                viaje,
                SetOptions.mergeFields(listOf("origen", "destino"))
            ).await()


        uploadImageToViaje(viajeRef, bitMap.toByteArray())
    }

}

