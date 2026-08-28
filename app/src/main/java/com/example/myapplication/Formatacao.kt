package com.example.myapplication

import java.text.NumberFormat
import java.util.Locale

object Formatacao {

    private val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    fun moeda (valor: Double): String {
        return formatoMoeda.format(valor)
    }

    fun numeroDigitado(texto: String): Double? {
        val limpo = texto
            .trim()
            .replace("R$", "")
            .replace("", "")

        if (limpo.isEmpty()) return null

        val normalizado = if (limpo.contains(",")) {
            limpo
                .replace(".", "")
                .replace(",", ".")
        } else {
            limpo
        }

        return normalizado.toDoubleOrNull()
    }
}