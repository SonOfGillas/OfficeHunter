package com.officehunter.data.repositories

import android.util.Log
import com.officehunter.data.remote.firestore.Firestore
import com.officehunter.data.remote.firestore.FirestoreCollection

class UserRepository(
    private val firestore: Firestore
) {
//    val users: Flow<List<User>> = userDAO.getAllUser()

    fun getUsers() {
        Log.d(TAG,"getUsers()")
        firestore.read(FirestoreCollection.USERS){
            it.map { result ->
                for (user in result){
                    Log.d(TAG, "${user.id} => ${user.data}")
                }
            }
        }
    }

    companion object{
        const val TAG = "UserRepository"
    }

}
