package com.officehunter.data.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class FirebaseAuthRemote {
    private var auth: FirebaseAuth = Firebase.auth
    var currentUser: FirebaseUser? = auth.currentUser
        private set

    fun userIsLogged(): Boolean {
        return  currentUser != null
    }

    fun login(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                currentUser = auth.currentUser
                onResult(Result.success(Unit))
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                onResult(Result.failure(task.exception ?: Exception("Unknown error")))
            }
        }
    }

    fun logout(){
        auth.signOut()
        currentUser = auth.currentUser
    }

    fun createUser(email: String, password: String, onResult: (Result<AuthResult>) -> Unit){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    onResult(Result.success(task.result))
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onResult(Result.failure(task.exception ?: Exception("Unknown error")))
                }
        }
    }

    companion object {
        private const val TAG = "FirebaseAuthenticator"
    }
}