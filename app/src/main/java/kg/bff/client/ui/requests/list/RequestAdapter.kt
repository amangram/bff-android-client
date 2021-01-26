package kg.bff.client.ui.requests.list

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kg.bff.client.R
import kg.bff.client.data.model.BookingRequest
import kotlinx.android.synthetic.main.item_request.view.*

class RequestAdapter(private val interaction: (BookingRequest) -> Unit) :
    ListAdapter<BookingRequest, RequestAdapter.RequestVH>(
        BookingRequestDC()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestVH {
        val inflater = LayoutInflater.from(parent.context)
        return RequestVH(inflater.inflate(R.layout.item_request, parent, false))
    }

    override fun onBindViewHolder(holder: RequestVH, position: Int) {
        holder.bind(getItem(position))
    }

    fun swapData(data: List<BookingRequest>) {
        submitList(data.toMutableList())
    }

    inner class RequestVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BookingRequest) {
            itemView.tv_item_request_name.text = item.stadiumName
            itemView.tv_item_request_date.text = item.date
            itemView.setOnClickListener { interaction(item) }
        }
    }

    private class BookingRequestDC : DiffUtil.ItemCallback<BookingRequest>() {
        override fun areItemsTheSame(
            oldItem: BookingRequest,
            newItem: BookingRequest
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: BookingRequest,
            newItem: BookingRequest
        ): Boolean {
            return oldItem == newItem
        }
    }
}