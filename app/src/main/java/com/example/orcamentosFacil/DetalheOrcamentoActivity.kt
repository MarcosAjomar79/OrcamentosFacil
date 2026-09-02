package com.example.orcamentosFacil

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.ClipData
import android.content.Intent
class DetalheOrcamentoActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    private var orcamentoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detalhe_orcamento)

        db = DatabaseHelper(this)

        orcamentoId = intent.getLongExtra(
            "orcamento_id",
            -1
        )

        findViewById<Button>(
            R.id.btnGerarPdf
        ).setOnClickListener {

            gerarPdf()
        }

        if (orcamentoId == -1L) {

            Toast.makeText(
                this,
                "Orçamento não encontrado.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        findViewById<Button>(R.id.btnExcluir)
            .setOnClickListener {

                confirmarExclusao()
            }

        findViewById<Button>(R.id.btnVoltarDetalhe)
            .setOnClickListener {

                finish()
            }

        carregarDados()
    }

    private fun carregarDados() {

        val orcamento = db.buscarOrcamento(orcamentoId)

        val itens = db.buscarItens(orcamentoId)

        if (orcamento == null) {

            Toast.makeText(
                this,
                "Registro não encontrado.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        findViewById<TextView>(R.id.txtNumero).text =
            orcamento.numero

        findViewById<TextView>(R.id.txtDadosCliente).text =
            "Cliente: ${orcamento.cliente}\n" +
                    "Telefone: ${orcamento.telefone}\n" +
                    "Data: ${orcamento.data}\n" +
                    "Validade: ${orcamento.validade}"

        findViewById<TextView>(R.id.txtTotalDetalhe).text =
            "Total: ${Formatacao.moeda(orcamento.total)}"

        findViewById<TextView>(R.id.txtObservacoesDetalhe).text =
            if (orcamento.observacoes.isBlank()) {
                ""
            } else {
                "Observações: ${orcamento.observacoes}"
            }

        val linhas = itens.mapIndexed { indice, item ->

            "${indice + 1}. ${item.descricao}\n" +
                    "${item.quantidade} x " +
                    "${Formatacao.moeda(item.valorUnitario)} = " +
                    Formatacao.moeda(item.subtotal)
        }

        findViewById<ListView>(
            R.id.listItensDetalhe
        ).adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            linhas
        )
    }

    private fun confirmarExclusao() {

        AlertDialog.Builder(this)
            .setTitle("Excluir orçamento")
            .setMessage(
                "Deseja realmente excluir este orçamento?"
            )
            .setPositiveButton("Excluir") { _, _ ->

                db.excluirOrcamento(orcamentoId)

                Toast.makeText(
                    this,
                    "Orçamento excluído.",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .setNegativeButton(
                "Cancelar",
                null
            )
            .show()
    }
    private fun gerarPdf() {

        val orcamento =
            db.buscarOrcamento(orcamentoId)

        if (orcamento == null) {

            Toast.makeText(
                this,
                "Orçamento não encontrado.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val itens =
            db.buscarItens(orcamentoId)

        try {

            val uri = PdfHelper.gerar(
                this,
                orcamento,
                itens
            )

            val compartilhar =
                Intent(Intent.ACTION_SEND).apply {

                    type = "application/pdf"

                    putExtra(
                        Intent.EXTRA_STREAM,
                        uri
                    )

                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    clipData =
                        ClipData.newRawUri(
                            "Orçamento",
                            uri
                        )
                }

            startActivity(
                Intent.createChooser(
                    compartilhar,
                    "Compartilhar orçamento"
                )
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Não foi possível gerar o PDF: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}