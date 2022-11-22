package com.digo.emdiabetes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.digo.emdiabetes.R
import com.digo.emdiabetes.databinding.FragmentFormContactBinding
import com.digo.emdiabetes.helper.BaseFragment
import com.digo.emdiabetes.helper.FirebaseHelper
import com.digo.emdiabetes.helper.initToolbar
import com.digo.emdiabetes.helper.showBottomSheet
import com.digo.emdiabetes.model.Contact

class FormContactFragment : BaseFragment() {

    private val args: FormContactFragmentArgs by navArgs()

    private var _binding: FragmentFormContactBinding? = null
    private val binding get() = _binding!!

    private lateinit var contact: Contact
    private var newContact: Boolean = true
    // private var qtdMedication: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.contact.let {
            if (it != null){
                contact = it

                newContact = false
            }
        }
    }

    private fun configContact() {
        newContact = false
        //statusTask = task.status
        binding.textToolbar.text = getString(R.string.text_editing_task_form_task_fragment)

        binding.edtDescription.setText(contact.nome)
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
        //nome contato
        val nome = binding.edtDescription.text.toString().trim()
        val numero = binding.edtNumero.text.toString().trim()

        if (nome.isNotEmpty()) {

            hideKeyboard()

            binding.progressBar.isVisible = true

            if (newContact) contact= Contact()
            contact.nome = nome
            contact.numero = Integer.valueOf(numero)
            //task.status = statusTask

            saveContact()
        } else {
            showBottomSheet(message = R.string.text_description_empty_form_task_fragment)
        }
    }

    private fun saveContact() {
        FirebaseHelper
            .getDatabase()
            .child("contact")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(contact.id)
            .setValue(contact)
            .addOnCompleteListener { contact ->
                if (contact.isSuccessful) {
                    if (newContact) { // Nova tarefa
                        findNavController().popBackStack()
                        Toast.makeText(
                            requireContext(),
                            R.string.text_save_task_sucess_form_task_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else { // Editando tarefa
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            R.string.text_update_task_sucess_form_task_fragment,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.text_erro_save_task_form_task_fragment, Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), R.string.text_erro_save_task_form_task_fragment, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}