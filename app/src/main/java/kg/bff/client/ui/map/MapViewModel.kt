package kg.bff.client.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kg.bff.client.data.model.Stadium
import kg.bff.client.data.model.State
import kg.bff.client.data.repository.StadiumsRepository

class MapViewModel(private val repository: StadiumsRepository) : ViewModel() {

    private val mStadiums = repository.getStadiums().asLiveData(viewModelScope.coroutineContext)

    val stadiums: LiveData<State<List<Stadium>>>
        get() = mStadiums
}