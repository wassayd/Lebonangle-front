package com.example.lebonangle_front.advert

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lebonangle_front.R
import com.example.lebonangle_front.advert.ShowAdvert
import com.example.lebonangle_front.api.advert.AdvertJson
import kotlinx.android.synthetic.main.advert_cell.view.*
import java.text.SimpleDateFormat
import java.util.*

class AdvertAdapter(context: Context, adverts: AdvertJson) : RecyclerView.Adapter<AdvertHolder>() {
    private val context  = context
    private val adverts   = adverts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertHolder {
        return AdvertHolder(LayoutInflater.from(context).inflate(R.layout.advert_cell, parent, false))
    }

    override fun onBindViewHolder(holder: AdvertHolder, position: Int) {
        val data = adverts[position]

        val titleTextView = holder.itemView.tvTitle
        val contentTextView = holder.itemView.tvCreatedBy

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(data.createdAt)
        val createdAt = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(date!!)

        val txtCreatedBy = "Annonce créer par ${data.author} le $createdAt";

        titleTextView.text = data.title
        contentTextView.text = txtCreatedBy

        holder.itemView.btnShowAdvert.setOnClickListener {
            val intent = Intent(context, ShowAdvert::class.java)
            intent.putExtra("advert",data)
            ContextCompat.startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
        return adverts.size
    }
}