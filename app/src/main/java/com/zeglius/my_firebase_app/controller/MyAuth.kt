package com.zeglius.my_firebase_app.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Deprecated("Just use Firebase.auth singleton")
object MyAuth {
    fun signUp(email: String, password: String, auth: FirebaseAuth? = null): Boolean {
        var result = false
        (auth ?: Firebase.auth).createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result = true
            }.addOnFailureListener {
                result = false
            }
        return result
    }

    fun login(email: String, password: String, auth: FirebaseAuth? = null): Boolean {
        var result = false
        (auth ?: Firebase.auth).signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result = true }
            .addOnFailureListener { result = false }
        return result
    }
}