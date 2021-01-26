package kg.bff.client.ui.stadium.list

import android.location.Location
import androidx.lifecycle.*
import kg.bff.client.data.model.Stadium
import kg.bff.client.data.model.State
import kg.bff.client.data.repository.StadiumsRepository

class StadiumListViewModel(private val stadiumRepository: StadiumsRepository) : ViewModel() {
    private val nearByStadiums by lazy { MutableLiveData<List<Stadium>>() }
    private val mStadiums = stadiumRepository.getStadiums().asLiveData(viewModelScope.coroutineContext)
    val stadiumsList: LiveData<State<List<Stadium>>>
        get() = mStadiums

    fun setLocation(userLocation: Location) {
        stadiumRepository.getNearBy(userLocation){
            nearByStadiums.value= it
        }
    }
    fun getLiveData() = nearByStadiums as LiveData<List<Stadium>>

    fun save(id: String, savedUsers: List<String>) = stadiumRepository.save(id,savedUsers)

}