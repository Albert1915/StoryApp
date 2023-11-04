package com.alz19.storyapp.welcome.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alz19.storyapp.R
import com.alz19.storyapp.adapter.StoryListAdapter
import com.alz19.storyapp.databinding.ActivityMainBinding
import com.alz19.storyapp.helper.TokenFactory
import com.alz19.storyapp.helper.TokenPreference
import com.alz19.storyapp.helper.dataStore
import com.alz19.storyapp.response.ListStoryItem
import com.alz19.storyapp.welcome.WelcomeActivity
import com.alz19.storyapp.welcome.add.AddActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var adapter: StoryListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = obtainViewModel()
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setupAction()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_logout -> mainViewModel.logoutSession()
            R.id.menu_main_setting -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupAction() {

        activityMainBinding.recyclerMainStories.layoutManager = LinearLayoutManager(this)
        adapter = StoryListAdapter()
        activityMainBinding.recyclerMainStories.adapter = adapter

        mainViewModel.getSession().observe(this) { token ->
            if (token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.allStoryResponse.observe(this) {
            setStoryListData(it.listStory)
        }

        activityMainBinding.buttonMainAdd.setOnClickListener {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent(this, AddActivity::class.java))
            finish()
        }
    }


    private fun showLoading(isLoading: Boolean) {
        activityMainBinding.progressMain.visibility = if (isLoading) View.VISIBLE else View.GONE

    }

    private fun setStoryListData(storyList: List<ListStoryItem?>?) {
        adapter.setStoryList(storyList)
        activityMainBinding.recyclerMainStories.adapter = adapter
    }

    private fun obtainViewModel(): MainViewModel {
        val pref = TokenPreference.getInstance(application.dataStore)
        val factory = TokenFactory(pref)
        return ViewModelProvider(this, factory)[MainViewModel::class.java]
    }
}