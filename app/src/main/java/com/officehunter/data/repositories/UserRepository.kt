package com.officehunter.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.officehunter.data.database.Achievement
import com.officehunter.data.entities.WorkRoles
import com.officehunter.data.entities.getRoleName
import com.officehunter.data.remote.FirebaseAuthRemote
import com.officehunter.data.remote.firestore.Firestore
import com.officehunter.data.remote.firestore.FirestoreCollection
import com.officehunter.data.remote.firestore.entities.Found
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

    fun updateUserPoints(newAchievements: List<Achievement>,onResult: (Result<Unit>) -> Unit){
        val currentUser = userRepositoryData.value.currentUser
        if(currentUser!=null){
            val points = newAchievements.fold(0){acc, achievement -> acc + achievement.pointValue  }
            val updatedUser = currentUser.copy(points = currentUser.points.toInt()+points)
            firestore.upsert(FirestoreCollection.USERS,updatedUser.id,updatedUser.toDocument()){
                onResult(Result.success(Unit))
            }
        }
    }

    private fun createNewUserDocument(id: String, name: String, surname: String,onResult: (Result<Unit>) -> Unit){
        val newUser = User(
            id = id,
            name = name,
            surname = surname,
            points = 1,
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
