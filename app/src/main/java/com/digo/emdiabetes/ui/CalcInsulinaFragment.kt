package com.digo.emdiabetes.ui

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.AlarmClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.*
import androidx.navigation.fragment.findNavController
import com.digo.emdiabetes.R
import com.digo.emdiabetes.databinding.FragmentCalcInsulinaBinding
import com.digo.emdiabetes.model.CalcularInsulina
import java.text.NumberFormat
import java.util.*


class CalcInsulinaFragment : Fragment() {
    private var _binding: FragmentCalcInsulinaBinding? = null
    private val binding get() = _binding!!

    private lateinit var edit_peso: EditText
    private lateinit var edit_carboidrato: EditText
    private lateinit var bt_calcular: Button
    private lateinit var txt_resultado_ui: TextView
    private lateinit var ic_redefinir_dados: ImageView
    private lateinit var bt_lembrete:Button
    private lateinit var bt_alarme:Button
    private lateinit var txt_hora: TextView
    private lateinit var txt_minutos: TextView

    private lateinit var calcularInsulina: CalcularInsulina
    private var resultado = 0.0

    lateinit var timePickerDialog: TimePickerDialog
    lateinit var calendario: Calendar
    var horaAtual = 0
    var minutosAtuais = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCalcInsulinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iniciarComponentes()
        calcularInsulina = CalcularInsulina()

        bt_calcular.setOnClickListener{
            if(edit_peso.text.toString().isEmpty()){
                Toast.makeText(requireContext(), R.string.toast_informe_peso, Toast.LENGTH_SHORT).show()
            }else if (edit_carboidrato.text.toString().isEmpty()){
                Toast.makeText(requireContext(), R.string.toast_informe_carboidrato, Toast.LENGTH_SHORT).show()
            }else{
                val peso = edit_peso.text.toString().toDouble()
                val carboidrato = edit_carboidrato.text.toString().toDouble()
                calcularInsulina.calcular(peso, carboidrato)
                resultado = calcularInsulina.resultado()
                val formatar = NumberFormat.getNumberInstance(Locale("pt","BR"))
                formatar.isGroupingUsed = false
                txt_resultado_ui.text = formatar.format(resultado) + " UI"
            }
        }
        ic_redefinir_dados.setOnClickListener{
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle(R.string.dialog_titulo)
                .setMessage(R.string.dialog_desc)
                .setPositiveButton("Ok",{dialogInterface, i ->
                    edit_peso.setText("")
                    edit_carboidrato.setText("")
                    txt_resultado_ui.text = ""
                })
            alertDialog.setNegativeButton("Cancelar", {dialogInterface, i ->

            })
            val dialog = alertDialog.create()
            dialog.show()
        }

        bt_lembrete.setOnClickListener{

            calendario = Calendar.getInstance()
            horaAtual = calendario.get(Calendar.HOUR_OF_DAY)
            minutosAtuais = calendario.get(Calendar.MINUTE)
            timePickerDialog = TimePickerDialog(requireContext(), {
                    timePicker: TimePicker, hourOfDay: Int, minutes: Int ->
                txt_hora.text = String.format("%02d",hourOfDay)
                txt_minutos.text = String.format("%02d",minutes)
            },horaAtual,minutosAtuais,true)
            timePickerDialog.show()
        }

        bt_alarme.setOnClickListener{
            if (!txt_hora.text.toString().isEmpty() && !txt_minutos.text.toString().isEmpty()){
                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                intent.putExtra(AlarmClock.EXTRA_HOUR, txt_hora.text.toString().toInt())
                intent.putExtra(AlarmClock.EXTRA_MINUTES, txt_minutos.text.toString().toInt())
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.alarme_mensagem)+" Sua dosagem é: "+txt_resultado_ui.text)
                startActivity(intent)
            }
        }
        binding.logoEmDBack.setOnClickListener{
            backHome()
        }
    }

    private fun backHome(){
        findNavController().navigate(R.id.action_calcInsulinaFragment_to_homeFragment)
    }


    private fun iniciarComponentes(){
        edit_peso = binding.editPeso
        edit_carboidrato = binding.editCarboidrato
        bt_calcular = binding.btCalcular
        txt_resultado_ui = binding.txtResultadoUi
        ic_redefinir_dados = binding.icRedefinir
        bt_alarme = binding.btAlarme
        bt_lembrete = binding.btDefinirLembrete
        txt_hora = binding.txtHora
        txt_minutos = binding.txtMinutos
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}