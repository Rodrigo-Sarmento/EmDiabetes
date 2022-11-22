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
import com.digo.emdiabetes.databinding.FragmentGlycemiaBinding
import com.digo.emdiabetes.helper.FirebaseHelper
import com.digo.emdiabetes.helper.showBottomSheet
import com.digo.emdiabetes.model.Glycemia
import com.digo.emdiabetes.ui.adapter.GlycemiaAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class GlycemiaFragment : Fragment() {


    private var _binding: FragmentGlycemiaBinding? = null
    private val binding get() = _binding!!

    private lateinit var glycemiaAdapter: GlycemiaAdapter

    private val glycemiaList = mutableListOf<Glycemia>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGlycemiaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        getGlycemias()
    }

    //botão ADD
    private fun initClicks() {
        binding.fabAddGlycemia.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormGlycemiaFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getGlycemias() {
        FirebaseHelper
            .getDatabase()
            .child("glycemia")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        glycemiaList.clear()
                        for (snap in snapshot.children) {
                            val glycemia = snap.getValue(Glycemia::class.java) as Glycemia

                            glycemiaList.add(glycemia)
                        }

                        glycemiaList.reverse()
                        initAdapter()
                    }

                    glycemiasEmpty()
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun glycemiasEmpty() {
        binding.textInfo.text = if (glycemiaList.isEmpty()) {
            getText(R.string.text_glycemia_list_empty_fragment)
        } else {
            ""
        }
    }

    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        glycemiaAdapter = GlycemiaAdapter(requireContext(), glycemiaList) { glycemia, select ->
            optionSelect(glycemia, select)
        }
        binding.rvTask.adapter = glycemiaAdapter
    }

    private fun optionSelect(glycemia: Glycemia, select: Int) {
        when (select) {
            GlycemiaAdapter.SELECT_REMOVE -> {
                deleteGlycemia(glycemia)
            }
            GlycemiaAdapter.SELECT_EDIT -> {
                //val action = HomeFragmentDirections
                    //.actionHomeFragmentToFormTaskFragment(glycemia)
                //findNavController().navigate(action)
            }
        }
    }

    private fun updateGlycemia(glycemia: Glycemia) {
        FirebaseHelper
            .getDatabase()
            .child("glycemia")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(glycemia.id)
            .setValue(glycemia)
            .addOnCompleteListener { glycemia ->
                if (glycemia.isSuccessful) {
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

    private fun deleteGlycemia(glycemia: Glycemia) {
        showBottomSheet(
            titleButton = R.string.text_button_confirm,
            message = R.string.text_message_delete_glycemia_fragment,
            onClick = {
                FirebaseHelper
                    .getDatabase()
                    .child("glycemia")
                    .child(FirebaseHelper.getIdUser() ?: "")
                    .child(glycemia.id)
                    .removeValue()
                    .addOnCompleteListener { glycemia ->
                        if (glycemia.isSuccessful) {
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

                glycemiaList.remove(glycemia)
                glycemiaAdapter.notifyDataSetChanged()

                Toast.makeText(
                    requireContext(),
                    R.string.text_task_delete_sucess,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}