package com.example.orcamentosFacil

data class ItemOrcamento (
    val descricao: String,
    val quantidade: Int,
    val valorUnitario: Double
) {
    val subtotal: Double
        get() = quantidade * valorUnitario
}
data class Orcamento (
    val id: Long,
    val numero: String,
    val cliente: String,
    val telefone: String,
    val data: String,
    val validade: String,
    val observacoes: String,
    val total: Double
)