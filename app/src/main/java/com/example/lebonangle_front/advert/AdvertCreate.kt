package com.example.lebonangle_front.advert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.lebonangle_front.R
import com.example.lebonangle_front.api.ApiConnection
import com.example.lebonangle_front.api.advert.AdvertPostJsonItem
import com.example.lebonangle_front.api.category.CategoriesJsonItem
import kotlinx.android.synthetic.main.activity_advert_create.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class AdvertCreate : AppCompatActivity() {

    val api = ApiConnection.connect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert_create)

        //Remplis le spinner avec les differentes categories
        setSpinnerCategories()

        btnPublishAdvert.setOnClickListener {
            postAdvert()
        }

    }


    private fun postAdvert(){
        val category = "api/categories/"+(spinnerCategoryAdvertCreate.selectedItem as CategoriesJsonItem).id

        val advert = AdvertPostJsonItem(
            etAuthor.text.toString(),
            category,
            etContent.text.toString(),
            etEmail.text.toString(),
            etPrice.text.toString().toFloat() ,
            etTitle.text.toString()
        )

        GlobalScope.launch(Dispatchers.IO) {
            val response = api.postAdvert(advert).awaitResponse()

            if (response.isSuccessful){
                val data = response.body()!!
                withContext(Dispatchers.Main) {
                    println("POST SAID $data")
                }
            }
        }
    }

    private fun setSpinnerCategories(){
        GlobalScope.launch(Dispatchers.IO) {
            val response = api.getCategories().awaitResponse()

            if (response.isSuccessful){
                val categories = response.body()!!
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter<CategoriesJsonItem>(
                        this@AdvertCreate,
                        android.R.layout.simple_spinner_item,
                        categories
                    )

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategoryAdvertCreate.adapter = adapter;
                }
            }
        }
    }


}