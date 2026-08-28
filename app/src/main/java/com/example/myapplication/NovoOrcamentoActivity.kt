package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NovoOrcamentoActivity : AppCompatActivity() {

    // Lista temporária dos itens enquanto o orçamento está sendo montado.
    private val itens = mutableListOf<ItemOrcamento>()

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var txtTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_orcamento)

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
}