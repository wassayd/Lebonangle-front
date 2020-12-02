package com.example.lebonangle_front.advert

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lebonangle_front.api.ApiConnection
import com.example.lebonangle_front.R
import com.example.lebonangle_front.api.picture.PictureJson
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.advert_show_image.view.*

class AdvertShowAdapter(private val context: Context, private val pictures: List<PictureJson>) : RecyclerView.Adapter<AdvertHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertHolder {
        return AdvertHolder(LayoutInflater.from(context).inflate(R.layout.advert_show_image, parent, false))
    }

    override fun onBindViewHolder(holder: AdvertHolder, position: Int) {
        val data = pictures[position]
        val imageUrl = ApiConnection.BASE_SITE_URL + data.contentUrl

        val picassoBuilder = Picasso.Builder(context)
        picassoBuilder.downloader(OkHttp3Downloader(ApiConnection.getUnsafeOkHttpClient()))
        val picasso = picassoBuilder.build()


        picasso.load(imageUrl).into(holder.itemView.imgAdvertShow)


    }

    override fun getItemCount(): Int {
        return pictures.size
    }
}