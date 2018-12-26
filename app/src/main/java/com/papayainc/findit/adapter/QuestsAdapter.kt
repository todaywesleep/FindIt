package com.papayainc.findit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.papayainc.findit.R
import com.papayainc.findit.model.Quest

class QuestsAdapter(private val data: ArrayList<Quest>) :
    RecyclerView.Adapter<QuestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuestsAdapter.ViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_quests_item, parent, false)

        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.questTitle.text = data[position].item_to_search
        holder.questReward.text = data[position].reward.toString()
    }

    override fun getItemCount() = data.size

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val questTitle: TextView = root.findViewById(R.id.fragment_quests_item_quest_title)
        val questReward: TextView = root.findViewById(R.id.fragment_quests_item_quest_reward)
    }

    fun addItem(item: Quest) {
        if (!data.contains(item)){
            data.add(item)
            notifyItemInserted(data.size)
        }
    }

    fun removeItem(item: Quest){
        notifyItemRemoved(data.indexOf(item))
        data.remove(item)
    }
}