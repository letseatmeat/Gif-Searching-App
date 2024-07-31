package com.example.gifsearchappproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class DataAdapter(
    private val context: Context,
    private val dataModelArrayList: ArrayList<DataModel>
) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_gif, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = dataModelArrayList[position]
        Glide.with(context)
            .load(dataModel.imageUrl)
            .placeholder(R.drawable.placeholder) // Placeholder image
            .error(R.drawable.error) // Error image
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(RoundedCorners(16)) // Apply rounded corners
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return dataModelArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.gif_image)

        init {
            itemView.setOnClickListener {
                mListener?.onItemClick(adapterPosition)
            }
        }
    }
}
