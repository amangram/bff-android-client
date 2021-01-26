package kg.bff.client.ui.stadium.list

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kg.bff.client.R
import kg.bff.client.data.model.Stadium
import kg.bff.client.ui.GlideApp
import kotlinx.android.synthetic.main.item_horizontal_stadium.view.*

class StadiumsHorizontalAdapter(private val interaction: (Stadium) -> Unit) :
    ListAdapter<Stadium, StadiumsHorizontalAdapter.HorizontalVH>(StadiumDC()) {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalVH {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return HorizontalVH(inflater.inflate(R.layout.item_horizontal_stadium, parent, false))
    }

    override fun onBindViewHolder(holder: HorizontalVH, position: Int) {
        holder.bind(getItem(position))
    }

    fun swapData(data: List<Stadium>) {
        submitList(data.toMutableList())
    }

    inner class HorizontalVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Stadium) {
            if (!item.images.isNullOrEmpty()) {
                GlideApp.with(context)
                    .load(item.images[0].preview)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(itemView.iv_item_near)
            } else{
                itemView.iv_item_near.setImageResource(R.drawable.no_image)
            }
            itemView.tv_item_near.text = item.name
            itemView.setOnClickListener { interaction(item) }
        }
    }

    private class StadiumDC : DiffUtil.ItemCallback<Stadium>() {
        override fun areItemsTheSame(
            oldItem: Stadium,
            newItem: Stadium
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Stadium,
            newItem: Stadium
        ): Boolean {
            return oldItem == newItem
        }
    }
}