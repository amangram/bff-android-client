package kg.bff.client.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookingRequest(
    val id: String = "",
    val userId: String = "",
    val stadiumId: String = "",
    val stadiumName: String = "",
    val adminId: String = "",
    val date: String = "",
    val time: List<String> = emptyList(),
    val status: String = ""
) : Parcelable