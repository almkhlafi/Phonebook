package com.example.sqlite
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.dictionaryViewHolder>() {
    private var itemList: ArrayList<ItemModel> = ArrayList()
    private var onClickItem: ((ItemModel) -> Unit)? = null
    private var onDeleteItemClick: ((ItemModel) -> Unit)? = null

    fun setOnClickItem(callback: (ItemModel) -> Unit) {
        this.onClickItem = callback
    }

    fun setOnDeleteItemClick(callback: (ItemModel) -> Unit) {
        this.onDeleteItemClick = callback
    }

    fun addItems(items: ArrayList<ItemModel>) {
        this.itemList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): dictionaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_items_dictionary, parent, false)
        return dictionaryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: dictionaryViewHolder, position: Int) {
        val item = itemList[position]
        holder.bindView(item)
        holder.itemView.setOnClickListener { onClickItem?.invoke(item) }
        holder.btnDelete.setOnClickListener { onDeleteItemClick?.invoke(item) }
    }

    class dictionaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val id: TextView = view.findViewById(R.id.tvId)
        private val Name: TextView = view.findViewById(R.id.Name)
        private val PNumber: TextView = view.findViewById(R.id.PhoneNum)
        private val image: ImageView = view.findViewById(R.id.ivImage)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)

        fun bindView(item: ItemModel) {
            Name.text = item.name
            PNumber.text = item.phonenumber.toString()

        item.image?.let { imgByteArray ->
                val bitmap = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.size)
                image.setImageBitmap(bitmap)
            } ?: run {
                image.setImageResource(R.drawable.addimage)
            }
        }
    }
}
