package kg.bff.client.data.model

import com.google.firebase.firestore.GeoPoint

data class Stadium(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val images: List<Image> = emptyList(),
    val location: GeoPoint? = null,
    val price: Int = 0 ,
    val userId: String = "",
    val saved: List<String> = emptyList()
)
