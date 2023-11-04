package com.alz19.storyapp.welcome.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.alz19.storyapp.databinding.ActivityRegisterBinding
import com.alz19.storyapp.welcome.WelcomeActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var activityRegisterBinding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private var isError :Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        setContentView(activityRegisterBinding.root)

        setupAction()
        setupView()
        playAnimation()
    }

    private fun playAnimation() {
        val binding = activityRegisterBinding

        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.registerTitle, View.ALPHA, 1f).setDuration(300)
        val name = ObjectAnimator.ofFloat(binding.textRegisterNameLabel, View.ALPHA, 1f).setDuration(300)
        val inputName = ObjectAnimator.ofFloat(binding.editRegisterNameLayout, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.textRegisterEmailLabel, View.ALPHA, 1f).setDuration(300)
        val inputEmail = ObjectAnimator.ofFloat(binding.editRegisterEmailLayout, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.textRegisterPasswordLabel, View.ALPHA, 1f).setDuration(300)
        val inputPassword = ObjectAnimator.ofFloat(binding.editRegisterPasswordLayout, View.ALPHA, 1f).setDuration(300)
        val button = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(300)


        AnimatorSet().apply {
            playSequentially(title, name, inputName, email, inputEmail, password, inputPassword, button)
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        activityRegisterBinding.settingImageView.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        activityRegisterBinding.editRegisterPassword.message.observe(this) {
            activityRegisterBinding.editRegisterPasswordLayout.error = it
        }

        activityRegisterBinding.editRegisterEmail.emailValidity.observe(this) {
            activityRegisterBinding.editRegisterEmailLayout.error = it
        }

        activityRegisterBinding.buttonRegister.setOnClickListener {
            if (activityRegisterBinding.editRegisterEmail.text.isNullOrEmpty()){
                activityRegisterBinding.editRegisterEmailLayout.error = "Can't be empty"
            }
            if (activityRegisterBinding.editRegisterPassword.text.isNullOrEmpty()){
                activityRegisterBinding.editRegisterPasswordLayout.error = "Can't be empty"
            }

            if (activityRegisterBinding.editRegisterEmailLayout.error != null || activityRegisterBinding.editRegisterPasswordLayout.error != null){
                Toast.makeText(this, "Please insert the valid data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerViewModel.regisUser(
                activityRegisterBinding.editRegisterName.text.toString(),
                activityRegisterBinding.editRegisterEmail.text.toString(),
                activityRegisterBinding.editRegisterPassword.text.toString(),
            )

            registerViewModel.isError.observe(this){
                isError = it
            }
            registerViewModel.isLoading.observe(this){
                if (!it){
                    AlertDialog.Builder(this).apply {
                        setTitle("Message")
                        setMessage(if (isError == true) "Can't create account" else "Account created")
                        setPositiveButton("Oke") { _, _ ->
                            val intent = Intent(context, WelcomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        activityRegisterBinding.progressRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.imageView.visibility = if (!isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.registerTitle.visibility = if (!isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.textRegisterNameLabel.visibility = if (!isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.editRegisterNameLayout.visibility = if (!isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.textRegisterEmailLabel.visibility = if (!isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.editRegisterEmailLayout.visibility = if (!isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.textRegisterPasswordLabel.visibility = if (!isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.editRegisterPasswordLayout.visibility = if (!isLoading) View.VISIBLE else View.GONE
        activityRegisterBinding.buttonRegister.visibility = if (!isLoading) View.VISIBLE else View.GONE
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }


}