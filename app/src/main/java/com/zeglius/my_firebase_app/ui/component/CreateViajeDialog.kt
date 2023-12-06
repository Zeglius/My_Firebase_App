package com.zeglius.my_firebase_app.ui.component

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
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
import com.zeglius.my_firebase_app.controller.DAO
import com.zeglius.my_firebase_app.ui.model.Viaje
import com.zeglius.my_firebase_app.ui.theme.My_Firebase_AppTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateViajeDialog(
    isVisible: Boolean,
    fieldsContents: Viaje? = null,
    onDismissRequest: () -> Unit,
) {
    val isUpdateTransaction = run { fieldsContents != null }

    val context = LocalContext.current
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitMap = remember { mutableStateOf<Bitmap?>(null) }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri -> imageUri = uri })
    val coroutineScope = rememberCoroutineScope()


    if (fieldsContents != null) {
        origen = fieldsContents.origen!!
        destino = fieldsContents.destino!!
    }


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
                    text = if(!isUpdateTransaction)"Create travel" else "Update travel",
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
                            if (bitMap.value != null) {
                                coroutineScope.launch {
                                    if (isUpdateTransaction && fieldsContents != null) {
                                        var auxViaje = fieldsContents.copy()
                                        origen?.let { auxViaje = auxViaje.copy(origen = it) }
                                        destino?.let { auxViaje = auxViaje.copy(destino = destino) }
                                        DAO.updateTravel(auxViaje, bitMap.value!!)
                                    } else {
                                        DAO.createTravel(
                                            origen,
                                            destino,
                                            bitMap.value!!
                                        )
                                    }

                                }
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


@Preview(showSystemUi = true)
@Composable
private fun CreateViajeDialogPreview() {
    My_Firebase_AppTheme {
        CreateViajeDialog(isVisible = true, onDismissRequest = {})
    }
}