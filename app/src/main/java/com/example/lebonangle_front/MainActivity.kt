package com.example.lebonangle_front

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lebonangle_front.advert.AdvertAdapter
import com.example.lebonangle_front.advert.AdvertCreate
import com.example.lebonangle_front.advert.ShowAdvert
import com.example.lebonangle_front.api.advert.AdvertJson
import com.example.lebonangle_front.api.ApiConnection
import com.example.lebonangle_front.api.category.CategoriesJson
import com.example.lebonangle_front.api.category.CategoriesJsonItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse


class MainActivity : AppCompatActivity() {

    private var TAG = "MainActivity"

    val api = ApiConnection.connect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCurrentData()

        btnAdvertCreate.setOnClickListener {
            val intent = Intent(this@MainActivity, AdvertCreate::class.java)

            ContextCompat.startActivity(this@MainActivity,intent,null)
        }
    }


    private fun getCurrentData(){
        GlobalScope.launch(Dispatchers.IO) {
           //val response = api.getAdverts(2).awaitResponse()
            val response = api.getCategories().awaitResponse()

            if (response.isSuccessful){
                val categories = response.body()!!
                withContext(Dispatchers.Main) {
                    setSpinnerCategories(categories)
                }
            }
        }
    }

    private fun setSpinnerCategories(data: CategoriesJson) {
        val adapter = ArrayAdapter<CategoriesJsonItem>(
            this,
            android.R.layout.simple_spinner_item,
            data
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.adapter = adapter;

        spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val currentCategory: CategoriesJsonItem = adapterView?.getItemAtPosition(position) as CategoriesJsonItem

                GlobalScope.launch(Dispatchers.IO) {
                    val response = api.getAdverts(currentCategory.id).awaitResponse()

                    if (response.isSuccessful){
                        val data = response.body()!!

                        withContext(Dispatchers.Main) {
                            rvAdverts.layoutManager = LinearLayoutManager(this@MainActivity)
                            rvAdverts.adapter = AdvertAdapter(this@MainActivity,data)
                        }
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                val currentCategory: CategoriesJsonItem = adapterView?.selectedItem as CategoriesJsonItem

            }

        }
    }

    private fun setAdvertsInRecyclerView(data: AdvertJson){

    }
}