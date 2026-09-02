package com.example.orcamentosFacil

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.ListView
import android.widget.Toast
import android.widget.Button
import kotlin.toString

class NovoOrcamentoActivity : AppCompatActivity() {

    private val itens = mutableListOf<ItemOrcamento>()

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var txtTotal: TextView
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_novo_orcamento)
        db = DatabaseHelper(this)

        txtTotal = findViewById(R.id.txtTotal)

        val listItens = findViewById<ListView>(R.id.listItens)

        // O adapter transforma Strings em linhas visíveis no ListView.
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )

        listItens.adapter = adapter

        // Valores iniciais.
        findViewById<EditText>(R.id.edtQuantidade).setText("1")
        findViewById<EditText>(R.id.edtValidade).setText("7 dias")

        findViewById<Button>(R.id.btnAdicionarItem).setOnClickListener {
            adicionarItem()
        }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.btnSalvarOrcamento).setOnClickListener {
            Toast.makeText(this, "BOTÃO SALVAR CLICADO", Toast.LENGTH_SHORT).show()
            salvarOrcamento()
        }

        // Toque longo remove um item.
        listItens.setOnItemLongClickListener { _, _, position, _ ->

            itens.removeAt(position)

            atualizarLista()

            Toast.makeText(
                this,
                "Item removido.",
                Toast.LENGTH_SHORT
            ).show()

            true
        }
    }

    private fun adicionarItem() {

        val descricao = findViewById<EditText>(R.id.edtDescricaoItem)
            .text
            .toString()
            .trim()

        val quantidade = findViewById<EditText>(R.id.edtQuantidade)
            .text
            .toString()
            .trim()
            .toIntOrNull()

        val valor = Formatacao.numeroDigitado(
            findViewById<EditText>(R.id.edtValorUnitario)
                .text
                .toString()
        )

        if (
            descricao.isEmpty() ||
            quantidade == null ||
            quantidade <= 0 ||
            valor == null ||
            valor < 0
        ) {

            Toast.makeText(
                this,
                "Preencha descrição, quantidade e valor corretamente.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val item = ItemOrcamento(
            descricao = descricao,
            quantidade = quantidade,
            valorUnitario = valor
        )

        itens.add(item)

        // Limpa campos para o próximo item.
        findViewById<EditText>(R.id.edtDescricaoItem).text.clear()
        findViewById<EditText>(R.id.edtQuantidade).setText("1")
        findViewById<EditText>(R.id.edtValorUnitario).text.clear()

        atualizarLista()
    }

    private fun atualizarLista() {

        val linhas = itens.mapIndexed { indice, item ->

            "${indice + 1}. ${item.descricao}\n" +
                    "${item.quantidade} x " +
                    "${Formatacao.moeda(item.valorUnitario)} = " +
                    Formatacao.moeda(item.subtotal)
        }

        adapter.clear()
        adapter.addAll(linhas)
        adapter.notifyDataSetChanged()

        val total = itens.sumOf { it.subtotal }

        txtTotal.text =
            "Total: ${Formatacao.moeda(total)}"
    }

    private fun salvarOrcamento() {

        val cliente = findViewById<EditText>(R.id.edtCliente)
            .text
            .toString()
            .trim()

        val telefone = findViewById<EditText>(R.id.edtTelefone)
            .text
            .toString()
            .trim()

        val validade = findViewById<EditText>(R.id.edtValidade)
            .text
            .toString()
            .trim()

        val observacoes = findViewById<EditText>(R.id.edtObservacoes)
            .text
            .toString()
            .trim()

        if (cliente.isEmpty()) {

            Toast.makeText(
                this,
                "Informe o nome do cliente.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (itens.isEmpty()) {

            Toast.makeText(
                this,
                "Adicione pelo menos um item.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val id = db.salvarOrcamento(
            cliente = cliente,
            telefone = telefone,
            validade = validade,
            observacoes = observacoes,
            itens = itens
        )

        Toast.makeText(
            this,
            "Orçamento salvo! ID: $id",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

}