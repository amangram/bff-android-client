package kg.bff.client.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kg.bff.client.data.model.Image
import java.util.*
import kotlin.collections.ArrayList

class StorageRepository(private val storage: FirebaseStorage) {

    private val currentUserRef: StorageReference
        get() = storage.reference
            .child(
                "profileImages/${FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")}"
            )

    fun uploadProfilePhoto(
        imageBytes: Array<ByteArray>,
        onSuccess: (imagePath: Image) -> Unit
    ) {
        val imageUrls: ArrayList<String> = ArrayList()
        val ref = arrayOf(
            currentUserRef.child("${UUID.nameUUIDFromBytes(imageBytes[0])}original"),
            currentUserRef.child("${UUID.nameUUIDFromBytes(imageBytes[1])}preview")
        )
        for (i in 0..1) {
            ref[i].putBytes(imageBytes[i])
                .addOnSuccessListener {
                    ref[i].downloadUrl.addOnSuccessListener {
                        imageUrls.add(it.toString())
                        if (i == 1) {
                            onSuccess(Image(imageUrls.first(), imageUrls.last()))
                        }
                    }

                }
        }
    }
}