package com.mapbox.maps.extension.style.utils

import com.google.gson.JsonPrimitive
import com.mapbox.bindgen.Value
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Geometry
import com.mapbox.maps.StylePropertyValue
import com.mapbox.maps.StylePropertyValueKind
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.properties.generated.LayerProperty
import com.mapbox.maps.extension.style.light.LightPosition
import com.mapbox.maps.extension.style.types.Formatted
import com.mapbox.maps.extension.style.types.StyleTransition

internal object TypeUtils {
  fun wrapToValue(value: Any): Value {
    return when (value) {
      is Value -> {
        value
      }
      is Expression -> {
        value
      }
      is Formatted -> {
        value.toValue()
      }
      is StyleTransition -> {
        value.toValue()
      }
      is LayerProperty -> {
        Value(value.value)
      }
      is LightPosition -> {
        value.toValue()
      }
      is Int -> {
        Value(value.toLong())
      }
      is String -> {
        Value(value)
      }
      is Boolean -> {
        Value(value)
      }
      is Double -> {
        Value(value)
      }
      is Float -> {
        Value(value.toDouble())
      }
      is Long -> {
        Value(value)
      }
      is IntArray -> {
        val valueArray = value.map { Value(it.toLong()) }
        Value(valueArray)
      }
      is BooleanArray -> {
        val valueArray = value.map { Value(it) }
        Value(valueArray)
      }
      is DoubleArray -> {
        val valueArray = value.map { Value(it) }
        Value(valueArray)
      }
      is FloatArray -> {
        val valueArray = value.map { Value(it.toDouble()) }
        Value(valueArray)
      }
      is LongArray -> {
        val valueArray = value.map { Value(it) }
        Value(valueArray)
      }
      is Array<*> -> {
        val valueArray = value.map { it?.let { wrapToValue(it) } }
        Value(valueArray)
      }
      is List<*> -> {
        val valueArray = value.map { it?.let { wrapToValue(it) } }
        Value(valueArray)
      }
      is HashMap<*, *> -> {
        val result: HashMap<String, Value> = HashMap()
        @Suppress("UNCHECKED_CAST")
        val castedValue = value as HashMap<String, Any>
        castedValue.keys.forEach { key -> result[key] = wrapToValue(castedValue[key]!!) }
        Value(result)
      }
      is JsonPrimitive -> {
        if (value.isBoolean) {
          return Value(value.asBoolean)
        }
        if (value.isNumber) {
          return Value(value.asDouble)
        }
        if (value.isString) {
          return Value(value.asString)
        }
        throw RuntimeException("Failed to parse JsonPrimitive: $value")
      }
      else -> throw RuntimeException("Wrapping \"${value::class.java.simpleName}\" is not supported.")
    }
  }
}

/**
 * Extension function for [Value] to unwrap Value to [Any].
 */
fun Value.unwrapToAny(): Any {
  when (val contents = this.contents) {
    is Double -> return contents
    is Long -> return contents
    is Boolean -> return contents
    is String -> return contents
    is DoubleArray -> return contents
    is LongArray -> return contents
    is BooleanArray -> return contents
    is List<*> -> {
      @Suppress("UNCHECKED_CAST")
      val valueList = contents as List<Value>
      val ret = ArrayList<Any>()
      valueList.forEach {
        ret.add(it.unwrapToAny())
      }
      return ret
    }
    is HashMap<*, *> -> {
      @Suppress("UNCHECKED_CAST")
      val valueMap = contents as HashMap<String, Value>
      val ret = HashMap<String, Any>()
      valueMap.keys.forEach { key ->
        valueMap[key]?.let { value ->
          ret[key] = value.unwrapToAny()
        }
      }
      return ret
    }
  }
  throw RuntimeException("unable to unwrap Value of content type: ${this.contents?.let { it::class.java.simpleName }}")
}

/**
 * Extension function for [Value] to unwrap Value to the given type.
 *
 * Throws exception if type doesn't match.
 */
inline fun <reified T> Value.unwrapToTyped(): T {
  val unwrappedValue = unwrapToAny()
  return if (unwrappedValue is T) {
    unwrappedValue
  } else {
    throw RuntimeException("Requested type ${T::class.java.simpleName} doesn't match ${unwrappedValue::class.java.simpleName}")
  }
}

/**
 * Extension function for [Value] to unwrap Value to [StyleTransition].
 *
 * Throws exception if couldn't convert.
 */
fun Value.unwrapToStyleTransition(): StyleTransition {
  when (val transition = this.contents) {
    is HashMap<*, *> -> {
      val builder = StyleTransition.Builder()
      @Suppress("UNCHECKED_CAST")
      val map = transition as HashMap<String, Value>
      map["delay"]?.let {
        builder.delay(it.contents as Long)
      }
      map["duration"]?.let {
        builder.duration(it.contents as Long)
      }
      return builder.build()
    }
    is List<*> -> {
      @Suppress("UNCHECKED_CAST")
      val array = transition as List<Value>
      val builder = StyleTransition.Builder()
      builder.duration(array[0].contents as Long)
      builder.delay(array[1].contents as Long)
      return builder.build()
    }
  }
  throw RuntimeException("unable to unwrap to StyleTransition: $this")
}

/**
 * Extension function for [Value] to unwrap Value to [Expression].
 *
 * Throws exception if couldn't convert.
 */
fun Value.unwrapToExpression(): Expression {
  when (val input = this.contents) {
    is Double -> {
      return Expression.literal(input)
    }
    is Long -> {
      return Expression.literal(input)
    }
    is Boolean -> {
      return Expression.literal(input)
    }
    is String -> {
      return Expression.literal(input)
    }
    is List<*> -> {
      @Suppress("UNCHECKED_CAST")
      val listValue = input as List<Value>
      val operator = listValue.first().contents as String
      if ("literal" == operator) {
        when (val literalValue = listValue.last().contents) {
          is List<*> -> {
            @Suppress("UNCHECKED_CAST")
            val literalList = literalValue as List<Value>
            val resultList = ArrayList<Expression>()
            literalList.forEach { resultList.add(it.unwrapToExpression()) }
            return Expression.ExpressionBuilder("literal").addArgument(Expression(resultList))
              .build()
          }
          is HashMap<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            val literalMap = literalValue as HashMap<String, Value>
            val resultMap = HashMap<String, Value>()
            for ((key, value) in literalMap) {
              resultMap[key] = value.unwrapToExpression()
            }
            @Suppress("UNCHECKED_CAST")
            return Expression.literal(resultMap as HashMap<String, Any>)
          }
        }
      }
      val argList = listValue.drop(1)
      val expressionBuilder = Expression.ExpressionBuilder(operator)
      argList.forEach {
        expressionBuilder.addArgument(it.unwrapToExpression())
      }
      return expressionBuilder.build()
    }
    is HashMap<*, *> -> {
      @Suppress("UNCHECKED_CAST")
      val inputMap = input as HashMap<String, Value>
      return Expression(inputMap)
    }
  }
  throw RuntimeException("unable to unwrap to Expression: $this")
}

/**
 * Extension function for [StylePropertyValue] to unwrap [StylePropertyValue] to given type.
 *
 * Throws exception if couldn't convert or type doesn't match.
 */
inline fun <reified T> StylePropertyValue.unwrap(): T {
  when (this.kind) {
    StylePropertyValueKind.CONSTANT -> {
      return this.value.unwrapToTyped()
    }
    StylePropertyValueKind.TRANSITION -> {
      val unwrappedTransition = this.value.unwrapToStyleTransition()
      if (unwrappedTransition is T) {
        return unwrappedTransition
      } else {
        throw RuntimeException("Requested type ${T::class.java.simpleName} doesn't match ${unwrappedTransition::class.java.simpleName}")
      }
    }
    StylePropertyValueKind.EXPRESSION -> {
      val unwrappedExpression = this.value.unwrapToExpression()
      if (unwrappedExpression is T) {
        return unwrappedExpression
      } else {
        throw RuntimeException("Requested type ${T::class.java.simpleName} doesn't match ${unwrappedExpression::class.java.simpleName}")
      }
    }
    StylePropertyValueKind.UNDEFINED -> {
      throw RuntimeException("Property is undefined")
    }
    else -> throw RuntimeException("parsing ${this.kind} is not supported yet")
  }
}

/**
 * Extension function for [StylePropertyValue] to silently unwrap [StylePropertyValue] to given type.
 *
 * Returns null if couldn't convert or type doesn't match.
 */
inline fun <reified T> StylePropertyValue.silentUnwrap(): T? {
  return try {
    this.unwrap()
  } catch (e: RuntimeException) {
    null
  }
}

/**
 * Extension function for [Feature] to convert [Feature] to [Value].
 */
fun Feature.toValue(): Value {
  return TypeUtils.wrapToValue(this.toJson())
}

/**
 * Extension function for [FeatureCollection] to convert [FeatureCollection] to [Value].
 */
fun FeatureCollection.toValue(): Value {
  return TypeUtils.wrapToValue(this.toJson())
}

/**
 * Extension function for [Geometry] to convert [Geometry] to [Value].
 */
fun Geometry.toValue(): Value {
  return TypeUtils.wrapToValue(this.toJson())
}