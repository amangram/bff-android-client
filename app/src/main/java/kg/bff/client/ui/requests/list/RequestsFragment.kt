package kg.bff.client.ui.requests.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kg.bff.client.R
import kg.bff.client.data.model.State
import kg.bff.client.gone
import kg.bff.client.toast
import kg.bff.client.ui.requests.detail.RequestDetailActivity
import kg.bff.client.visible
import kotlinx.android.synthetic.main.fragment_requests.*
import org.koin.android.viewmodel.ext.android.viewModel


class RequestsFragment : Fragment() {

    private val TAG = this::class.java.simpleName
    private val viewModel: RequestsViewModel by viewModel()
    private lateinit var activeAdapter: RequestAdapter
    private lateinit var rejectedAdapter: RequestAdapter
    private lateinit var waitingAdapter: RequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapters()
        setData()
    }

    private fun setAdapters() {
        activeAdapter = RequestAdapter { request ->
            startActivity(
                Intent(requireContext(), RequestDetailActivity::class.java)
                    .putExtra("Request", request)
            )
        }
        rejectedAdapter = RequestAdapter { request ->
            startActivity(
                Intent(requireContext(), RequestDetailActivity::class.java)
                    .putExtra("Request", request)
            )
        }
        waitingAdapter = RequestAdapter { request ->
            startActivity(
                Intent(requireContext(), RequestDetailActivity::class.java)
                    .putExtra("Request", request)
            )
        }
        rv_active_request.apply {
            adapter = activeAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        rv_rejected_request.apply {
            adapter = rejectedAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        rv_waiting_request.apply {
            adapter = waitingAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setData() {
        viewModel.activeList.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> {
                    pb_request.visible()
                }
                is State.Success -> {
                    pb_request.gone()
                    if (!state.data.isNullOrEmpty())
                        tv_empty_active.gone()
                    activeAdapter.swapData(state.data)
                }
                is State.Failed -> {
                    pb_request.gone()
                    Log.e(TAG, state.message)
                    context?.toast(getString(R.string.error))
                }
            }
        })
        viewModel.rejectList.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> {
                    pb_request.visible()
                }
                is State.Success -> {
                    pb_request.gone()
                    if (!state.data.isNullOrEmpty())
                        tv_empty_rejected.gone()
                    rejectedAdapter.swapData(state.data)
                }
                is State.Failed -> {
                    pb_request.gone()
                    Log.e(TAG, state.message)
                    context?.toast(getString(R.string.error))
                }
            }
        })
        viewModel.waitingList.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loading -> {
                    pb_request.visible()
                }
                is State.Success -> {
                    pb_request.gone()
                    if (!state.data.isNullOrEmpty())
                        tv_empty_waiting.gone()
                    waitingAdapter.swapData(state.data)
                }
                is State.Failed -> {
                    pb_request.gone()
                    Log.e(TAG, state.message)
                    context?.toast(getString(R.string.error))
                }
            }
        })
    }


}
