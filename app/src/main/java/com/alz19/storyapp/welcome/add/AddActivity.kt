package com.alz19.storyapp.welcome.add

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.alz19.storyapp.databinding.ActivityAddBinding
import com.alz19.storyapp.helper.TokenFactory
import com.alz19.storyapp.helper.TokenPreference
import com.alz19.storyapp.helper.Utils.Companion.getImageUri
import com.alz19.storyapp.helper.Utils.Companion.reduceFileImage
import com.alz19.storyapp.helper.Utils.Companion.uriToFile
import com.alz19.storyapp.helper.dataStore
import com.alz19.storyapp.welcome.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddActivity : AppCompatActivity() {

    private lateinit var activityAddBinding: ActivityAddBinding
    private lateinit var addViewModel: AddViewModel

    private var currentImageUri: Uri? = null
    private var message: String? = null

    private fun initComponent() {
        activityAddBinding = ActivityAddBinding.inflate(layoutInflater)
        addViewModel = obtainViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponent()
        setContentView(activityAddBinding.root)

        activityAddBinding.buttonAddGallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        activityAddBinding.buttonAddCamera.setOnClickListener {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }

        addViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        activityAddBinding.buttonAddSubmit.setOnClickListener {

            if (currentImageUri == null){
                Toast.makeText(this, "Insert Image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentImageUri?.let { uri ->
                val description = activityAddBinding.editAddDesc.text.toString()
                val imageFile = uriToFile(uri, this).reduceFileImage()
                if (description.isEmpty()){
                    Toast.makeText(this, "Description can't be empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                addViewModel.postMultiPart(multipartBody, requestBody)

                addViewModel.message.observe(this) {
                    message = it
                }
                addViewModel.isLoading.observe(this) {
                    if (!it) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    }
                }
                if (!message.isNullOrEmpty()) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun obtainViewModel(): AddViewModel {
        val pref = TokenPreference.getInstance(application.dataStore)
        val factory = TokenFactory(pref)
        return ViewModelProvider(this, factory)[AddViewModel::class.java]
    }

    private fun showImage() {
        currentImageUri?.let {
            try {
                // Use the URI for further processing (e.g., display in an ImageView)
                activityAddBinding.imageAddStory.setImageURI(it)
            } catch (e: Exception) {
                Log.e("Photo Picker", "Error displaying selected image: ${e.message}")
                Toast.makeText(this, "Error displaying the selected image", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(activityAddBinding) {
            progressAdd.visibility = if (isLoading) View.VISIBLE else View.GONE
            imageAddStory.visibility = if (!isLoading) View.VISIBLE else View.GONE
            buttonAddGallery.visibility = if (!isLoading) View.VISIBLE else View.GONE
            buttonAddCamera.visibility = if (!isLoading) View.VISIBLE else View.GONE
            buttonAddSubmit.visibility = if (!isLoading) View.VISIBLE else View.GONE
            editAddDescLayout.visibility = if (!isLoading) View.VISIBLE else View.GONE
        }
    }

}