package com.example.records.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat

object RichTextParser {

    /**
     * Converts a string with <b>, <i>, and <u> tags to an AnnotatedString for Compose.
     */
    fun toAnnotatedString(text: String, searchQuery: String = ""): AnnotatedString {
        return buildAnnotatedString {
            val htmlText = text.replace("\n", "<br>")
            val spanned = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            appendSpanned(spanned, searchQuery)
        }
    }

    private fun AnnotatedString.Builder.appendSpanned(spanned: Spanned, searchQuery: String) {
        val text = spanned.toString()
        append(text)
        
        // Apply HTML spans
        val spans = spanned.getSpans(0, text.length, Any::class.java)
        for (span in spans) {
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)
            when (span) {
                is android.text.style.StyleSpan -> {
                    when (span.style) {
                        android.graphics.Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                        android.graphics.Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                        android.graphics.Typeface.BOLD_ITALIC -> addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), start, end)
                    }
                }
                is android.text.style.UnderlineSpan -> {
                    addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                }
            }
        }
        
        // Apply search highlights
        if (searchQuery.isNotEmpty()) {
            val lowerText = text.lowercase()
            val lowerQuery = searchQuery.lowercase()
            var start = lowerText.indexOf(lowerQuery)
            while (start >= 0) {
                addStyle(
                    SpanStyle(background = Color(0xFFC48E1F), color = Color.White),
                    start,
                    start + lowerQuery.length
                )
                start = lowerText.indexOf(lowerQuery, start + 1)
            }
        }
    }
}
