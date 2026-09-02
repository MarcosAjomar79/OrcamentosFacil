package com.example.orcamentosFacil

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    "orcamentos.db",
    null,
    1
) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE orcamentos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero TEXT NOT NULL,
                cliente TEXT NOT NULL,
                telefone TEXT,
                data TEXT NOT NULL,
                validade TEXT,
                observacoes TEXT,
                total REAL NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE itens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                orcamento_id INTEGER NOT NULL,
                descricao TEXT NOT NULL,
                quantidade INTEGER NOT NULL,
                valor_unitario REAL NOT NULL,
                subtotal REAL NOT NULL,
                FOREIGN KEY (orcamento_id)
                    REFERENCES orcamentos(id)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS itens")
        db.execSQL("DROP TABLE IF EXISTS orcamentos")
        onCreate(db)
    }

    fun salvarOrcamento(
        cliente: String,
        telefone: String,
        validade: String,
        observacoes: String,
        itens: List<ItemOrcamento>
    ): Long {

        val db = writableDatabase

        db.beginTransaction()

        try {

            val total = itens.sumOf { it.subtotal }

            val data = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale("pt", "BR")
            ).format(Date())

            val valoresOrcamento = ContentValues().apply {
                put("numero", "TEMP")
                put("cliente", cliente)
                put("telefone", telefone)
                put("data", data)
                put("validade", validade)
                put("observacoes", observacoes)
                put("total", total)
            }

            val id = db.insertOrThrow(
                "orcamentos",
                null,
                valoresOrcamento
            )

            val numero =
                "ORC-" + id.toString().padStart(5, '0')

            val atualizacao = ContentValues().apply {
                put("numero", numero)
            }

            db.update(
                "orcamentos",
                atualizacao,
                "id = ?",
                arrayOf(id.toString())
            )

            itens.forEach { item ->

                val valoresItem = ContentValues().apply {
                    put("orcamento_id", id)
                    put("descricao", item.descricao)
                    put("quantidade", item.quantidade)
                    put("valor_unitario", item.valorUnitario)
                    put("subtotal", item.subtotal)
                }

                db.insertOrThrow(
                    "itens",
                    null,
                    valoresItem
                )
            }

            db.setTransactionSuccessful()

            return id

        } finally {

            db.endTransaction()
        }
    }

    fun listarOrcamentos(): List<Orcamento> {

        val lista = mutableListOf<Orcamento>()

        readableDatabase.rawQuery(
            "SELECT * FROM orcamentos ORDER BY id DESC",
            null
        ).use { cursor ->

            while (cursor.moveToNext()) {

                lista.add(
                    Orcamento(
                        id = cursor.getLong(
                            cursor.getColumnIndexOrThrow("id")
                        ),
                        numero = cursor.getString(
                            cursor.getColumnIndexOrThrow("numero")
                        ),
                        cliente = cursor.getString(
                            cursor.getColumnIndexOrThrow("cliente")
                        ),
                        telefone = cursor.getString(
                            cursor.getColumnIndexOrThrow("telefone")
                        ) ?: "",
                        data = cursor.getString(
                            cursor.getColumnIndexOrThrow("data")
                        ),
                        validade = cursor.getString(
                            cursor.getColumnIndexOrThrow("validade")
                        ) ?: "",
                        observacoes = cursor.getString(
                            cursor.getColumnIndexOrThrow("observacoes")
                        ) ?: "",
                        total = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("total")
                        )
                    )
                )
            }
        }

        return lista
    }

    fun buscarOrcamento(id: Long): Orcamento? {

        readableDatabase.rawQuery(
            "SELECT * FROM orcamentos WHERE id = ?",
            arrayOf(id.toString())
        ).use { cursor ->

            if (cursor.moveToFirst()) {

                return Orcamento(
                    id = cursor.getLong(
                        cursor.getColumnIndexOrThrow("id")
                    ),
                    numero = cursor.getString(
                        cursor.getColumnIndexOrThrow("numero")
                    ),
                    cliente = cursor.getString(
                        cursor.getColumnIndexOrThrow("cliente")
                    ),
                    telefone = cursor.getString(
                        cursor.getColumnIndexOrThrow("telefone")
                    ) ?: "",
                    data = cursor.getString(
                        cursor.getColumnIndexOrThrow("data")
                    ),
                    validade = cursor.getString(
                        cursor.getColumnIndexOrThrow("validade")
                    ) ?: "",
                    observacoes = cursor.getString(
                        cursor.getColumnIndexOrThrow("observacoes")
                    ) ?: "",
                    total = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("total")
                    )
                )
            }
        }

        return null
    }

    fun buscarItens(orcamentoId: Long): List<ItemOrcamento> {

        val lista = mutableListOf<ItemOrcamento>()

        readableDatabase.rawQuery(
            """
        SELECT descricao, quantidade, valor_unitario
        FROM itens
        WHERE orcamento_id = ?
        ORDER BY id
        """.trimIndent(),
            arrayOf(orcamentoId.toString())
        ).use { cursor ->

            while (cursor.moveToNext()) {

                lista.add(
                    ItemOrcamento(
                        descricao = cursor.getString(0),
                        quantidade = cursor.getInt(1),
                        valorUnitario = cursor.getDouble(2)
                    )
                )
            }
        }

        return lista
    }

    fun excluirOrcamento(id: Long) {

        writableDatabase.delete(
            "orcamentos",
            "id = ?",
            arrayOf(id.toString())
        )
    }
}