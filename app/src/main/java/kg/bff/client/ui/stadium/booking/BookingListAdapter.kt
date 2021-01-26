package kg.bff.client.ui.stadium.booking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kg.bff.client.R
import kg.bff.client.data.model.Hour
import kg.bff.client.gone
import kg.bff.client.toast
import kg.bff.client.visible
import kotlinx.android.synthetic.main.item_booking_list.view.*

class BookingListAdapter(private val clickListener: (Hour, Boolean) -> Unit) :
    ListAdapter<Hour, BookingListAdapter.ViewHolder>(HourDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return ViewHolder(inflater.inflate(R.layout.item_booking_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bind(_hour: Hour) {
            itemView.hour_tv.text = _hour.hour
            if (_hour.booked) {
                itemView.cv_status.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSecondary
                    )
                )
                itemView.cb_booking.gone()
                itemView.tv_status_text.text = "Занято"
            } else {
                itemView.cv_status.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorAccent
                    )
                )
                itemView.cb_booking.visible()
                itemView.tv_status_text.text = "Свободно"
            }
            itemView.setOnClickListener {
                if (!_hour.booked) {
                    when (itemView.cb_booking.isChecked) {
                        false -> {
                            itemView.cb_booking.isChecked = true
                            clickListener(_hour, true)
                        }
                        true -> {
                            itemView.cb_booking.isChecked = false
                            clickListener(_hour, false)
                        }
                    }
                }
            }
            itemView.cb_booking.setOnClickListener {
                context.toast(_hour.hour)
                when (itemView.cb_booking.isChecked) {
                    false -> {
                        clickListener(_hour, false)
                    }
                    true -> {
                        clickListener(_hour, true)
                    }
                }
            }
        }
    }

    class HourDiffCallback : DiffUtil.ItemCallback<Hour>(){
        override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem==newItem
        }

        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem.hour == newItem.hour
        }
    }
}