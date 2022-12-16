package com.digo.emdiabetes.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digo.emdiabetes.R
import com.digo.emdiabetes.databinding.FragmentFormMedicationBinding
import com.digo.emdiabetes.helper.BaseFragment
import com.digo.emdiabetes.helper.FirebaseHelper
import com.digo.emdiabetes.helper.initToolbar
import com.digo.emdiabetes.helper.showBottomSheet
import com.digo.emdiabetes.model.Medication

class FormMedicationFragment : BaseFragment() {

    private val args: FormMedicationFragmentArgs by navArgs()

    private var _binding: FragmentFormMedicationBinding? = null
    private val binding get() = _binding!!

    private lateinit var medication: Medication
    private var newMedication: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.medication.let {
            if (it != null){
                medication = it

                newMedication = false
            }
        }
    }

    private fun configMedication() {
        newMedication = false
        //statusTask = task.status
        binding.textToolbar.text = getString(R.string.text_editing_form_fragment)

        binding.edtDescription.setText(medication.nome)
        //setStatus()
    }

    /*
    private fun setStatus() {
        binding.radioGroup.check(
            when (task.status) {
                0 -> {
                    R.id.rbTodo
                }
                1 -> {
                    R.id.rbDoing
                }
                else -> {
                    R.id.rbDone
                }
            }
        )
    }
*/

    private fun initListeners() {
        binding.btnSave.setOnClickListener { validateData() }

    }


    private fun validateData() {
        val nome = binding.edtDescription.text.toString().trim()
        val dosagem = binding.edtDosagem.text.toString().trim()

        if (nome.isNotEmpty()) {

            hideKeyboard()

            binding.progressBar.isVisible = true

            if (newMedication) medication = Medication()
            medication.nome = nome
            medication.dosagem = dosagem
            //task.status = statusTask

            saveMedication()
        } else {
            showBottomSheet(message = R.string.text_description_empty_form_fragment)
        }
    }

    private fun saveMedication() {
        FirebaseHelper
            .getDatabase()
            .child("medication")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(medication.id)
            .setValue(medication)
            .addOnCompleteListener { medication ->
                if (medication.isSuccessful) {
                    if (newMedication) { // Nova tarefa
                        findNavController().popBackStack()
                        Toast.makeText(
                            requireContext(),
                            R.string.text_save_sucess_form_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else { // Editando tarefa
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            R.string.text_update_sucess_form_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.text_erro_save_form_fragment, Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), R.string.text_erro_save_form_fragment, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}