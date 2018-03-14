package com.landoop.jdbc4

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.landoop.rest.domain.InsertField
import com.landoop.rest.domain.PreparedInsertInfo
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.SQLException

// used by prepared statements to build up records
class RecordBuilder(val info: PreparedInsertInfo) {

  data class Path(val parts: List<String>) {
    override fun toString(): String = parts.joinToString(".")
  }

  private val values = mutableMapOf<Int, Any?>()

  private fun InsertField.path(): Path {
    val parents = this.parents.toMutableList()
    parents.add(this.name)
    return Path(parents)
  }

  fun build(): Pair<String?, JsonNode> {
    val root = JacksonSupport.mapper.createObjectNode()
    fun find(parents: List<String>): ObjectNode = parents.fold(root, { node, field ->
      node.findParent(field) ?: node.putObject(field)
    })
    // the key is not included in the value json
    info.fields.withIndex().filterNot { it.value.isKey }.forEach {
      val node = find(it.value.parents)
      val value = values[it.index]
      when (value) {
        null -> node.putNull(it.value.name)
        is String -> node.put(it.value.name, value)
        is Boolean -> node.put(it.value.name, value)
        is Long -> node.put(it.value.name, value)
        is Float -> node.put(it.value.name, value)
        is Int -> node.put(it.value.name, value)
        is Double -> node.put(it.value.name, value)
        is BigInteger -> node.put(it.value.name, value)
        is BigDecimal -> node.put(it.value.name, value)
        else -> throw SQLException("Unsupported value type $value")
      }
    }
    val key = info.fields.withIndex().filter { it.value.isKey }.map { values[it.index] }.first()?.toString()
    return key to root
  }

  // sets a value by index, where the index is the original position in the sql query
  fun put(index: Int, value: Any?) {
    checkBounds(index)
    // remember jdbc indexes are 1 based
    values[index - 1] = value
  }

  private fun checkBounds(k: Int) {
    if (k < 1 || k > info.fields.size)
      throw IndexOutOfBoundsException("$k is out of bounds")
  }

  // throws an exception if this record is not valid because of missing values
  fun checkRecord() {
    for (k in 0 until info.fields.size) {
      val field = info.fields[k]
      if (!values.containsKey(k))
        throw SQLException("Variable ${field.path()} was not set; You must set all values before executing; if null is desired, explicitly set this using setNull(pos)")
    }
  }
}