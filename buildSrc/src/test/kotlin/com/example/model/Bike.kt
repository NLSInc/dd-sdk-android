package com.example.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.lang.IllegalStateException
import java.lang.NumberFormatException
import kotlin.Boolean
import kotlin.Long
import kotlin.Number
import kotlin.String
import kotlin.jvm.JvmStatic
import kotlin.jvm.Throws

public data class Bike(
    public val productId: Long = 1L,
    public val productName: String,
    public val type: String? = "road",
    public val price: Number = 55.5,
    public val frameMaterial: FrameMaterial? = FrameMaterial.LIGHT_ALUMINIUM,
    public val inStock: Boolean = true,
    public val color: Color = Color.LIME_GREEN,
) {
    public fun toJson(): JsonElement {
        val json = JsonObject()
        json.addProperty("productId", productId)
        json.addProperty("productName", productName)
        type?.let { json.addProperty("type", it) }
        json.addProperty("price", price)
        frameMaterial?.let { json.add("frameMaterial", it.toJson()) }
        json.addProperty("inStock", inStock)
        json.add("color", color.toJson())
        return json
    }

    public companion object {
        @JvmStatic
        @Throws(JsonParseException::class)
        public fun fromJson(serializedObject: String): Bike {
            try {
                val jsonObject = JsonParser.parseString(serializedObject).asJsonObject
                val productId = jsonObject.get("productId").asLong
                val productName = jsonObject.get("productName").asString
                val type = jsonObject.get("type")?.asString
                val price = jsonObject.get("price").asNumber
                val frameMaterial = jsonObject.get("frameMaterial")?.asString?.let {
                    FrameMaterial.fromJson(it)
                }
                val inStock = jsonObject.get("inStock").asBoolean
                val color = jsonObject.get("color").asString.let {
                    Color.fromJson(it)
                }
                return Bike(productId, productName, type, price, frameMaterial, inStock, color)
            } catch (e: IllegalStateException) {
                throw JsonParseException(e.message)
            } catch (e: NumberFormatException) {
                throw JsonParseException(e.message)
            }
        }
    }

    public enum class FrameMaterial(
        private val jsonValue: String,
    ) {
        CARBON("carbon"),
        LIGHT_ALUMINIUM("light_aluminium"),
        IRON("iron"),
        ;

        public fun toJson(): JsonElement = JsonPrimitive(jsonValue)

        public companion object {
            @JvmStatic
            public fun fromJson(serializedObject: String): FrameMaterial = values().first {
                it.jsonValue == serializedObject
            }
        }
    }

    public enum class Color(
        private val jsonValue: String,
    ) {
        RED("red"),
        AMBER("amber"),
        GREEN("green"),
        DARK_BLUE("dark_blue"),
        LIME_GREEN("lime green"),
        SUNBURST_YELLOW("sunburst-yellow"),
        ;

        public fun toJson(): JsonElement = JsonPrimitive(jsonValue)

        public companion object {
            @JvmStatic
            public fun fromJson(serializedObject: String): Color = values().first {
                it.jsonValue == serializedObject
            }
        }
    }
}
