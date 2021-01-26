package kg.bff.client.ui.stadium.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kg.bff.client.R
import kg.bff.client.data.model.Stadium
import kg.bff.client.ui.GlideApp
import kotlinx.android.synthetic.main.item_vertical_stadium.view.*

class StadiumsAdapter(private val interaction: (Stadium) -> Unit,private val save: (Stadium)-> Unit) :
    ListAdapter<Stadium, StadiumsAdapter.StadiumVH>(StadiumDC()), Filterable {

    private lateinit var context: Context
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val fullList: ArrayList<Stadium> by lazy { ArrayList<Stadium>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StadiumVH {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return StadiumVH(inflater.inflate(R.layout.item_vertical_stadium, parent, false))
    }

    override fun onBindViewHolder(holder: StadiumVH, position: Int) {
        holder.bind(getItem(position))
    }

    fun swapData(data: List<Stadium>) {
        submitList(data.toMutableList())
        fullList.addAll(data)
    }

    inner class StadiumVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Stadium) {
            itemView.stadium_title_name_tv.text = item.name
            itemView.stadium_item_price_tv.text = "${item.price} С/Час"
            if (!item.images.isEmpty()) {
                GlideApp.with(context)
                    .load(item.images[0].preview)
                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .into(itemView.stadium_list_image)
            } else{
                itemView.stadium_list_image.setImageResource(R.drawable.no_image)
            }

            if (item.saved.contains(auth.currentUser?.uid)){
                itemView.iv_item_save.setImageResource(R.drawable.ic_saved)
            } else{
                itemView.iv_item_save.setImageResource(R.drawable.ic_not_saved)
            }
            itemView.iv_item_save.setOnClickListener { save(item) }
            itemView.setOnClickListener {
                interaction(item)
            }
        }
    }

    override fun getFilter(): Filter {
        return stadiumFilter
    }

    private val stadiumFilter = object : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Stadium>()
            if (constraint.isNullOrEmpty()){
                filteredList.addAll(fullList)
            } else{
                val filterPattern = constraint.toString().toLowerCase().trim()
                for (item in fullList){
                    if (item.name.toLowerCase().contains(filterPattern))
                        filteredList.add(item)
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            submitList(results?.values as MutableList<Stadium>)
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