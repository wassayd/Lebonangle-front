package com.example.lebonangle_front.advert

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lebonangle_front.R
import com.example.lebonangle_front.api.advert.AdvertJsonItem
import com.example.lebonangle_front.api.ApiConnection
import com.example.lebonangle_front.api.picture.PictureJson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_show_advert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShowAdvert : AppCompatActivity() {

    val api = ApiConnection.connect()
    private var pictures = ArrayList<PictureJson>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_advert)
        val advert = intent.extras?.get("advert") as AdvertJsonItem;
        setAdvertContent(advert)

    }

    @SuppressLint("SetTextI18n")
    private fun setAdvertContent(advert: AdvertJsonItem){
        tvShowTitle.text = advert.title
        tvShowContent.text = advert.content
        tvShowAuthor.text = "Créer par "+advert.author
        tvShowEmail.text = "Email : "+advert.email
        tvShowPrice.text = advert.price.toString() + "€"

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(advert.publishedAt)
        val publishedAt = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(date!!)

        tvShowPublishedAt.text = "Publié le $publishedAt"

        loadPictures(advert.id)
    }

    private fun loadPictures(advertId: Int) {

        GlobalScope.launch(Dispatchers.IO) {
            val response = api.getPictures(advertId).awaitResponse()

            if (response.isSuccessful){

                val data: ArrayList<PictureJson> = response.body()!!

                withContext(Dispatchers.Main) {
                    rvAdvertShowImage.layoutManager = LinearLayoutManager(this@ShowAdvert)
                    rvAdvertShowImage.adapter = AdvertShowAdapter(this@ShowAdvert,data)
                }
            }
        }
    }
}