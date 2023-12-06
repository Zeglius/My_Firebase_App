package com.zeglius.my_firebase_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zeglius.my_firebase_app.ui.component.FirebaseLoginFields
import com.zeglius.my_firebase_app.ui.theme.My_Firebase_AppTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            My_Firebase_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginContent(
                        onSucessListener = {
                            startActivity(Intent(this, TravelListActivity::class.java))
                            finish()
                        },
                        onFailureListener = { e -> handleFailure(e) }
                    )
                }
            }
        }
    }

    private fun handleFailure(e: Exception) {
        Toast.makeText(
            this,
            "Couldn't login: ${e.localizedMessage}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
private fun LoginContent(onSucessListener: (Any) -> Unit, onFailureListener: (Exception) -> Unit) {
    FirebaseLoginFields(
        buttonText = {
            Text(text = stringResource(R.string.login))
        },
        onLoginOrSignUp = { email, password ->
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSucessListener)
                .addOnFailureListener(onFailureListener)
        }
    )
}

