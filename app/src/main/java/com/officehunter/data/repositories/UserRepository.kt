package com.officehunter.data.repositories

import android.util.Log
import com.officehunter.data.remote.FirebaseAuthRemote
import com.officehunter.data.remote.firestore.Firestore
import com.officehunter.data.remote.firestore.FirestoreCollection
import com.officehunter.data.remote.firestore.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
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

     suspend fun updateData() {
        firestore.read(FirestoreCollection.USERS){
            result ->
                result.onSuccess {
                    val updatedUsers = it.map { document -> User.fromQueryDocumentSnapshot(document) }
                    runBlocking{ emitUpdatedData(updatedUsers) }
                }
        }
    }

    suspend fun login(email: String, password: String, onError: (error: String?)->Unit){
        authRemote.login(email,password){
            result ->  result.onSuccess {
                runBlocking { updateData() }
            }.onFailure { onError(it.message)}
        }
    }

     fun signUp(name: String, surname: String, email: String, password: String, onError: (error: String?) -> Unit){
        authRemote.createUser(email,password){
            result ->  result.onSuccess {
                if(it.user != null){
                    createNewUserDocument(it.user!!.uid,name,surname)
                } else {
                    onError("Signup failed")
                }
            }.onFailure { onError(it.message) }
        }
    }

    fun logout(){
        authRemote.logout()
    }

    private fun createNewUserDocument(id: String, name: String, surname: String){
        val newUser = User(
            id = id,
            name = name,
            surname = surname,
        )
        firestore.upsert(FirestoreCollection.USERS,id,newUser){
            result ->  result.onSuccess {
                Log.d(TAG,"User created ")
            }.onFailure {
                Log.d(TAG, "User creation Fail")
            }
        }
    }

    private suspend fun emitUpdatedData(newUsers: List<User>){
        Log.d(TAG,"emit newUsers")
        val currentUserId = authRemote.currentUser?.uid
        val currentUserData = newUsers.find { user->user.id == currentUserId }
        userRepositoryData.emit(userRepositoryData.value.copy(currentUser = currentUserData, usersList = newUsers))
    }

    companion object{
        const val TAG = "UserRepository"
    }
}
