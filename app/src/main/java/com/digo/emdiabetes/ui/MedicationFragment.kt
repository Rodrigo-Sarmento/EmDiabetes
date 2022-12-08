package com.digo.emdiabetes.ui

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.digo.emdiabetes.R
import com.digo.emdiabetes.databinding.FragmentMedicationBinding

import com.digo.emdiabetes.helper.FirebaseHelper
import com.digo.emdiabetes.helper.showBottomSheet
import com.digo.emdiabetes.model.Medication
import com.digo.emdiabetes.ui.adapter.MedicationAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class MedicationFragment : Fragment() {

    private var _binding: FragmentMedicationBinding? = null
    private val binding get() = _binding!!

    private lateinit var medicationAdapter: MedicationAdapter

    private val medicationList = mutableListOf<Medication>()

    lateinit var timePickerDialog: TimePickerDialog
    lateinit var calendario: Calendar
    var horaAtual = 0
    var minutosAtuais = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()

        getMedications()
    }

    //botão ADD
    private fun initClicks() {
        binding.fabAddMedication.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormTaskFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getMedications() {
        FirebaseHelper
            .getDatabase()
            .child("medication")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        medicationList.clear()
                        for (snap in snapshot.children) {
                            val medication = snap.getValue(Medication::class.java) as Medication

                            medicationList.add(medication)
                        }

                        medicationList.reverse()
                        initAdapter()
                    }

                    medicationsEmpty()

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun medicationsEmpty() {
        binding.textInfo.text = if (medicationList.isEmpty()) {
            getText(R.string.text_medication_list_empty_fragment)
        } else {
            ""
        }
    }

    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        medicationAdapter = MedicationAdapter(requireContext(), medicationList) { task, select ->
            optionSelect(task, select)
        }
        binding.rvTask.adapter = medicationAdapter
    }


    private fun alarme(medication: Medication){
        calendario = Calendar.getInstance()
        horaAtual = calendario.get(Calendar.HOUR_OF_DAY)
        minutosAtuais = calendario.get(Calendar.MINUTE)
        timePickerDialog = TimePickerDialog(requireContext(), {
                timePicker: TimePicker, hourOfDay: Int, minutes: Int ->
            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            intent.putExtra(AlarmClock.EXTRA_HOUR, hourOfDay)
            intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes)
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, "hora da medicação: "+medication.nome+" Sua dosagem é de: "+medication.dosagem)
            startActivity(intent)
        },horaAtual,minutosAtuais,true)
        timePickerDialog.show()
    }



    private fun optionSelect(medication: Medication, select: Int) {
        when (select) {
            MedicationAdapter.SELECT_REMOVE -> {
                deleteMedication(medication)
            }
            MedicationAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(medication)
                findNavController().navigate(action)
            }
            MedicationAdapter.SELECT_DETAILS ->{
                alarme(medication)
            }
        }
    }

    private fun updateMedication(medication: Medication) {
        FirebaseHelper
            .getDatabase()
            .child("medication")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(medication.id)
            .setValue(medication)
            .addOnCompleteListener { medication ->
                if (medication.isSuccessful) {
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

    private fun deleteMedication(medication: Medication) {
        showBottomSheet(
            titleButton = R.string.text_button_confirm,
            message =R.string.text_message_delete_medication_fragment,
            onClick = {
                FirebaseHelper
                    .getDatabase()
                    .child("medication")
                    .child(FirebaseHelper.getIdUser() ?: "")
                    .child(medication.id)
                    .removeValue()
                    .addOnCompleteListener { medication ->
                        if (medication.isSuccessful) {
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

                medicationList.remove(medication)
                medicationAdapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), R.string.text_task_delete_sucess, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}