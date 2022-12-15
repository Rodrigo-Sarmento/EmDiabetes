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
import com.digo.emdiabetes.databinding.FragmentFormDietBinding
import com.digo.emdiabetes.helper.BaseFragment
import com.digo.emdiabetes.helper.FirebaseHelper
import com.digo.emdiabetes.helper.initToolbar
import com.digo.emdiabetes.helper.showBottomSheet
import com.digo.emdiabetes.model.Diet

class FormDietFragment : BaseFragment() {

    private val args: FormDietFragmentArgs by navArgs()

    private var _binding: FragmentFormDietBinding? = null
    private val binding get() = _binding!!

    private lateinit var diet: Diet
    private var newDiet: Boolean = true
    // private var qtdMedication: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormDietBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.diet.let {
            if (it != null){
                diet = it

                newDiet = false
            }
        }
    }

    private fun configDiet() {
        newDiet = false
        //statusTask = task.status
        binding.textToolbar.text = getString(R.string.text_editing_form_fragment)

        binding.edtDescription.setText(diet.alimento)
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
        val refeicao = binding.edtDescription.text.toString().trim()
        val alimento = binding.edtAlimento.text.toString().trim()

        if (refeicao.isNotEmpty()) {

            hideKeyboard()

            binding.progressBar.isVisible = true

            if (newDiet) diet= Diet()
            diet.refeicao = refeicao
            diet.alimento = alimento
            //task.status = statusTask

            saveDiet()
        } else {
            showBottomSheet(message = R.string.text_description_empty_form_fragment)
        }
    }

    private fun saveDiet() {
        FirebaseHelper
            .getDatabase()
            .child("diet")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(diet.id)
            .setValue(diet)
            .addOnCompleteListener { contact ->
                if (contact.isSuccessful) {
                    if (newDiet) { // Nova tarefa
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