package com.zeglius.my_firebase_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zeglius.my_firebase_app.ui.component.CreateViajeDialog
import com.zeglius.my_firebase_app.ui.component.TextHeader
import com.zeglius.my_firebase_app.ui.component.ViajeItem
import com.zeglius.my_firebase_app.ui.model.Viaje
import com.zeglius.my_firebase_app.ui.theme.My_Firebase_AppTheme

class TravelListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            My_Firebase_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TravelListContent {
                        Firebase.auth.signOut()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TravelListContent(onSignOut: () -> Unit) {
    @Suppress("UNUSED_VARIABLE")
    val coroutineScope = rememberCoroutineScope()
    var isCreateViajeVisible by rememberSaveable { mutableStateOf(false) }
    val db = Firebase.firestore
    var viajesList by remember { mutableStateOf<List<Viaje>>(emptyList()) }
    val collectionReference = db.collection("viajes")
        .document(Firebase.auth.currentUser!!.email!!)
        .collection("userViajes")
    var selectedViaje: Viaje? by remember { mutableStateOf(null) }

    @Suppress("UNUSED_VARIABLE")
    val valueEventListener by rememberUpdatedState {
        collectionReference.addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                val items = mutableListOf<Viaje>()
                for (viaje in it.toObjects(Viaje::class.java)) {
                    viaje?.let { items.add(viaje) }
                }
                viajesList = items
            }
        }
    }

    SideEffect {
        collectionReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            snapshot?.let {
                val items = mutableListOf<Viaje>()
                for (viaje in it.toObjects(Viaje::class.java)) {
                    viaje?.let { items.add(viaje) }
                }
                viajesList = items
            }


        }

    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onSignOut,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Log out")
                    Text(text = stringResource(R.string.log_out))
                }
            }
        }
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))
            TextHeader(text = stringResource(R.string.my_travel_list))
            Spacer(modifier = Modifier.height(20.dp))


            CreateViajeDialog(
                isVisible = isCreateViajeVisible,
                fieldsContents = selectedViaje,
                onDismissRequest = { isCreateViajeVisible = false; selectedViaje = null })

            Column(Modifier.padding(10.dp)) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viajesList) { viaje ->
                        ViajeItem(viaje) { viaje -> selectedViaje = viaje; isCreateViajeVisible = true }
                    }
                }
                Button(
                    onClick = { isCreateViajeVisible = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.create_travel))
                }
            }
        }
    }

}


