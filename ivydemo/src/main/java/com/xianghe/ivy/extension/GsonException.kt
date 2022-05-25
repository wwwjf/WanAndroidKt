package com.birdsport.cphome.ui.widget.gson

import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * Gson异常处理类
 * Created by ycl on 2018/11/4.
 */
class IntegerDefault0Adapter : JsonSerializer<Int>, JsonDeserializer<Int> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Int? {
        try {
            if (json.asString == "" || json.asString == "null") {//定义为int类型,如果后台返回""或者null,则返回0
                return 0
            }
        } catch (ignore: Exception) {
        }

        try {
            return json.asInt
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(src: Int?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src!!)
    }
}

class DoubleDefault0Adapter : JsonSerializer<Double>, JsonDeserializer<Double> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Double? {
        try {
            if (json.asString == "" || json.asString == "null") {//定义为double类型,如果后台返回""或者null,则返回0.00
                return 0.00
            }
        } catch (ignore: Exception) {
        }

        try {
            return json.asDouble
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }

    }

    override fun serialize(src: Double?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src!!)
    }
}

class LongDefault0Adapter : JsonSerializer<Long>, JsonDeserializer<Long> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Long? {
        try {
            if (json.asString == "" || json.asString == "null") {//定义为long类型,如果后台返回""或者null,则返回0
                return 0L
            }
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }

        try {
            return json.asLong
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }

    }

    override fun serialize(src: Long?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src!!)
    }
}

class AnyDefault0Adapter : JsonSerializer<Any>, JsonDeserializer<Any> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): Any {
        if (json.asString == "" || json.asString == "null") {
            return json.asJsonNull
        }
        if (json.isJsonObject) {
            return json.asJsonObject
        } else if (json.isJsonArray) {
            return json.asJsonArray
        } else if (json.isJsonNull) {
            return json.asJsonNull
        }
        try {
            return json.asJsonPrimitive
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }

    override fun serialize(src: Any?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

class ListDefault0Adapter : JsonDeserializer<List<Any>> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<Any> {
        if (json.isJsonArray) {
            val jsonArray = json.asJsonArray
            val itemType = (typeOfT as ParameterizedType).actualTypeArguments[0]
            val list = arrayListOf<Any>()
            for (i in 0 until jsonArray.size()) {
                val element = jsonArray.get(i)
                val item = context.deserialize<Any>(element, itemType)
                list.add(item)
            }
            return list
        } else {
            //和接口类型不符，返回空List
            return emptyList<Any>()
        }
    }
}

class StringDefault0Adapter : JsonSerializer<String>, JsonDeserializer<String> {
    override fun serialize(src: String?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src!!)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): String? {
        try {
            if (json.asString == "" || json.asString == "null") {//定义为long类型,如果后台返回""或者null,则返回0
                return ""
            }
        } catch (ignore: Exception) {
        }

        try {
            return json.asString
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }

    }
}

class NullStringToEmptyAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType != String::class.java) {
            return null
        }
        return StringNullAdapter() as TypeAdapter<T>
    }
}

class StringNullAdapter : TypeAdapter<String>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): String {
        if (reader.peek() === JsonToken.NULL) {
            reader.nextNull()
            return ""
        }
        return reader.nextString()
    }

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: String?) {
        if (value == null) {
//            writer.value("")
            writer.nullValue()
            return
        }
        writer.value(value)
    }
}

class LongTypeAdapter : TypeAdapter<Long>() {
    override fun write(out: JsonWriter, value: Long?) {
        if (value == null) {
            out.value(0L)
            return
        }
        out.value(value)
    }

    override fun read(`in`: JsonReader): Long? {
        try {
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                Log.e("TypeAdapter", "null is not a number")
                return 0L
            }
            if (`in`.peek() == JsonToken.BOOLEAN) {
                val b = `in`.nextBoolean()
                Log.e("TypeAdapter", b.toString() + " is not a number")
                return 0L
            }
            if (`in`.peek() == JsonToken.STRING) {
                val str = `in`.nextString()
                return try {
                    java.lang.Long.parseLong(str)
                } catch (e: Exception) {
                    0L
                }
            } else {
                return `in`.nextLong()
            }
        } catch (e: Exception) {
            Log.e("TypeAdapter", "Not a number", e)
        }
        return 0L
    }
}

class IntegerTypeAdapter : TypeAdapter<Int>() {
    override fun write(out: JsonWriter, value: Int?) {
        if (value == null) {
            out.value(0)
            return
        }
        out.value(value)
    }

    override fun read(`in`: JsonReader): Int? {
        try {
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                Log.e("TypeAdapter", "null is not a number")
                return 0
            }
            if (`in`.peek() == JsonToken.BOOLEAN) {
                val b = `in`.nextBoolean()
                Log.e("TypeAdapter", b.toString() + " is not a number")
                return 0
            }
            if (`in`.peek() == JsonToken.STRING) {
                val str = `in`.nextString()
                return try {
                    Integer.parseInt(str)
                } catch (e: Exception) {
                    0
                }
            } else {
                return `in`.nextInt()
            }
        } catch (e: Exception) {
            Log.e("TypeAdapter", "Not a number", e)
        }
        return 0
    }
}

class DoubleTypeAdapter : TypeAdapter<Double>() {
    override fun write(out: JsonWriter, value: Double?) {
        if (value == null) {
            out.value(0.0)
            return
        }
        out.value(value)
    }

    override fun read(`in`: JsonReader): Double? {
        try {
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                Log.e("TypeAdapter", "null is not a number")
                return 0.0
            }
            if (`in`.peek() == JsonToken.BOOLEAN) {
                val b = `in`.nextBoolean()
                Log.e("TypeAdapter", b.toString() + " is not a number")
                return 0.0
            }
            if (`in`.peek() == JsonToken.STRING) {
                val str = `in`.nextString()
                return try {
                    java.lang.Double.parseDouble(str)
                } catch (e: Exception) {
                    0.0
                }
            } else {
                val value = `in`.nextDouble()
                return value ?: 0.0
            }
        } catch (e: NumberFormatException) {
            Log.e("TypeAdapter", e.message, e)
        } catch (e: Exception) {
            Log.e("TypeAdapter", e.message, e)
        }
        return 0.0
    }
}

class FloatTypeAdapter : TypeAdapter<Float>() {
    override fun write(out: JsonWriter, value: Float?) {
        if (value == null) {
            out.value(0f)
            return
        }
        out.value(value)
    }

    override fun read(`in`: JsonReader): Float? {
        try {
            val value: Float?
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                Log.e("TypeAdapter", "null is not a number")
                return 0f
            }
            if (`in`.peek() == JsonToken.BOOLEAN) {
                val b = `in`.nextBoolean()
                Log.e("TypeAdapter", b.toString() + " is not a number")
                return 0f
            }
            if (`in`.peek() == JsonToken.STRING) {
                val str = `in`.nextString()
                return try {
                    java.lang.Float.parseFloat(str)
                } catch (e: Exception) {
                    0f
                }
            } else {
                val str = `in`.nextString()
                value = java.lang.Float.valueOf(str)
            }
            return value
        } catch (e: Exception) {
            Log.e("TypeAdapter", "Not a number", e)
        }
        return 0f
    }
}