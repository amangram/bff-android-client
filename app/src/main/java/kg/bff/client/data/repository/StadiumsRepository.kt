package kg.bff.client.data.repository

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ckdroid.geofirequery.GeoQuery
import com.ckdroid.geofirequery.model.Distance
import com.ckdroid.geofirequery.utils.BoundingBoxUtils
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kg.bff.client.data.model.Stadium
import kg.bff.client.data.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class StadiumsRepository(private val firestore: FirebaseFirestore) {

    private val TAG = this::class.java.simpleName

    private val stadiumsCollectionRef: CollectionReference
        get() = this.firestore.collection("stadiums")
    private val distance = Distance(2.0, BoundingBoxUtils.DistanceUnit.KILOMETERS)
    private val goeQuery: GeoQuery
        get() = GeoQuery().collection("stadiums")

    fun getStadiums() = flow<State<List<Stadium>>> {
        emit(State.loading())
        val snapshots = stadiumsCollectionRef.get().await()
        val stadiums = snapshots.toObjects(Stadium::class.java)
            .mapIndexed { index, stadium -> stadium.copy(id = snapshots.documents[index].id) }
        emit(State.success(stadiums))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    fun getNearBy(userLocation: Location, onSuccess: (List<Stadium>) -> Unit) {
        goeQuery.whereNearToLocation(userLocation, distance).get()
            .addOnCompleteListener { exception, snapshotList, list2 ->
                val stadiums = mutableListOf<Stadium>()
                for (s in snapshotList) {
                    s.toObject(Stadium::class.java)?.let { stadiums.add(it) }
                }
                onSuccess(stadiums)
            }
    }

    fun getStadium(stadiumId: String) = flow<State<Stadium>> {
        emit(State.loading())
        val snapshot = stadiumsCollectionRef.document(stadiumId).get().await()
        val stadium = snapshot.toObject(Stadium::class.java)
        stadium?.let { emit(State.success(it)) }
    }.catch {
        emit(State.failed(this.toString()))
    }.flowOn(Dispatchers.IO)

    fun getSavedStadiums(id: String) = flow<State<List<Stadium>>> {
        emit(State.loading())
        val snapshots = stadiumsCollectionRef.whereArrayContains("saved", id).get().await()
        Log.d(TAG, "getSavedStadiums: ${snapshots.size()}")
        val stadiums = snapshots.toObjects(Stadium::class.java)
        emit(State.success(stadiums))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun save(id: String, savedUsers: List<String>): LiveData<State<String>> {
        val state = MutableLiveData<State<String>>()
        state.value = State.loading()
        stadiumsCollectionRef.document(id).update("saved", savedUsers)
            .addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    state.value = State.success("success")
                } else {
                    state.value = State.failed("error")
                }
            }
        return state
    }
}
