package com.example.orcamentosFacil

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfiguracoesActivity : AppCompatActivity() {

    private val prefsName = "dados_empresa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_configuracoes)

        val prefs = getSharedPreferences(
            prefsName,
            MODE_PRIVATE
        )

        val empresa =
            findViewById<EditText>(R.id.edtEmpresa)

        val telefone =
            findViewById<EditText>(R.id.edtEmpresaTelefone)

        val email =
            findViewById<EditText>(R.id.edtEmpresaEmail)

        val endereco =
            findViewById<EditText>(R.id.edtEmpresaEndereco)

        val pix =
            findViewById<EditText>(R.id.edtEmpresaPix)

        // Carrega os dados salvos anteriormente.
        empresa.setText(
            prefs.getString("empresa", "")
        )

        telefone.setText(
            prefs.getString("telefone", "")
        )

        email.setText(
            prefs.getString("email", "")
        )

        endereco.setText(
            prefs.getString("endereco", "")
        )

        pix.setText(
            prefs.getString("pix", "")
        )

        findViewById<Button>(
            R.id.btnSalvarConfiguracoes
        ).setOnClickListener {

            prefs.edit()
                .putString(
                    "empresa",
                    empresa.text.toString().trim()
                )
                .putString(
                    "telefone",
                    telefone.text.toString().trim()
                )
                .putString(
                    "email",
                    email.text.toString().trim()
                )
                .putString(
                    "endereco",
                    endereco.text.toString().trim()
                )
                .putString(
                    "pix",
                    pix.text.toString().trim()
                )
                .apply()

            Toast.makeText(
                this,
                "Dados salvos!",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        findViewById<Button>(
            R.id.btnVoltarConfiguracoes
        ).setOnClickListener {

            finish()
        }
    }
}