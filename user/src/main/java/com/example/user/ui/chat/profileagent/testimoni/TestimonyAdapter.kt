package com.example.user.ui.chat.profileagent.testimoni

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.user.R

class TestimonyAdapter(private val testimonies: List<Testimony>) :
    RecyclerView.Adapter<TestimonyAdapter.TestimonyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestimonyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.testimony_item, parent, false)
        return TestimonyViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestimonyViewHolder, position: Int) {
        val testimony = testimonies[position]
        holder.bind(testimony)
    }

    override fun getItemCount(): Int {
        return testimonies.size
    }

    inner class TestimonyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.testimony_title)
        private val textTextView: TextView = itemView.findViewById(R.id.testimony_text)
        private val imageView: ImageView = itemView.findViewById(R.id.testimony_image)

        fun bind(testimony: Testimony) {
            titleTextView.text = testimony.title
            textTextView.text = testimony.text
            // Load image using Glide or Picasso library
            // Glide.with(itemView.context).load(testimony.imageUrl).into(imageView)
        }
    }
}
