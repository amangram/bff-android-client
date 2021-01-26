package kg.bff.client.ui.requests.detail

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.View.OnClickListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kg.bff.client.R
import kotlinx.android.synthetic.main.item_time.view.*

class TimeAdapter() :
    ListAdapter<String, TimeAdapter.TimeVH>(StringDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeVH {
        val inflater = LayoutInflater.from(parent.context)
        return TimeVH(inflater.inflate(R.layout.item_time, parent, false))
    }

    override fun onBindViewHolder(holder: TimeVH, position: Int) {
        holder.bind(getItem(position))
    }

    fun swapData(data: List<String>) {
        submitList(data.toMutableList())
    }

    inner class TimeVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String) {
            itemView.tv_item_time.text = item
        }
    }

    private class StringDC : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
           return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}