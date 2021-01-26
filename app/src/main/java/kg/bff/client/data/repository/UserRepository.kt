package kg.bff.client.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kg.bff.client.data.model.Admin
import kg.bff.client.data.model.Image
import kg.bff.client.data.model.State
import kg.bff.client.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class UserRepository(private val firestore: FirebaseFirestore) {

    private val TAG = this::class.java.simpleName
    private val currentUserDocumentReference: DocumentReference
        get() = firestore.document(
            "users/${FirebaseAuth.getInstance().uid
                ?: throw NullPointerException("UID is null.")}"
        )
    private val adminCollectionReference: CollectionReference
        get() = firestore.collection("admins")

    fun getUser() = flow<State<User>> {
        emit(State.loading())
        val snapshot = currentUserDocumentReference.get().await()
        val user = snapshot.toObject(User::class.java)
        user?.let { emit(State.success(user)) }
    }.catch {
        emit(State.failed(this.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAdmin(adminId: String) = flow<State<Admin>> {
        emit(State.loading())
        val snapshot = adminCollectionReference.document(adminId).get().await()
        Log.d("EWrT", "$adminId repo: ${snapshot.id}")
        snapshot.toObject(Admin::class.java)?.let {
            emit(State.success(it))
            Log.d("EWrT", "$adminId repo: ${it.name}")
        }
    }.catch {
        Log.e(TAG, this.toString())
        emit(State.failed(this.toString()))
    }.flowOn(Dispatchers.IO)

    fun addToDataBase(user: User) {
        currentUserDocumentReference.get().addOnSuccessListener { userSnapshot ->
            if (!userSnapshot.exists()) {
                currentUserDocumentReference.set(user)
            }
        }
    }

    fun updateCurrentUser(
        name: String = "",
        birthdate: String = "",
        image: Image? = null
    ) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (birthdate.isNotBlank()) userFieldMap["birthdate"] = birthdate
        if (image != null) userFieldMap["image"] = image
        currentUserDocumentReference.update(userFieldMap)
    }
}