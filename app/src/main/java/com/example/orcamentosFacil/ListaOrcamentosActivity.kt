package com.example.orcamentosFacil

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent


class ListaOrcamentosActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var txtQuantidade: TextView

    private var orcamentos: List<Orcamento> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_orcamentos)

        db = DatabaseHelper(this)

        listView = findViewById(R.id.listOrcamentos)

        listView.setOnItemClickListener { _, _, position, _ ->

            val orcamento = orcamentos[position]

            val tela = Intent(
                this,
                DetalheOrcamentoActivity::class.java
            )

            tela.putExtra(
                "orcamento_id",
                orcamento.id
            )

            startActivity(tela)
        }

        txtQuantidade = findViewById(R.id.txtQuantidadeOrcamentos)

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        carregarLista()
    }

    private fun carregarLista() {

        orcamentos = db.listarOrcamentos()

        val linhas = orcamentos.map { orcamento ->

            "${orcamento.numero}\n" +
                    "${orcamento.cliente} - ${orcamento.data}\n" +
                    "Total: ${Formatacao.moeda(orcamento.total)}"
        }

        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            linhas
        )

        txtQuantidade.text =
            "${orcamentos.size} orçamento(s)"
    }
}