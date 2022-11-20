package com.digo.emdiabetes.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digo.emdiabetes.databinding.ItemAdapterBinding
import com.digo.emdiabetes.model.Glycemia

class GlycemiaAdapter(
    private val context: Context,
    private val glycemiaList: List<Glycemia>,
    val glycemiaSelected: (Glycemia, Int) -> Unit
) : RecyclerView.Adapter<GlycemiaAdapter.MyViewHolder>() {

    companion object {
        val SELECT_BACK: Int = 1
        val SELECT_REMOVE: Int = 2
        val SELECT_EDIT: Int = 3
        val SELECT_DETAILS: Int = 4
        val SELECT_NEXT: Int = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val glycemia= glycemiaList[position]

        holder.binding.textDescription.text = glycemia.glicemia

        holder.binding.btnDelete.setOnClickListener { glycemiaSelected(glycemia, SELECT_REMOVE) }
        holder.binding.btnEdit.setOnClickListener { glycemiaSelected(glycemia, SELECT_EDIT) }
        holder.binding.btnDetails.setOnClickListener { glycemiaSelected(glycemia, SELECT_DETAILS) }


    }

    override fun getItemCount() = glycemiaList.size

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}