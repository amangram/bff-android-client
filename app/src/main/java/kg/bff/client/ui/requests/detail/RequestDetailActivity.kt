package kg.bff.client.ui.requests.detail

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.bff.client.R
import kg.bff.client.data.model.BookingRequest
import kg.bff.client.data.model.State
import kg.bff.client.gone
import kg.bff.client.toast
import kg.bff.client.visible
import kotlinx.android.synthetic.main.activity_reques_detail.*
import org.koin.android.viewmodel.ext.android.viewModel

class RequestDetailActivity : AppCompatActivity() {

    private lateinit var request: BookingRequest
    private lateinit var timeAdapter: TimeAdapter
    private val viewModel: RequestDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reques_detail)
        request = intent.getParcelableExtra("Request")
        setAdapter()
        setData()
        btn_delete_request.setOnClickListener {
            prepareDeleting()
        }
    }

    private fun setAdapter() {
        timeAdapter = TimeAdapter()
        rv_request_detail_time.apply {
            adapter = timeAdapter
            layoutManager =
                LinearLayoutManager(this@RequestDetailActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun prepareDeleting() {
        val ad = AlertDialog.Builder(this)
        ad.setTitle(R.string.request)
        ad.setMessage(R.string.request_dialog)
        ad.setNegativeButton(R.string.no) { dialog, which ->
            dialog.cancel()
        }
        ad.setPositiveButton(R.string.yes) { dialog, which ->
           deleteRequest()
        }
        ad.setCancelable(true)
        ad.show()
    }

    private fun setData() {
        tv_request_stadium_name.text = request.stadiumName
        when (request.status) {
            "waiting" -> tv_request_status_text.text = "В ожидании"
            "active" -> tv_request_status_text.text = "Активный"
            "rejected" -> tv_request_status_text.text = "Отказано"
        }
        timeAdapter.swapData(request.time)
    }

    private fun deleteRequest(){
        viewModel.deleteRequest(request.id).observe(this, Observer { state->
            when(state){
                is State.Loading->{
                    pb_delete_request.visible()
                }
                is State.Success->{
                    pb_delete_request.gone()
                    toast(getString(R.string.deleted))
                    finish()
                }
                is State.Failed ->{
                    pb_delete_request.gone()
                    toast(getString(R.string.error))
                }
            }
        })
    }

}
