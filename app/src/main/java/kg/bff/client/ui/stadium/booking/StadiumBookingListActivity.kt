package kg.bff.client.ui.stadium.booking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kg.bff.client.R
import kg.bff.client.gone
import kg.bff.client.data.model.BookingRequest
import kg.bff.client.data.model.State
import kg.bff.client.toast
import kg.bff.client.visible
import kotlinx.android.synthetic.main.activity_stadium_booking_list.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*
import kotlin.properties.Delegates

class StadiumBookingListActivity : AppCompatActivity() {
    private val myViewModel: StadiumBookingListViewModel by viewModel {
        parametersOf(intent.getStringExtra("stadiumId"),booking_day_tv.text)
    }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var stadiumId: String
    private lateinit var adminId: String
    private lateinit var stadiumName: String
    private lateinit var bookingListAdapter: BookingListAdapter
    private val selectedHours = mutableListOf<String>()
    private lateinit var calendar: Calendar
    private var year by Delegates.notNull<Int>()
    private var month by Delegates.notNull<Int>()
    private var day by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stadium_booking_list)
        stadiumId = intent.getStringExtra("stadiumId")
        adminId = intent.getStringExtra("ADMIN")
        stadiumName = intent.getStringExtra("S_NAME")
        setCurrentDate()
        setAdapter()
        setBookingList()
        booking_day_tv.setOnClickListener {
            pickDate()
        }
        btn_next_date.setOnClickListener {
            booking_day_tv.text = "${++day}/$month/$year"
            myViewModel.updateTable(booking_day_tv.text.toString())
        }
        btn_prev_date.setOnClickListener {
            booking_day_tv.text = "${--day}/$month/$year"
            myViewModel.updateTable(booking_day_tv.text.toString())
            toast(day.toString())
        }
        btn_send_request.setOnClickListener {
            if (!selectedHours.isNullOrEmpty()) {
                prepareRequest()
            } else {
                toast(getString(R.string.no_selected_date))
            }
        }

    }

    private fun setCurrentDate() {
        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)
        booking_day_tv.text = "$day/$month/$year"
    }

    private fun pickDate() {
        val datePicker =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                booking_day_tv.text = "$mDay/$mMonth/$mYear"
                myViewModel.updateTable(booking_day_tv.text.toString())
            }, year, month, day)
        datePicker.show()
    }

    private fun setAdapter() {
        bookingListAdapter = BookingListAdapter { item, checked ->
            if (checked) {
                if (!selectedHours.contains(item.hour)) {
                    selectedHours.add(item.hour)
                }
            } else {
                selectedHours.remove(item.hour)
            }
        }
        booking_list_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@StadiumBookingListActivity)
            adapter = bookingListAdapter
        }
    }

    private fun setBookingList() {
        myViewModel.getBookingTable().observe(this, Observer { state ->
            when (state) {
                is State.Loading -> {
                    pb_booking.visible()
                }
                is State.Success -> {
                    pb_booking.gone()
                    bookingListAdapter.submitList(state.data.first().hours)
                }
                is State.Failed -> {
                    pb_booking.gone()
                    toast(getString(R.string.error))
                }
            }
        })
    }

    private fun prepareRequest() {
        val ad = AlertDialog.Builder(this)
        ad.setTitle(R.string.request)
        ad.setMessage(R.string.request_dialog)
        ad.setPositiveButton(R.string.yes) { dialog, which ->
            sendRequest()
        }
        ad.setNegativeButton(R.string.no) { dialog, which ->
            dialog.cancel()
        }
        ad.setCancelable(true)
        ad.show()
    }

    private fun sendRequest() {
        val request = auth.currentUser?.uid?.let { uId ->
            BookingRequest(
                "",uId, stadiumId,stadiumName, adminId, booking_day_tv.text.toString(), selectedHours,
                status = "waiting")
        }
        request?.let { myViewModel.sendBookingRequest(it).observe(this, Observer { state->
            when(state){
                is State.Loading->{
                    pb_booking.visible()
                }
                is State.Success->{
                    pb_booking.gone()
                    toast(getString(R.string.request_success))
                    finish()
                }
                is State.Failed->{
                    pb_booking.gone()
                    Log.d("TAGht", state.message)
                    toast(getString(R.string.error))
                }
            }
        }) }
    }
}
