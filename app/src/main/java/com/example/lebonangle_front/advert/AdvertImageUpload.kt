package com.example.lebonangle_front.advert

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.example.lebonangle_front.R
import com.example.lebonangle_front.UploadRequestBody
import com.example.lebonangle_front.api.ApiConnection
import com.example.lebonangle_front.api.picture.PicturePostJson
import com.example.lebonangle_front.getFileName
import com.example.lebonangle_front.snackbar
import kotlinx.android.synthetic.main.activity_advert_image_upload.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class AdvertImageUpload : AppCompatActivity(), UploadRequestBody.UploadCallback {

    private var selectedImageUri: Uri? = null
    private val api = ApiConnection.connect()

    private lateinit var  bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert_image_upload)

        imgChoosePicture.setOnClickListener {
            openImageChooser()
        }

        button_upload.setOnClickListener {
            uploadImage()
        }
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }
    private fun openImageChooser2() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            it.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    imgChoosePicture.setImageURI(selectedImageUri)

                    /*var path = data!!.data
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver,path)
                    imgChoosePicture.setImageBitmap(bitmap)*/
                }
            }
        }
    }


    private fun uploadImage2(){
        CoroutineScope(Dispatchers.IO).launch {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)

            val imageInByte = byteArrayOutputStream.toByteArray()

            val encodedImage = Base64.encodeToString(imageInByte, Base64.DEFAULT)

            val response = api.postPicture2(encodedImage).awaitResponse()
            withContext(Dispatchers.Main) {
                println(response)
            }

        }



    }

    private fun uploadImage() {
        if (selectedImageUri == null){
            layout_upload_image.snackbar("Select an Image First")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        progress_bar.progress = 0
        //val body = UploadRequestBody(file, "image", this)

        val requestFile = RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                file
        )

        api.postPicture(
                MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        requestFile
                )
        ).enqueue(object : Callback<PicturePostJson> {

            override fun onFailure(call: Call<PicturePostJson>, t: Throwable) {
                layout_upload_image.snackbar(t.message!!)
                progress_bar.progress = 0
                println("said error")
            }

            override fun onResponse(
                    call: Call<PicturePostJson>,
                    response: Response<PicturePostJson>
            ) {
                response.body()?.let {
                    layout_upload_image.snackbar("Good")
                    progress_bar.progress = 100
                }

            }
        })
    }

    override fun onProgressUpdate(percentage: Int) {
        progress_bar.progress = percentage
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 21
    }
}