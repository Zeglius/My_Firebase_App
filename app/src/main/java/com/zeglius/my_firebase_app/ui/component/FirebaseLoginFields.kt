package com.zeglius.my_firebase_app.ui.component

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.zeglius.my_firebase_app.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FirebaseLoginFields(
    buttonText: @Composable() (RowScope.() -> Unit),
    onLoginOrSignUp: (email: String, password: String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    fun isEmailValid() =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() /*&& email.isNotEmpty()*/

    fun isPasswordValid() = password.length > 6 /*&& password.isNotEmpty()*/


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = !isEmailValid() && email.isNotEmpty(),
            supportingText = {
                if (!isEmailValid() && email.isNotEmpty()) Text(
                    text = stringResource(
                        R.string.invalid_email
                    )
                )
            },
            label = { Text(text = stringResource(R.string.email)) }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it.trim() },
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    if (showPass) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = null)
                    } else {
                        Icon(Icons.Default.Visibility, contentDescription = null)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = !isPasswordValid() && password.isNotEmpty(),
            supportingText = {
                if (!isPasswordValid() && password.isNotEmpty())
                    Text(text = stringResource(R.string.invalid_password))
            },
            visualTransformation = if (showPass) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            label = { Text(text = stringResource(R.string.password)) }
        )


        Button(
            onClick = { onLoginOrSignUp(email, password) },
            enabled = isEmailValid() && isPasswordValid(),
            content = buttonText
        )
    }
}