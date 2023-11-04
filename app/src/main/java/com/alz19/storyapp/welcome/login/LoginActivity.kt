package com.alz19.storyapp.welcome.login

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
import com.alz19.storyapp.databinding.ActivityLoginBinding
import com.alz19.storyapp.helper.TokenFactory
import com.alz19.storyapp.helper.TokenPreference
import com.alz19.storyapp.helper.dataStore
import com.alz19.storyapp.welcome.main.MainActivity
import com.alz19.storyapp.welcome.WelcomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var activityLoginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private var isError: Boolean? = null


    private fun initComponent() {
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        loginViewModel = obtainViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponent()
        setContentView(activityLoginBinding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        val binding = activityLoginBinding

        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(300)
        val titleMessage = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(300)
        val inputEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val inputPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val loginButton = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(title, titleMessage, email, inputEmail, password, inputPassword, loginButton)
            start()
        }
    }

    private fun setupAction() {
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        activityLoginBinding.settingImageView.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        activityLoginBinding.editLoginPassword.message.observe(this) {
            activityLoginBinding.passwordEditTextLayout.error = it
        }

        activityLoginBinding.editLoginEmail.emailValidity.observe(this) {
            activityLoginBinding.emailEditTextLayout.error = it
        }

        activityLoginBinding.buttonLogin.setOnClickListener {

            if (activityLoginBinding.editLoginEmail.text.isNullOrEmpty()){
                activityLoginBinding.emailEditTextLayout.error = "Can't be empty"
            }
            if (activityLoginBinding.editLoginPassword.text.isNullOrEmpty()){
                activityLoginBinding.passwordEditTextLayout.error = "Can't be empty"
            }

            if (activityLoginBinding.emailEditTextLayout.error != null || activityLoginBinding.passwordEditTextLayout.error != null
            ) {
                Toast.makeText(this, "Please insert the valid data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginViewModel.loginUser(
                activityLoginBinding.editLoginEmail.text.toString(),
                activityLoginBinding.editLoginPassword.text.toString()
            )

            loginViewModel.isError.observe(this) {
                isError = it
            }
            loginViewModel.isLoading.observe(this) {
                if (isError == true) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Message")
                        setMessage("Can't Login, please check your data again")
                        setPositiveButton("Oke") { _, _ ->
                            val intent = Intent(context, WelcomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                    return@observe
                }
                if (!it) {
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

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

    private fun showLoading(isLoading: Boolean) {
        with(activityLoginBinding) {
            activityLoginBinding.progressLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
            imageView.visibility = if (!isLoading) View.VISIBLE else View.GONE
            titleTextView.visibility = if (!isLoading) View.VISIBLE else View.GONE
            messageTextView.visibility = if (!isLoading) View.VISIBLE else View.GONE
            emailTextView.visibility = if (!isLoading) View.VISIBLE else View.GONE
            emailEditTextLayout.visibility = if (!isLoading) View.VISIBLE else View.GONE
            passwordTextView.visibility = if (!isLoading) View.VISIBLE else View.GONE
            passwordEditTextLayout.visibility = if (!isLoading) View.VISIBLE else View.GONE
            buttonLogin.visibility = if (!isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun obtainViewModel(): LoginViewModel {
        val pref = TokenPreference.getInstance(application.dataStore)
        val factory = TokenFactory(pref)
        return ViewModelProvider(this, factory)[LoginViewModel::class.java]
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