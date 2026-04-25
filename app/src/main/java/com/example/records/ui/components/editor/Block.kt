package com.example.records.ui.components.editor

import java.util.UUID

sealed class Block {
    abstract val id: String

    data class RichText(
        override val id: String = UUID.randomUUID().toString(),
        var htmlContent: String = ""
    ) : Block()

    data class Checkbox(
        override val id: String = UUID.randomUUID().toString(),
        var checked: Boolean = false,
        var text: String = ""
    ) : Block()

    data class Table(
        override val id: String = UUID.randomUUID().toString(),
        var rows: Int = 2,
        var cols: Int = 2,
        var cells: MutableList<MutableList<String>> = MutableList(2) { MutableList(2) { "" } }
    ) : Block()
}

// Wrapper for Gson serialization to keep track of type
data class BlockData(
    val type: String,
    val id: String,
    val htmlContent: String? = null,
    val checked: Boolean? = null,
    val text: String? = null,
    val rows: Int? = null,
    val cols: Int? = null,
    val cells: List<List<String>>? = null
) {
    fun toBlock(): Block {
        return when (type) {
            "RichText" -> Block.RichText(id, htmlContent ?: "")
            "Checkbox" -> Block.Checkbox(id, checked ?: false, text ?: "")
            "Table" -> {
                val mutableCells = cells?.map { it.toMutableList() }?.toMutableList()
                    ?: MutableList(rows ?: 2) { MutableList(cols ?: 2) { "" } }
                Block.Table(id, rows ?: 2, cols ?: 2, mutableCells)
            }
            else -> Block.RichText(id, "")
        }
    }
}

fun Block.toBlockData(): BlockData {
    return when (this) {
        is Block.RichText -> BlockData(
            type = "RichText",
            id = id,
            htmlContent = htmlContent
        )
        is Block.Checkbox -> BlockData(
            type = "Checkbox",
            id = id,
            checked = checked,
            text = text
        )
        is Block.Table -> BlockData(
            type = "Table",
            id = id,
            rows = rows,
            cols = cols,
            cells = cells
        )
    }
}

fun List<Block>.toJson(): String {
    val gson = com.google.gson.Gson()
    val dataList = this.map { it.toBlockData() }
    return gson.toJson(dataList)
}

fun String.toBlocks(): List<Block> {
    if (this.isEmpty()) return listOf(Block.RichText())
    return try {
        val gson = com.google.gson.Gson()
        val type = object : com.google.gson.reflect.TypeToken<List<BlockData>>() {}.type
        val dataList: List<BlockData> = gson.fromJson(this, type)
        dataList.map { it.toBlock() }
    } catch (e: Exception) {
        // Fallback for old simple strings
        listOf(Block.RichText(htmlContent = this))
    }
}

fun List<Block>.toPlainText(): String {
    return this.joinToString(" ") { block ->
        when (block) {
            is Block.RichText -> android.text.Html.fromHtml(block.htmlContent, android.text.Html.FROM_HTML_MODE_COMPACT).toString()
            is Block.Checkbox -> "[${if (block.checked) "x" else " "}] ${block.text}"
            is Block.Table -> "Table (${block.rows}x${block.cols})"
        }
    }
}

