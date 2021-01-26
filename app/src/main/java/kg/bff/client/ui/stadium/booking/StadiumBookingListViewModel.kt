package kg.bff.client.ui.stadium.booking

import androidx.lifecycle.*
import kg.bff.client.data.model.BookingRequest
import kg.bff.client.data.model.Date
import kg.bff.client.data.model.State
import kg.bff.client.data.repository.BookingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StadiumBookingListViewModel(
    private val mStadiumId: String,
    private var mDate: String,
    private val repository: BookingRepository
) :
    ViewModel() {

    private val bookingDateList by lazy { MutableLiveData<State<List<Date>>>() }

    init {
        viewModelScope.launch { initBookingTable() }
    }

    fun getBookingTable() = bookingDateList as LiveData<State<List<Date>>>

    fun updateTable(date: String){
        mDate = date
        viewModelScope.launch { initBookingTable() }
    }

    private suspend fun initBookingTable() {
        repository.getBookingTable(mStadiumId).collect { state ->
            when (state) {
                is State.Loading -> {
                    bookingDateList.postValue(State.loading())
                }
                is State.Success -> {
                    val filtered = state.data.days.filter { date ->
                        date.date == mDate
                    }
                    bookingDateList.postValue(State.success(filtered))
                }
                is State.Failed -> {
                    bookingDateList.postValue(State.failed(state.message))
                }
            }
        }
    }

    fun sendBookingRequest(request: BookingRequest) =
        repository.addBookingRequest(request).asLiveData(viewModelScope.coroutineContext)

}