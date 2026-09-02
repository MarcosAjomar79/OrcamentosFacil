package com.example.orcamentosFacil

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PdfHelper {

    fun gerar(
        context: Context,
        orcamento: Orcamento,
        itens: List<ItemOrcamento>
    ): Uri {

        val prefs = context.getSharedPreferences(
            "dados_empresa",
            Context.MODE_PRIVATE
        )

        val empresa =
            prefs.getString("empresa", "") ?: ""

        val telefoneEmpresa =
            prefs.getString("telefone", "") ?: ""

        val emailEmpresa =
            prefs.getString("email", "") ?: ""

        val enderecoEmpresa =
            prefs.getString("endereco", "") ?: ""

        val pixEmpresa =
            prefs.getString("pix", "") ?: ""

        val pdf = PdfDocument()

        // Aproximação de uma folha A4 em pontos.
        val largura = 595
        val altura = 842

        val margemEsquerda = 40f
        val margemDireita = 555f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        var numeroPagina = 1

        var pagina = pdf.startPage(
            PdfDocument.PageInfo.Builder(
                largura,
                altura,
                numeroPagina
            ).create()
        )

        var canvas = pagina.canvas

        var y = 50f

        fun novaPagina() {

            pdf.finishPage(pagina)

            numeroPagina++

            pagina = pdf.startPage(
                PdfDocument.PageInfo.Builder(
                    largura,
                    altura,
                    numeroPagina
                ).create()
            )

            canvas = pagina.canvas

            y = 50f
        }

        fun cabecalhoItens() {

            paint.textSize = 10f
            paint.typeface = Typeface.DEFAULT_BOLD

            canvas.drawText(
                "Descrição",
                margemEsquerda,
                y,
                paint
            )

            canvas.drawText(
                "Qtd",
                335f,
                y,
                paint
            )

            canvas.drawText(
                "Valor",
                385f,
                y,
                paint
            )

            canvas.drawText(
                "Subtotal",
                475f,
                y,
                paint
            )

            y += 8f

            canvas.drawLine(
                margemEsquerda,
                y,
                margemDireita,
                y,
                paint
            )

            y += 18f

            paint.typeface = Typeface.DEFAULT
        }

        // ---------------- EMPRESA ----------------

        paint.textSize = 22f
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            if (empresa.isBlank()) {
                "ORÇAMENTO"
            } else {
                empresa
            },
            margemEsquerda,
            y,
            paint
        )

        y += 28f

        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT

        listOf(
            if (telefoneEmpresa.isBlank()) {
                ""
            } else {
                "Telefone: $telefoneEmpresa"
            },

            if (emailEmpresa.isBlank()) {
                ""
            } else {
                "E-mail: $emailEmpresa"
            },

            if (enderecoEmpresa.isBlank()) {
                ""
            } else {
                "Endereço: $enderecoEmpresa"
            }

        ).filter {
            it.isNotBlank()
        }.forEach { linha ->

            canvas.drawText(
                linha.take(90),
                margemEsquerda,
                y,
                paint
            )

            y += 15f
        }

        y += 10f

        // ---------------- ORÇAMENTO ----------------

        paint.textSize = 16f
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            orcamento.numero,
            margemEsquerda,
            y,
            paint
        )

        y += 24f

        paint.textSize = 11f
        paint.typeface = Typeface.DEFAULT

        val dados = listOf(
            "Cliente: ${orcamento.cliente}",
            "Telefone: ${orcamento.telefone}",
            "Data: ${orcamento.data}",
            "Validade: ${orcamento.validade}"
        )

        dados.forEach { linha ->

            canvas.drawText(
                linha.take(90),
                margemEsquerda,
                y,
                paint
            )

            y += 17f
        }

        y += 15f

        cabecalhoItens()

        // ---------------- ITENS ----------------

        itens.forEach { item ->

            if (y > 760f) {

                novaPagina()

                paint.textSize = 12f
                paint.typeface = Typeface.DEFAULT_BOLD

                canvas.drawText(
                    "${orcamento.numero} - continuação",
                    margemEsquerda,
                    y,
                    paint
                )

                y += 28f

                cabecalhoItens()
            }

            paint.textSize = 10f
            paint.typeface = Typeface.DEFAULT

            val descricao =
                if (item.descricao.length > 42) {

                    item.descricao.take(39) + "..."

                } else {

                    item.descricao
                }

            canvas.drawText(
                descricao,
                margemEsquerda,
                y,
                paint
            )

            canvas.drawText(
                item.quantidade.toString(),
                340f,
                y,
                paint
            )

            canvas.drawText(
                Formatacao.moeda(item.valorUnitario),
                385f,
                y,
                paint
            )

            canvas.drawText(
                Formatacao.moeda(item.subtotal),
                475f,
                y,
                paint
            )

            y += 20f
        }

        // ---------------- TOTAL ----------------

        if (y > 690f) {
            novaPagina()
        }

        y += 18f

        canvas.drawLine(
            margemEsquerda,
            y,
            margemDireita,
            y,
            paint
        )

        y += 28f

        paint.textSize = 18f
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "TOTAL: ${Formatacao.moeda(orcamento.total)}",
            325f,
            y,
            paint
        )

        y += 35f

        // ---------------- OBSERVAÇÕES ----------------

        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT

        if (orcamento.observacoes.isNotBlank()) {

            canvas.drawText(
                "Observações:",
                margemEsquerda,
                y,
                paint
            )

            y += 16f

            orcamento.observacoes
                .chunked(80)
                .forEach { trecho ->

                    if (y > 780f) {
                        novaPagina()
                    }

                    canvas.drawText(
                        trecho,
                        margemEsquerda,
                        y,
                        paint
                    )

                    y += 15f
                }

            y += 8f
        }

        if (pixEmpresa.isNotBlank()) {

            canvas.drawText(
                "Chave Pix: $pixEmpresa",
                margemEsquerda,
                y,
                paint
            )
        }

        pdf.finishPage(pagina)

        // ---------------- ARQUIVO ----------------

        val pasta = File(
            context.cacheDir,
            "pdf"
        )

        if (!pasta.exists()) {
            pasta.mkdirs()
        }

        val arquivo = File(
            pasta,
            "${orcamento.numero}.pdf"
        )

        FileOutputStream(arquivo).use { saida ->

            pdf.writeTo(saida)
        }

        pdf.close()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            arquivo
        )
    }
}