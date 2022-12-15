package com.digo.emdiabetes.ui


import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digo.emdiabetes.R
import com.digo.emdiabetes.databinding.FragmentDietBinding

import com.digo.emdiabetes.helper.FirebaseHelper
import com.digo.emdiabetes.helper.showBottomSheet
import com.digo.emdiabetes.model.Diet
import com.digo.emdiabetes.model.Glycemia
import com.digo.emdiabetes.ui.adapter.DietAdapter
import com.digo.emdiabetes.ui.adapter.GlycemiaAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*


class DietFragment : Fragment() {

    private var _binding: FragmentDietBinding? = null
    private val binding get() = _binding!!

    private lateinit var dietAdapter: DietAdapter

    private val dietList = mutableListOf<Diet>()

    lateinit var timePickerDialog: TimePickerDialog
    lateinit var calendario: Calendar
    var horaAtual = 0
    var minutosAtuais = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getDiet()

    }

    //botão ADD
    private fun initClicks() {
        binding.fabAddContact.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormDietFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getDiet() {
        FirebaseHelper
            .getDatabase()
            .child("diet")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        dietList.clear()
                        for (snap in snapshot.children) {
                            val diet = snap.getValue(Diet::class.java) as Diet

                            dietList.add(diet)
                        }

                        dietList.reverse()
                        initAdapter()
                    }

                    dietEmpty()

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun dietEmpty() {
        binding.textInfo.text = if (dietList.isEmpty()) {
            getText(R.string.text_diet_list_empty_fragment)
        } else {
            ""
        }
    }

    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        dietAdapter = DietAdapter(requireContext(), dietList) { diet, select ->
            optionSelect(diet, select)
        }
        binding.rvTask.adapter = dietAdapter
    }

    private fun alarme(diet: Diet){
        calendario = Calendar.getInstance()
        horaAtual = calendario.get(Calendar.HOUR_OF_DAY)
        minutosAtuais = calendario.get(Calendar.MINUTE)
        timePickerDialog = TimePickerDialog(requireContext(), {
                timePicker: TimePicker, hourOfDay: Int, minutes: Int ->
            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            intent.putExtra(AlarmClock.EXTRA_HOUR, hourOfDay)
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes)
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Hora do "+diet.refeicao+"! Alimento(s):"+diet.alimento)
            startActivity(intent)
        },horaAtual,minutosAtuais,true)
        timePickerDialog.show()
    }

    private fun optionSelect(diet: Diet, select: Int) {
        when (select) {
            DietAdapter.SELECT_REMOVE -> {
                deleteDiet(diet)
            }
            DietAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormDietFragment(diet)
                findNavController().navigate(action)
            }
            DietAdapter.SELECT_DETAILS ->{
                alarme(diet)
            }
        }
    }

    private fun updateDiet(diet: Diet) {
        FirebaseHelper
            .getDatabase()
            .child("diet")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(diet.id)
            .setValue(diet)
            .addOnCompleteListener { diet ->
                if (diet.isSuccessful) {
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

    private fun deleteDiet(diet: Diet) {
        showBottomSheet(
            titleButton = R.string.text_button_confirm,
            message = R.string.text_message_delete_diet_fragment,
            onClick = {
                FirebaseHelper
                    .getDatabase()
                    .child("diet")
                    .child(FirebaseHelper.getIdUser() ?: "")
                    .child(diet.id)
                    .removeValue()
                    .addOnCompleteListener { diet ->
                        if (diet.isSuccessful) {
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

                dietList.remove(diet)
                dietAdapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), R.string.text_task_delete_sucess, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}