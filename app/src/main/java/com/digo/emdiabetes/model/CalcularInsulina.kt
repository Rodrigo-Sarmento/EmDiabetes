package com.digo.emdiabetes.model

class CalcularInsulina {

    private var resultado = 0.0
    private var resultadoTotal = 0.0

    fun calcular(peso:Double, carboidrato:Double){
        if (peso>=45 && peso<49){
            resultado = carboidrato / 16
            resultadoTotal = resultado
        }else if (peso>=49 && peso<58){
            resultado = carboidrato / 15
            resultadoTotal = resultado
        }else if (peso>=58 && peso<63){
            resultado = carboidrato / 14
            resultadoTotal = resultado
        }else if (peso>=63 && peso<67){
            resultado = carboidrato / 13
            resultadoTotal = resultado
        }else if (peso>=67 && peso<76){
            resultado = carboidrato / 12
            resultadoTotal = resultado
        }else if (peso>=76 && peso<81){
            resultado = carboidrato / 11
            resultadoTotal = resultado
        }else if (peso>=81 && peso<85){
            resultado = carboidrato / 10
            resultadoTotal = resultado
        }else if (peso>=85 && peso<90){
            resultado = carboidrato / 9
            resultadoTotal = resultado
        }else if (peso>=90 && peso<99){
            resultado = carboidrato / 8
            resultadoTotal = resultado
        }else if (peso>=99 && peso<108){
            resultado = carboidrato / 7
            resultadoTotal = resultado
        }else if (peso>=108){
            resultado = carboidrato / 6
            resultadoTotal = resultado
        }else if (peso<45){
            resultado = carboidrato / 17
            resultadoTotal = resultado
        }
    }

    fun resultado():Double{
        return resultadoTotal
    }
}