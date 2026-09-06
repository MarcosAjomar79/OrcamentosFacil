package com.example.orcamentosFacil

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged


class ListaOrcamentosActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var txtQuantidade: TextView
    private lateinit var edtBusca: EditText
    private lateinit var txtListaVazia: TextView

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

        edtBusca =
            findViewById(R.id.edtBuscarOrcamento)

        txtListaVazia =
            findViewById(R.id.txtListaVazia)

        edtBusca.doOnTextChanged { texto, _, _, _ ->

            carregarLista(
                texto?.toString() ?: ""
            )
        }

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
    }
    override fun onResume() {
        super.onResume()

        carregarLista(
            edtBusca.text.toString()
        )
    }
    private fun carregarLista(termo: String = "") {

        orcamentos =
            db.buscarOrcamentos(termo)

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

        if (orcamentos.isEmpty()) {

            listView.visibility = View.GONE
            txtListaVazia.visibility = View.VISIBLE

            txtListaVazia.text =
                if (termo.isBlank()) {
                    "Nenhum orçamento cadastrado."
                } else {
                    "Nenhum orçamento encontrado.\nTente outro cliente ou número."
                }

        } else {

            listView.visibility = View.VISIBLE
            txtListaVazia.visibility = View.GONE
        }
    }
}