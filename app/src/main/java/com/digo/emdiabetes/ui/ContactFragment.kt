package com.digo.emdiabetes.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digo.emdiabetes.R
import com.digo.emdiabetes.databinding.FragmentContactBinding

import com.digo.emdiabetes.helper.FirebaseHelper
import com.digo.emdiabetes.helper.showBottomSheet
import com.digo.emdiabetes.model.Contact
import com.digo.emdiabetes.ui.adapter.ContactAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactAdapter: ContactAdapter

    private val contactList = mutableListOf<Contact>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getContacts()
    }

    //botão ADD
    private fun initClicks() {
        binding.fabAddContact.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormContactFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getContacts() {
        FirebaseHelper
            .getDatabase()
            .child("contact")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        contactList.clear()
                        for (snap in snapshot.children) {
                            val contact = snap.getValue(Contact::class.java) as Contact

                            contactList.add(contact)
                        }

                        contactList.reverse()
                        initAdapter()
                    }

                    contactsEmpty()

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun contactsEmpty() {
        binding.textInfo.text = if (contactList.isEmpty()) {
            getText(R.string.text_contact_list_empty_fragment)
        } else {
            ""
        }
    }

    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        contactAdapter = ContactAdapter(requireContext(), contactList) { contact, select ->
            optionSelect(contact, select)
        }
        binding.rvTask.adapter = contactAdapter
    }

    private fun optionSelect(contact: Contact, select: Int) {
        when (select) {
            ContactAdapter.SELECT_REMOVE -> {
                deleteContact(contact)
            }
            ContactAdapter.SELECT_EDIT -> {
                //val action = HomeFragmentDirections
                    //.actionHomeFragmentToFormTaskFragment(contact)
                //findNavController().navigate(action)
            }

        }
    }

    private fun updateContact(contact: Contact) {
        FirebaseHelper
            .getDatabase()
            .child("contact")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(contact.id)
            .setValue(contact)
            .addOnCompleteListener { contact ->
                if (contact.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        R.string.text_task_update_sucess,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showBottomSheet(message = R.string.error_generic)
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                showBottomSheet(message = R.string.error_generic)
            }
    }

    private fun deleteContact(contact: Contact) {
        showBottomSheet(
            titleButton = R.string.text_button_confirm,
            message = R.string.text_message_delete_contact_fragment,
            onClick = {
                FirebaseHelper
                    .getDatabase()
                    .child("contact")
                    .child(FirebaseHelper.getIdUser() ?: "")
                    .child(contact.id)
                    .removeValue()
                    .addOnCompleteListener { contact ->
                        if (contact.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                R.string.text_task_update_sucess,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showBottomSheet(message = R.string.error_generic)
                        }
                    }.addOnFailureListener {
                        binding.progressBar.isVisible = false
                        showBottomSheet(message = R.string.error_generic)
                    }

                contactList.remove(contact)
                contactAdapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), R.string.text_task_delete_sucess, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}