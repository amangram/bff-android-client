package kg.bff.client.ui.stadium.detail

import android.util.Log
import androidx.lifecycle.*
import kg.bff.client.cancelIfActive
import kg.bff.client.data.model.Admin
import kg.bff.client.data.model.Stadium
import kg.bff.client.data.model.State
import kg.bff.client.data.repository.StadiumsRepository
import kg.bff.client.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StadiumDetailViewModel(
    private val stadiumId: String,
    private val stadiumRepository: StadiumsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var getAdminJob: Job? = null
    private val admin by lazy { MutableLiveData<State<Admin>>() }
    private val _stadium =
        stadiumRepository.getStadium(stadiumId).asLiveData(viewModelScope.coroutineContext)
    val stadium: LiveData<State<Stadium>>
        get() = _stadium

    fun setAdmin(adminId: String) {
        getAdminJob.cancelIfActive()
        Log.d("EWrT", "setAdmin:")
        getAdminJob = viewModelScope.launch {
            userRepository.getAdmin(adminId).collect {
                admin.value = it
            }
        }
    }

    fun save(id: String, savedUsers: List<String>) = stadiumRepository.save(id,savedUsers)

    fun getAdmin()= admin as LiveData<State<Admin>>

}