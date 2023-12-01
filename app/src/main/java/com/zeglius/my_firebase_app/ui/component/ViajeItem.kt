package com.zeglius.my_firebase_app.ui.component

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.zeglius.my_firebase_app.R
import com.zeglius.my_firebase_app.ui.model.Viaje
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("NewApi")
@Composable
@Deprecated("Use a more parametriced way to show the image")
fun ViajeItem(viaje: Viaje) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var coroutineScope = rememberCoroutineScope()


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            viaje.imageBitmapStoragePath?.let {
                coroutineScope.launch {
                    val storageReference = Firebase.storage.reference
                    val imageRef = storageReference.child("${viaje.imageBitmapStoragePath}")
                    imageUri = imageRef.downloadUrl.await()
                }
            }


            imageUri?.let { uri ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .crossfade(false)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }


            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = stringResource(R.string.travel_id, viaje.idViaje ?: ""),
                )
                Text(text = stringResource(R.string.start_point, viaje.origen ?: ""))
                Text(text = stringResource(R.string.destination, viaje.destino ?: ""))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ViajeItemPreview() {
    val viaje = Viaje("001", "Murcia", "Ohio")

    Box(/*Modifier.padding(40.dp)*/) {
        ViajeItem(viaje = viaje)
    }
}