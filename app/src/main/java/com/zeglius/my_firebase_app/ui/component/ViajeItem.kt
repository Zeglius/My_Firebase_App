package com.zeglius.my_firebase_app.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.zeglius.my_firebase_app.ui.model.Viaje

@Composable
fun ViajeItem(viaje: Viaje, onClick: (Viaje) -> Unit = {}) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(viaje) })
    {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            SubcomposeAsyncImage(
                model = viaje.imageBitmapStoragePath,
                contentDescription = null
            ) {
                val state = painter.state
                if (state !is AsyncImagePainter.State.Success) {
                    CircularProgressIndicator()
                } else {
                    SubcomposeAsyncImageContent()
                }
            }


            Column(Modifier.padding(8.dp)) {
                Text(text = "Viaje id: ${viaje.idViaje}")
                Text(text = "Origen: ${viaje.origen}")
                Text(text = "Origen: ${viaje.destino}")
            }
        }
    }
}