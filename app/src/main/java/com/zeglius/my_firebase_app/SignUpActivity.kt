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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.*
import com.google.firebase.ktx.Firebase
import com.zeglius.my_firebase_app.ui.component.FirebaseLoginFields
import com.zeglius.my_firebase_app.ui.theme.My_Firebase_AppTheme

private const val TAG = "SignUpActivity"

class SignUpActivity : ComponentActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            val context = LocalContext.current
            My_Firebase_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignUpContent(onSuccessListener = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                        handleSuccess()
                    }
                    ) { e -> handleFailure(e) }
                }
            }
        }
    }


    private fun handleFailure(e: Exception) {
        Toast.makeText(
            this,
            "Couldn't signup: ${e.localizedMessage}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}

@Composable
private fun SignUpContent(
    onSuccessListener: (Any) -> Unit,
    onFailureListener: (Exception) -> Unit,
) {
    FirebaseLoginFields(
        buttonText = { Text(text = stringResource(R.string.sign_up)) },
        onLoginOrSignUp = { email, password ->
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener)
        }
    )
}

