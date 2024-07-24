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
    private val authRepository: FirebaseAuthRemote,
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

    private suspend fun emitUpdatedData(newUsers: List<User>){
        Log.d(TAG,"emit newUsers")
        val currentUserId = authRepository.currentUser?.uid
        val currentUserData = newUsers.find { user->user.id == currentUserId }
        userRepositoryData.emit(userRepositoryData.value.copy(currentUser = currentUserData, usersList = newUsers))
    }

    companion object{
        const val TAG = "UserRepository"
    }
}
