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

    data class NumberedList(
        override val id: String = UUID.randomUUID().toString(),
        var text: String = "",
        var indentLevel: Int = 0
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
    val cells: List<List<String>>? = null,
    val indentLevel: Int? = null
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
            "NumberedList" -> Block.NumberedList(id, text ?: "", indentLevel ?: 0)
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
        is Block.NumberedList -> BlockData(
            type = "NumberedList",
            id = id,
            text = text,
            indentLevel = indentLevel
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
            is Block.NumberedList -> block.text
        }
    }
}

fun calculateNumberedListPrefixes(blocks: List<Block>): Map<String, String> {
    val prefixes = mutableMapOf<String, String>()
    val activeNumbers = mutableListOf<Int>()
    
    fun getRoman(n: Int): String {
        val map = listOf(
            1000 to "m", 900 to "cm", 500 to "d", 400 to "cd",
            100 to "c", 90 to "xc", 50 to "l", 40 to "xl",
            10 to "x", 9 to "ix", 5 to "v", 4 to "iv", 1 to "i"
        )
        var num = n
        val sb = StringBuilder()
        for ((value, roman) in map) {
            while (num >= value) {
                sb.append(roman)
                num -= value
            }
        }
        return sb.toString()
    }
    
    fun getAlpha(n: Int): String {
        var num = n
        val sb = StringBuilder()
        while (num > 0) {
            val rem = (num - 1) % 26
            sb.insert(0, ('a' + rem))
            num = (num - 1) / 26
        }
        return sb.toString()
    }
    
    for (block in blocks) {
        if (block !is Block.NumberedList) {
            activeNumbers.clear()
            continue
        }
        
        val level = block.indentLevel
        
        // Ensure activeNumbers is large enough
        while (activeNumbers.size <= level) {
            activeNumbers.add(0)
        }
        // Increment the number at this level
        activeNumbers[level] = activeNumbers[level] + 1
        
        // Truncate activeNumbers for any deeper levels
        while (activeNumbers.size > level + 1) {
            activeNumbers.removeAt(activeNumbers.lastIndex)
        }
        
        // Construct the prefix based on activeNumbers
        val sb = StringBuilder()
        for (i in 0..level) {
            val num = activeNumbers[i]
            val valStr = when (i % 3) {
                0 -> "$num"
                1 -> getRoman(num)
                else -> getAlpha(num)
            }
            sb.append(valStr)
            if (i < level) {
                sb.append(".")
            }
        }
        sb.append(".")
        prefixes[block.id] = sb.toString()
    }
    return prefixes
}

