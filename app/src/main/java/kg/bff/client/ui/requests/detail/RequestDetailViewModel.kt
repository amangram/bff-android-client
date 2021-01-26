package kg.bff.client.ui.requests.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kg.bff.client.data.repository.BookingRepository

class RequestDetailViewModel(private val repository: BookingRepository): ViewModel() {

    fun deleteRequest(id: String) = repository.deleteRequest(id).asLiveData(viewModelScope.coroutineContext)
}