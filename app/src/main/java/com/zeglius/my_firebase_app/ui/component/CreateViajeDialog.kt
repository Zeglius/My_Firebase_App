package com.zeglius.my_firebase_app.ui.component

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storageMetadata
import com.zeglius.my_firebase_app.ui.model.Viaje
import com.zeglius.my_firebase_app.ui.theme.My_Firebase_AppTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateViajeDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitMap = remember { mutableStateOf<Bitmap?>(null) }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri -> imageUri = uri })
    val coroutineScope = rememberCoroutineScope()



    if (isVisible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = true)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(15.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Create travel",
                    modifier = Modifier.align(Alignment.Start),
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = origen,
                    onValueChange = { origen = it },
                    isError = origen.isBlank(),
                    placeholder = {
                        Text(text = "origen")
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = destino,
                    isError = destino.isBlank(),
                    onValueChange = { destino = it },
                    placeholder = {
                        Text(text = "destino")
                    }
                )

                imageUri?.let {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    coroutineScope.launch {
                        bitMap.value =
                            Bitmap.createScaledBitmap(
                                ImageDecoder.decodeBitmap(source),
                                200,
                                200,
                                true
                            )
                    }
                }

                bitMap.value?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                }

                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text(text = "Select image")
                }

                Row(Modifier.align(Alignment.End)) {
                    TextButton(
                        onClick = {
                            if (bitMap.value != null)
                                coroutineScope.launch {
                                    handleTravelCreation(
                                        origen,
                                        destino,
                                        bitMap.value!!
                                    )
                                }
                            onDismissRequest()
                        },
                        enabled = origen.isNotBlank() && destino.isNotBlank() && bitMap.value != null
                    ) {
                        Text(text = "Save travel")
                    }
                }

            }
        }
    }

}

private const val TAG = "CreateViajeDialog"

private suspend fun handleTravelCreation(
    origen: String,
    destino: String,
    bitmap: Bitmap,
) {
    val storageRef = Firebase.storage.reference
    val email = Firebase.auth.currentUser!!.email
    val userViajesRef =
        Firebase.firestore.collection("viajes").document(email!!).collection("userViajes")

    // Get image bytes
    var stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val imageBytes = stream.toByteArray()


    // Create viaje
    var myViaje = Viaje(origen = origen, destino = destino)


    // Upload viaje
    var viajeId: String? = null
    var viajeRef: DocumentReference? = null
    userViajesRef.add(myViaje).addOnSuccessListener {
        viajeId = it.id
        viajeRef = it
        it.update("idViaje", it.id)
    }.await()

    // Upload image and obtain id
    var imageRef: StorageReference? = null
    viajeId?.let { id ->
        imageRef = storageRef.child("images/${id}")
        imageRef!!.putBytes(imageBytes, storageMetadata {
            contentType = "image/jpg"
        })
            .addOnFailureListener { e ->
                Log.e(TAG, "handleTravelCreation: Couldnt upload image", e)
            }
            .addOnSuccessListener {
                Log.d(TAG, "handleTravelCreation: Uploaded file ${it.uploadSessionUri}")
            }
    }

    // Update imageRef in the viaje entry
    viajeRef?.update("imageBitmapStoragePath", imageRef!!.path)
}


@Preview(showSystemUi = true)
@Composable
private fun CreateViajeDialogPreview() {
    My_Firebase_AppTheme {
        CreateViajeDialog(isVisible = true, onDismissRequest = {})
    }
}