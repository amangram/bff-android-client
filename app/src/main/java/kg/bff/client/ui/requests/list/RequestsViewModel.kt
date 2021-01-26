package kg.bff.client.ui.requests.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kg.bff.client.data.model.BookingRequest
import kg.bff.client.data.model.State
import kg.bff.client.data.repository.BookingRepository

class RequestsViewModel(private val repository: BookingRepository) : ViewModel() {

    private val _waitingList = repository.getWaitedRequests().asLiveData(viewModelScope.coroutineContext)
    val waitingList : LiveData<State<List<BookingRequest>>>
    get() = _waitingList
    private val _activeList = repository.getActiveRequests().asLiveData(viewModelScope.coroutineContext)
    val activeList : LiveData<State<List<BookingRequest>>>
        get() = _activeList
    private val _rejectList = repository.getRejectedRequests().asLiveData(viewModelScope.coroutineContext)
    val rejectList : LiveData<State<List<BookingRequest>>>
        get() = _rejectList
}