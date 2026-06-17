package com.alhadi.cmms.util

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Minimal read-only XLSX parser built only on the platform's [ZipInputStream] and
 * [XmlPullParser] — no third-party library. It returns each worksheet (keyed by its tab name)
 * as a list of rows, where every row is a list of cell strings (shared strings resolved).
 *
 * It is intentionally lightweight and tolerant: it targets the fixed maintenance-kit template,
 * not the full OOXML spec.
 */
object XlsxReader {

    fun read(input: InputStream): Map<String, List<List<String>>> {
        val entries = HashMap<String, ByteArray>()
        ZipInputStream(input).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) entries[entry.name] = zis.readBytes()
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }

        val shared = entries["xl/sharedStrings.xml"]?.let { parseSharedStrings(it) } ?: emptyList()
        val nameToRid = entries["xl/workbook.xml"]?.let { parseWorkbook(it) } ?: emptyMap()
        val ridToTarget = entries["xl/_rels/workbook.xml.rels"]?.let { parseRels(it) } ?: emptyMap()

        val result = LinkedHashMap<String, List<List<String>>>()
        for ((name, rid) in nameToRid) {
            val target = ridToTarget[rid] ?: continue
            val path = when {
                target.startsWith("/") -> target.trimStart('/')
                target.startsWith("xl/") -> target
                else -> "xl/$target"
            }
            val bytes = entries[path] ?: continue
            result[name] = parseSheet(bytes, shared)
        }
        return result
    }

    private fun newParser(bytes: ByteArray): XmlPullParser {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(ByteArrayInputStream(bytes), null)
        return parser
    }

    private fun parseSharedStrings(bytes: ByteArray): List<String> {
        val list = ArrayList<String>()
        val parser = newParser(bytes)
        var builder: StringBuilder? = null
        var inText = false
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "si" -> builder = StringBuilder()
                    "t" -> inText = true
                }
                XmlPullParser.TEXT -> if (inText) builder?.append(parser.text)
                XmlPullParser.END_TAG -> when (parser.name) {
                    "t" -> inText = false
                    "si" -> { list.add(builder?.toString() ?: ""); builder = null }
                }
            }
            event = parser.next()
        }
        return list
    }

    private fun parseWorkbook(bytes: ByteArray): Map<String, String> {
        val map = LinkedHashMap<String, String>()
        val parser = newParser(bytes)
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG && parser.name == "sheet") {
                val name = parser.getAttributeValue(null, "name")
                val rid = parser.getAttributeValue(null, "r:id")
                if (name != null && rid != null) map[name] = rid
            }
            event = parser.next()
        }
        return map
    }

    private fun parseRels(bytes: ByteArray): Map<String, String> {
        val map = HashMap<String, String>()
        val parser = newParser(bytes)
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG && parser.name == "Relationship") {
                val id = parser.getAttributeValue(null, "Id")
                val target = parser.getAttributeValue(null, "Target")
                if (id != null && target != null) map[id] = target
            }
            event = parser.next()
        }
        return map
    }

    private fun parseSheet(bytes: ByteArray, shared: List<String>): List<List<String>> {
        val rows = ArrayList<List<String>>()
        val parser = newParser(bytes)
        var cells = HashMap<Int, String>()
        var maxCol = -1
        var curRef = ""
        var curType: String? = null
        var value = StringBuilder()
        var capture = false
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "row" -> { cells = HashMap(); maxCol = -1 }
                    "c" -> {
                        curRef = parser.getAttributeValue(null, "r") ?: ""
                        curType = parser.getAttributeValue(null, "t")
                        value = StringBuilder()
                    }
                    "v", "t" -> capture = true
                }
                XmlPullParser.TEXT -> if (capture) value.append(parser.text)
                XmlPullParser.END_TAG -> when (parser.name) {
                    "v", "t" -> capture = false
                    "c" -> {
                        val raw = value.toString()
                        val resolved = if (curType == "s") {
                            raw.trim().toIntOrNull()?.let { shared.getOrNull(it) } ?: ""
                        } else {
                            raw
                        }
                        val col = colIndex(curRef)
                        if (col >= 0) {
                            cells[col] = resolved
                            if (col > maxCol) maxCol = col
                        }
                    }
                    "row" -> rows.add((0..maxCol).map { cells[it] ?: "" })
                }
            }
            event = parser.next()
        }
        return rows
    }

    /** Extracts the 0-based column index from a cell reference such as "B7". */
    private fun colIndex(ref: String): Int {
        var idx = 0
        var seen = false
        for (ch in ref) {
            if (ch in 'A'..'Z') {
                idx = idx * 26 + (ch - 'A' + 1)
                seen = true
            } else if (seen) {
                break
            }
        }
        return idx - 1
    }
}
