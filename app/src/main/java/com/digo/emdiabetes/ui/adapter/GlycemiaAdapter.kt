package com.digo.emdiabetes.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digo.emdiabetes.R
import com.digo.emdiabetes.databinding.ItemAdapterBinding
import com.digo.emdiabetes.model.Glycemia
import com.google.firebase.database.collection.LLRBNode

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
        val colorGlicoseDesregulada = ContextCompat.getColor(context, R.color.color_1)
        val colorGlicoseregulada = ContextCompat.getColor(context, R.color.color_2)

        holder.binding.textDescription.text = "Data: "+glycemia.dia+"/"+glycemia.mes+"/"+glycemia.ano+"\nGlicemia: "+glycemia.glicemia+"mg/dL"+"\nDescrição: "+ glycemia.descricao

        if (glycemia.glicemia.toDouble() >= 200 || glycemia.glicemia.toDouble() <= 75){
            holder.binding.cardview.setCardBackgroundColor(colorGlicoseDesregulada)
        }else{
            holder.binding.cardview.setCardBackgroundColor(colorGlicoseregulada)
        }


        holder.binding.btnDelete.setOnClickListener { glycemiaSelected(glycemia, SELECT_REMOVE) }
        holder.binding.btnEdit.setOnClickListener { glycemiaSelected(glycemia, SELECT_EDIT) }
        holder.binding.btnDetails.setOnClickListener { glycemiaSelected(glycemia, SELECT_DETAILS) }


    }

    override fun getItemCount() = glycemiaList.size

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}