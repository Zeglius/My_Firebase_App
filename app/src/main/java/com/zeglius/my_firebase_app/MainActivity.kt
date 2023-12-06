package com.zeglius.my_firebase_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.contextaware.withContextAvailable
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.withCreated
import androidx.lifecycle.withStarted
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zeglius.my_firebase_app.ui.component.TextHeader
import com.zeglius.my_firebase_app.ui.theme.My_Firebase_AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        setContent {
            My_Firebase_AppTheme(dynamicColor = false, darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Content()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser != null) {
            startActivity(Intent(this, TravelListActivity::class.java))
            finish()
        }
        /*
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // TODO: Switch to TravellListActivity
            // this.startActivity(Intent(this, ))
        }
        */
    }

    @Composable
    fun Content() {
        val context = LocalContext.current




        fun goSignUp(context: Context) {
            startActivity(Intent(context, SignUpActivity::class.java))
        }

        fun goLogin(context: Context) {
            startActivity(Intent(context, LoginActivity::class.java))
        }


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            TextHeader(text = stringResource(R.string.app_name))

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    goLogin(context)
                }) {
                Text(text = stringResource(R.string.login))
            }
            Button(
                onClick = {
                    goSignUp(context)
                }) {
                Text(text = stringResource(id = R.string.sign_up))
            }
        }

    }


    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    private fun GreetingPreview() {
        My_Firebase_AppTheme {
            Content()
        }
    }
}

