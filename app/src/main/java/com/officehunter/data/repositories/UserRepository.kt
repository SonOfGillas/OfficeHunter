package com.officehunter.data.repositories

import android.util.Log
import com.officehunter.data.remote.firestore.Firestore
import com.officehunter.data.remote.firestore.FirestoreCollection
import com.officehunter.data.remote.firestore.dao.UserDAO
import com.officehunter.data.remote.firestore.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class UserRepository(
    private val firestore: Firestore,
) {
    val usersList = MutableStateFlow<List<User>>(emptyList<User>())

     suspend fun getUsers() {
        firestore.read(FirestoreCollection.USERS){
            result ->
                result.onSuccess {
                    val updatedUsers = it.map { document -> User.fromQueryDocumentSnapshot(document) }
                    runBlocking{ _emitUpdatedUsers(updatedUsers) }
                }
        }
    }

    suspend fun _emitUpdatedUsers(newUsers: List<User>){
        Log.d(TAG,"emit newUsers")
        usersList.emit(newUsers)
    }

    companion object{
        const val TAG = "UserRepository"
    }

}
