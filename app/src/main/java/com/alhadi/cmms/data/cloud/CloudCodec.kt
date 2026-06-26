package com.alhadi.cmms.data.cloud

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

/**
 * Converts any @Serializable entity to/from a Firestore-friendly Map<String, Any?> using
 * kotlinx.serialization. This lets every entity sync to Firestore as a clean document without
 * hand-writing a field map per type.
 */
object CloudCodec {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    fun <T> toMap(serializer: KSerializer<T>, value: T): Map<String, Any?> {
        val element = json.encodeToJsonElement(serializer, value)
        return (element as JsonObject).mapValues { jsonToAny(it.value) }
    }

    fun <T> fromMap(serializer: KSerializer<T>, map: Map<String, Any?>): T {
        val obj = JsonObject(map.mapValues { anyToJson(it.value) })
        return json.decodeFromJsonElement(serializer, obj)
    }

    private fun jsonToAny(e: JsonElement): Any? = when (e) {
        is JsonNull -> null
        is JsonObject -> e.mapValues { jsonToAny(it.value) }
        is JsonArray -> e.map { jsonToAny(it) }
        is JsonPrimitive -> when {
            e.isString -> e.content
            e.booleanOrNull != null -> e.boolean
            e.longOrNull != null -> e.long
            e.doubleOrNull != null -> e.double
            else -> e.content
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun anyToJson(v: Any?): JsonElement = when (v) {
        null -> JsonNull
        is JsonElement -> v
        is Boolean -> JsonPrimitive(v)
        is Number -> JsonPrimitive(v)
        is String -> JsonPrimitive(v)
        is Map<*, *> -> JsonObject((v as Map<Any?, Any?>).entries.associate { (k, value) -> k.toString() to anyToJson(value) })
        is List<*> -> JsonArray(v.map { anyToJson(it) })
        else -> JsonPrimitive(v.toString())
    }
}
