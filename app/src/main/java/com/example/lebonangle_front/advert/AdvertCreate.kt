package com.example.lebonangle_front.advert

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.lebonangle_front.R
import com.example.lebonangle_front.api.ApiConnection
import com.example.lebonangle_front.api.advert.AdvertPostJsonItem
import com.example.lebonangle_front.api.category.CategoriesJsonItem
import com.example.lebonangle_front.getFileName
import kotlinx.android.synthetic.main.activity_advert_create.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.awaitResponse
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AdvertCreate : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private val api = ApiConnection.connect()
    private val PICK_IMAGE = 100
    private val pictures = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert_create)

        //Remplis le spinner avec les differentes categories
        setSpinnerCategories()

        btnPublishAdvert.setOnClickListener {
            postAdvert()
        }

        btnGoToUploadImage.setOnClickListener {
            openImageChooser()
            btnPublishAdvert.isEnabled = false
            btnPublishAdvert.isClickable = false

            btnGoToUploadImage.isEnabled = false
            btnGoToUploadImage.isClickable = false

        }
    }

    private fun openImageChooser() {
        val gallery:Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            selectedImageUri = data!!.data
            uploadImage()
        }
    }

    private fun uploadImage() {


        val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val requestFile = RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                file
        )


        GlobalScope.launch(Dispatchers.IO) {
            val response = api.postPicture(MultipartBody.Part.createFormData("file", file.name, requestFile)).awaitResponse()
            if (response.isSuccessful){

                withContext(Dispatchers.Main) {
                    btnPublishAdvert.isEnabled = true
                    btnPublishAdvert.isClickable = true

                    btnGoToUploadImage.isEnabled = true
                    btnGoToUploadImage.isClickable = true
                }
                
                val data = response.body()!!
                pictures.add(data.iri)
                println(pictures)
            }
        }

    }

    private fun postAdvert(){
        val category = "api/categories/"+(spinnerCategoryAdvertCreate.selectedItem as CategoriesJsonItem).id

        val advert = AdvertPostJsonItem(
                etAuthor.text.toString(),
                category,
                etContent.text.toString(),
                etEmail.text.toString(),
                etPrice.text.toString().toFloat(),
                etTitle.text.toString(),
                pictures
        )

        btnPublishAdvert.isEnabled = false
        btnPublishAdvert.isClickable = false

        btnGoToUploadImage.isEnabled = false
        btnGoToUploadImage.isClickable = false

        GlobalScope.launch(Dispatchers.IO) {
            val response = api.postAdvert(advert).awaitResponse()

            if (response.isSuccessful){
                val data = response.body()!!
                withContext(Dispatchers.Main) {
                    finish()
                    startActivity(intent)
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