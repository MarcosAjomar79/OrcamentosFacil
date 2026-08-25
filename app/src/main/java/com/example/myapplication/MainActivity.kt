package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.content.Intent
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnNovoOrcamento).setOnClickListener {
            startActivity(Intent(this, NovoOrcamentoActivity::class.java))
        }

        findViewById<Button>(R.id.btnOrcamentosSalvos).setOnClickListener {
            startActivity(Intent(this, ListaOrcamentosActivity::class.java))
        }

        findViewById<Button>(R.id.btnConfiguracoes).setOnClickListener {
            startActivity(Intent(this, ConfiguracoesActivity::class.java))
        }
    }
}