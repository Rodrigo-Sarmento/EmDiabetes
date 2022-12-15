package com.digo.emdiabetes.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digo.emdiabetes.databinding.ItemAdapterBinding
import com.digo.emdiabetes.model.Diet

class DietAdapter(
    private val context: Context,
    private val dietList: List<Diet>,
    val contactSelected: (Diet, Int) -> Unit
) : RecyclerView.Adapter<DietAdapter.MyViewHolder>() {

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
        val diet= dietList[position]

        holder.binding.textDescription.text = "Refeição: "+diet.refeicao +"\nAlimento(s): "+ diet.alimento

        holder.binding.btnDelete.setOnClickListener { contactSelected(diet, SELECT_REMOVE) }
        holder.binding.btnEdit.setOnClickListener { contactSelected(diet, SELECT_EDIT) }
        holder.binding.btnDetails.setOnClickListener { contactSelected(diet, SELECT_DETAILS) }


    }

    override fun getItemCount() = dietList.size

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}