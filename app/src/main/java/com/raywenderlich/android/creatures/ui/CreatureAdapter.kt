package com.raywenderlich.android.creatures.ui

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.creatures.R
import com.raywenderlich.android.creatures.app.inflate
import com.raywenderlich.android.creatures.model.Creature
import com.raywenderlich.android.creatures.model.Favorites
import kotlinx.android.synthetic.main.list_item_creature.view.*
import java.util.*

class CreatureAdapter(private val creatures: MutableList<Creature>, private val itemDragListener: ItemDragListener) : RecyclerView.Adapter<CreatureAdapter.ViewHolder>(), ItemTouchHelperListener {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, ItemSelectedListener {
        private lateinit var creature: Creature

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(creature: Creature) {
            this.creature = creature
            val context = itemView.context
            itemView.creatureImage.setImageResource(
                context.resources.getIdentifier(creature.uri, null, context.packageName))
            itemView.fullName.text = creature.fullName
            itemView.nickname.text = creature.nickname
            itemView.handle.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    itemDragListener.onItemDrag(this)
                }
                false
            }
            animateView(itemView)
        }

        override fun onClick(itemView: View?) {
            itemView?.let {
                val context = itemView.context
                val intent = CreatureActivity.newIntent(context, creature.id)
                context.startActivity(intent)
            }
        }

        override fun onItemSelected() {
            itemView.listItemContainer.setBackgroundColor(
                ContextCompat.getColor(itemView.context, R.color.selectedItem)
            )
        }

        override fun onItemCleared() {
            itemView.listItemContainer.setBackgroundColor(0)
        }

        private fun animateView(viewToAnimate: View) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.scale_favorites_item)
            viewToAnimate.animation = animation
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.list_item_creature)
        return ViewHolder(view)
    }

    override fun getItemCount() = creatures.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(creatures[position])
    }

    fun updateCreatures(creatures: List<Creature>) {
        this.creatures.clear()
        this.creatures.addAll(creatures)
        notifyDataSetChanged()
    }

    override fun onItemMove(recyclerView: RecyclerView, fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(creatures, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(creatures, i, i - 1)
            }
        }
        Favorites.saveFavorites(creatures.map { it.id }, recyclerView.context)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(viewHolder: RecyclerView.ViewHolder, position: Int) {
        Favorites.removeFavorite(creatures[position], viewHolder.itemView.context)
        creatures.removeAt(position)
        notifyItemRemoved(position)
    }
}