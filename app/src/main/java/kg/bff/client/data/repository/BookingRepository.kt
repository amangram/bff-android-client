package kg.bff.client.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kg.bff.client.data.model.Booking
import kg.bff.client.data.model.BookingRequest
import kg.bff.client.data.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class BookingRepository(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    private val userId = auth.currentUser?.uid
    private val stadiumBookingCollectionRef: CollectionReference
        get() = this.firestore.collection("bookingTable")
    private val stadiumBookingRequestCollectionRef: CollectionReference
        get() = this.firestore.collection("bookingRequest")

    fun getBookingTable(stadiumId: String) = flow<State<Booking>> {
        emit(State.loading())
        val snapshot =
            stadiumBookingCollectionRef.whereEqualTo("stadiumId", stadiumId).get().await()
        val booking = snapshot.toObjects(Booking::class.java)
        emit(State.success(booking.first()))
    }.catch {
        emit(State.failed(this.toString()))
    }.flowOn(Dispatchers.IO)

    fun getWaitedRequests() = flow<State<List<BookingRequest>>> {
        emit(State.loading())
        val snapshots = stadiumBookingRequestCollectionRef.whereEqualTo("userId", userId)
            .whereEqualTo("status", "waiting").get().await()
        Log.d("TAGht", "${snapshots.size()} ")
        emit(State.success(snapshots.toObjects(BookingRequest::class.java)))
        val bookingList: List<BookingRequest> = snapshots.toObjects(BookingRequest::class.java)

        Log.d("TAGht", "list ${bookingList.size} ")
        emit(State.success(snapshots.toObjects(BookingRequest::class.java)))
    }.catch {
        emit(State.failed(this.toString()))
    }.flowOn(Dispatchers.IO)

    fun getActiveRequests() = flow<State<List<BookingRequest>>> {
        emit(State.loading())
        val snapshots = stadiumBookingRequestCollectionRef.whereEqualTo("userId", userId)
            .whereEqualTo("status", "active").get().await()
        val bookingList = snapshots.toObjects(BookingRequest::class.java)
        emit(State.success(bookingList))
    }.catch {
        emit(State.failed(this.toString()))
    }.flowOn(Dispatchers.IO)

    fun getRejectedRequests() = flow<State<List<BookingRequest>>> {
        emit(State.loading())
        val snapshots = stadiumBookingRequestCollectionRef.whereEqualTo("userId", userId)
            .whereEqualTo("status", "rejected").get().await()
        val bookingList = snapshots.toObjects(BookingRequest::class.java)
        emit(State.success(bookingList))
    }.catch {
        emit(State.failed(this.toString()))
    }.flowOn(Dispatchers.IO)

    fun addBookingRequest(request: BookingRequest) =
        flow<State<DocumentReference>> {
            emit(State.loading())
            val requestRef = stadiumBookingRequestCollectionRef.add(request).await()
            stadiumBookingRequestCollectionRef.document(requestRef.id)
                .update("id", requestRef.id).await()
            emit(State.success(requestRef))
        }.catch {
            emit(State.failed(this.toString()))
        }.flowOn(Dispatchers.IO)

    fun deleteRequest(id: String) = flow<State<String>> {
        emit(State.loading())
        val delete = stadiumBookingRequestCollectionRef.document(id).delete().await()
        emit(State.success("delete"))
    }.catch {
        emit(State.failed(this.toString()))
    }.flowOn(Dispatchers.IO)
}