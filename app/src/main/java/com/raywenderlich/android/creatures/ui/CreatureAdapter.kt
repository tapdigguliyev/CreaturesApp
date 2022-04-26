package com.raywenderlich.android.creatures.ui

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.creatures.R
import com.raywenderlich.android.creatures.app.inflate
import com.raywenderlich.android.creatures.model.CompositeItem
import com.raywenderlich.android.creatures.model.Creature
import kotlinx.android.synthetic.main.list_item_creature.view.*
import kotlinx.android.synthetic.main.list_item_planet_header.view.*
import java.lang.IllegalArgumentException

class CreatureAdapter(private val compositeItems: MutableList<CompositeItem>) : RecyclerView.Adapter<CreatureAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var creature: Creature

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(compositeItem: CompositeItem) {
            if (compositeItem.isHeader) {
                itemView.headerName.text = compositeItem.header.name
                itemView.isClickable = false
            } else{
                this.creature = compositeItem.creature
                val context = itemView.context
                itemView.creatureImage.setImageResource(
                    context.resources.getIdentifier(creature.uri, null, context.packageName))
                itemView.fullName.text = creature.fullName
                itemView.nickname.text = creature.nickname
                animateView(itemView)
            }
        }

        override fun onClick(itemView: View?) {
            itemView?.let {
                val context = itemView.context
                val intent = CreatureActivity.newIntent(context, creature.id)
                context.startActivity(intent)
            }
        }

        private fun animateView(viewToAnimate: View) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.scale_favorites_item)
            viewToAnimate.animation = animation
        }
    }

    enum class ViewType {
        HEADER, CREATURE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType) {
            ViewType.HEADER.ordinal -> ViewHolder(parent.inflate(R.layout.list_item_planet_header))
            ViewType.CREATURE.ordinal -> ViewHolder(parent.inflate(R.layout.list_item_creature))
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount() = compositeItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(compositeItems[position])
    }

    override fun getItemViewType(position: Int): Int {
        return if(compositeItems[position].isHeader) ViewType.HEADER.ordinal else ViewType.CREATURE.ordinal
    }

    fun updateCreatures(compositeItems: List<CompositeItem>) {
        this.compositeItems.clear()
        this.compositeItems.addAll(compositeItems)
        notifyDataSetChanged()
    }
}