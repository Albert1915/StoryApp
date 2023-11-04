package com.alz19.storyapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.alz19.storyapp.R
import com.alz19.storyapp.response.ListStoryItem
import com.alz19.storyapp.welcome.detail.DetailActivity
import com.bumptech.glide.Glide


class StoryListAdapter : RecyclerView.Adapter<StoryListAdapter.StoryListViewHolder>() {

    private var _storyList : List<ListStoryItem?>? = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setStoryList (value : List<ListStoryItem?>?){
        _storyList = value
        notifyDataSetChanged()
    }

    inner class StoryListViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageStoryList : ImageView  = itemView.findViewById(R.id.image_story_list)
        val titleStoryList :TextView = itemView.findViewById(R.id.text_story_list_title)
        val descStoryList : TextView = itemView.findViewById(R.id.text_story_list_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_story, parent, false)
        return StoryListViewHolder(view)
    }

    override fun getItemCount(): Int = _storyList?.size ?: 0

    override fun onBindViewHolder(holder: StoryListViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = _storyList?.get(position)

        holder.titleStoryList.text = item?.name
        holder.descStoryList.text = item?.description

        Glide.with(context)
            .load(item?.photoUrl ?: "https://i.stack.imgur.com/l60Hf.png")
            .into(holder.imageStoryList)

        holder.itemView.setOnClickListener {
            val moveIntent = Intent(context, DetailActivity::class.java)
            moveIntent.putExtra(DetailActivity.EXTRA_ID, item?.id)

            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity,
                Pair(holder.imageStoryList, "image_detail_story"),
                Pair(holder.titleStoryList, "text_detail_title"),
                Pair(holder.descStoryList, "text_detail_desc")
            )

            context.startActivity(moveIntent, optionsCompat.toBundle())
        }
    }
}