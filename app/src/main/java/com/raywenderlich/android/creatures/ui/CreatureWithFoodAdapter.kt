package com.raywenderlich.android.creatures.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.creatures.R
import com.raywenderlich.android.creatures.app.inflate
import com.raywenderlich.android.creatures.model.Creature
import com.raywenderlich.android.creatures.model.CreatureStore
import kotlinx.android.synthetic.main.list_item_creature.view.*
import kotlinx.android.synthetic.main.list_item_creature.view.creatureImage
import kotlinx.android.synthetic.main.list_item_creature_with_food.view.*

class CreatureWithFoodAdapter(private val creatures: MutableList<Creature>) : RecyclerView.Adapter<CreatureWithFoodAdapter.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var creature: Creature
        private val adapter = FoodAdapter(mutableListOf())

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(creature: Creature) {
            this.creature = creature
            val context = itemView.context
            itemView.creatureImage.setImageResource(
                context.resources.getIdentifier(creature.uri, null, context.packageName))
            setupFoods()
        }

        private fun setupFoods() {
            itemView.foodRecyclerView.adapter = adapter
            itemView.foodRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            val foods = CreatureStore.getCreatureFoods(creature)
            adapter.updateFoods(foods)
        }

        override fun onClick(itemView: View?) {
            itemView?.let {
                val context = itemView.context
                val intent = CreatureActivity.newIntent(context, creature.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(parent.inflate(R.layout.list_item_creature_with_food))
        holder.itemView.foodRecyclerView.setRecycledViewPool(viewPool)
        LinearSnapHelper().attachToRecyclerView(holder.itemView.foodRecyclerView)
        return holder
    }

    override fun getItemCount() = creatures.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(creatures[position])
    }
}