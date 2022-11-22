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
import com.digo.emdiabetes.databinding.FragmentFormGlycemiaBinding
import com.digo.emdiabetes.helper.BaseFragment
import com.digo.emdiabetes.helper.FirebaseHelper
import com.digo.emdiabetes.helper.initToolbar
import com.digo.emdiabetes.helper.showBottomSheet
import com.digo.emdiabetes.model.Glycemia

class FormGlycemiaFragment : BaseFragment() {

    private val args: FormGlycemiaFragmentArgs by navArgs()

    private var _binding: FragmentFormGlycemiaBinding? = null
    private val binding get() = _binding!!

    private lateinit var glycemia: Glycemia
    private var newGlycemia: Boolean = true
   // private var qtdMedication: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormGlycemiaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        initListeners()

        getArgs()
    }

    private fun getArgs() {
        args.glycemia.let {
            if (it != null){
                glycemia = it

                newGlycemia = false
            }
        }
    }

    private fun configGlycemia() {
        newGlycemia = false
        //statusTask = task.status
        binding.textToolbar.text = getString(R.string.text_editing_task_form_task_fragment)

        binding.edtDescription.setText(glycemia.glicemia)
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
        //quantidade de glicemia
        val glicemia = binding.edtGlycemia.text.toString().trim()
        val descricao = binding.edtDescription.text.toString().trim()

        if (glicemia.isNotEmpty()) {

            hideKeyboard()

            binding.progressBar.isVisible = true

            if (newGlycemia) glycemia= Glycemia()
            glycemia.glicemia = glicemia
            glycemia.descricao = descricao
            //task.status = statusTask

            saveGlycemia()
        } else {
            showBottomSheet(message = R.string.text_description_empty_form_task_fragment)
        }
    }

    private fun saveGlycemia() {
        FirebaseHelper
            .getDatabase()
            .child("glycemia")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(glycemia.id)
            .setValue(glycemia)
            .addOnCompleteListener { glycemia ->
                if (glycemia.isSuccessful) {
                    if (newGlycemia) { // Nova tarefa
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