package kg.bff.client.data.model


data class User(
    val name: String = "",
    val phone: String = "",
    val birthdate: String = "" ,
    val image: Image?=null
)