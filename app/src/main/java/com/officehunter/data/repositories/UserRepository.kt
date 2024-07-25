package com.officehunter.data.repositories

import android.util.Log
import com.officehunter.data.remote.FirebaseAuthRemote
import com.officehunter.data.remote.firestore.Firestore
import com.officehunter.data.remote.firestore.FirestoreCollection
import com.officehunter.data.remote.firestore.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

data class UserRepositoryData (
    val currentUser: User? = null,
    val usersList: List<User> = emptyList<User>()
)

class UserRepository(
    private val firestore: Firestore,
    private val authRemote: FirebaseAuthRemote,
) {
    val userRepositoryData = MutableStateFlow<UserRepositoryData>(UserRepositoryData())

    fun updateData(onResult: (Result<Unit>) -> Unit){
        firestore.read(FirestoreCollection.USERS){
            result ->
                result.onSuccess {
                    val updatedUsers = it.map { document -> User.fromQueryDocumentSnapshot(document) }
                    emitUpdatedData(updatedUsers)
                    onResult(Result.success(Unit))
                }.onFailure{ onResult(Result.failure(it)) }
        }
    }

     fun login(email: String, password: String, onResult: (Result<Unit>) -> Unit){
        authRemote.login(email,password){
            result ->  result
                .onSuccess { updateData(onResult) }
                .onFailure { exception ->  onResult(Result.failure(exception)) }
        }
    }

     fun signUp(name: String, surname: String, email: String, password: String, onResult: (Result<Unit>) -> Unit){
        authRemote.createUser(email,password){
            result ->  result.onSuccess {
                if(it.user != null){
                    createNewUserDocument(it.user!!.uid,name,surname,onResult)
                } else {
                    onResult(Result.failure(Exception("Signup failed")))
                }
            }.onFailure { exception -> onResult(Result.failure(exception)) }
        }
    }

    fun logout(){
        authRemote.logout()
    }

    fun userIsLogged(): Boolean{
        return authRemote.userIsLogged()
    }

    private fun createNewUserDocument(id: String, name: String, surname: String,onResult: (Result<Unit>) -> Unit){
        val newUser = User(
            id = id,
            name = name,
            surname = surname,
        )
        firestore.upsert(FirestoreCollection.USERS,id,newUser){
            result ->  result
                .onSuccess { updateData(onResult) }
                .onFailure { onResult(Result.failure(it)) }
        }
    }

    private fun emitUpdatedData(newUsers: List<User>){
        val currentUserId = authRemote.currentUser?.uid
        val currentUserData = newUsers.find { user->user.id == currentUserId }
        userRepositoryData.update { it.copy(currentUser = currentUserData, usersList = newUsers) }
    }

    companion object{
        const val TAG = "UserRepository"
    }
}
