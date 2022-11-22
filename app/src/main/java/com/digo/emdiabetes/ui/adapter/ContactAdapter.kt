package com.digo.emdiabetes.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digo.emdiabetes.databinding.ItemAdapterBinding
import com.digo.emdiabetes.model.Contact

class ContactAdapter(
    private val context: Context,
    private val contactList: List<Contact>,
    val contactSelected: (Contact, Int) -> Unit
) : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

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
        val contact= contactList[position]

        holder.binding.textDescription.text = "Nome: "+contact.nome +"\nNúmero: "+ contact.numero

        holder.binding.btnDelete.setOnClickListener { contactSelected(contact, SELECT_REMOVE) }
        holder.binding.btnEdit.setOnClickListener { contactSelected(contact, SELECT_EDIT) }
        holder.binding.btnDetails.setOnClickListener { contactSelected(contact, SELECT_DETAILS) }


    }

    override fun getItemCount() = contactList.size

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

}