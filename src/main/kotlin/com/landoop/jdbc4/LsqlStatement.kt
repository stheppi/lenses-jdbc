package com.landoop.jdbc4

import com.landoop.rest.RestClient
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.SQLWarning
import java.sql.Statement

class LsqlStatement(private val conn: Connection,
                    private val client: RestClient) : Statement, AutoCloseable {

  private var results: ResultSet = LsqlResultSet.empty()

  override fun getResultSetType(): Int = ResultSet.TYPE_FORWARD_ONLY

  override fun isCloseOnCompletion(): Boolean = true

  override fun <T : Any?> unwrap(iface: Class<T>): T {
    try {
      return iface.cast(this)
    } catch (cce: ClassCastException) {
      throw SQLException("Unable to unwrap instance as " + iface.toString())
    }
  }

  override fun getMaxRows(): Int = 0

  override fun cancel() = throw SQLFeatureNotSupportedException()

  override fun getConnection(): Connection = conn

  override fun setMaxFieldSize(max: Int) {}

  override fun getWarnings(): SQLWarning? = null

  override fun close() {
    // statements have no resources associated
  }

  override fun isClosed(): Boolean = TODO()

  override fun getMaxFieldSize(): Int = 0

  override fun isWrapperFor(iface: Class<*>): Boolean = iface.isInstance(this)

  override fun getUpdateCount(): Int = -1

  override fun setMaxRows(max: Int) {}

  override fun getFetchSize(): Int = -1

  override fun setEscapeProcessing(enable: Boolean) {
  }

  override fun setCursorName(name: String?) = throw SQLFeatureNotSupportedException()

  override fun getQueryTimeout(): Int = 0

  override fun setFetchSize(rows: Int) {}

  override fun clearWarnings() {}

  // we can pool since our statements hold no state
  override fun isPoolable(): Boolean = true

  override fun getResultSetConcurrency(): Int = ResultSet.CONCUR_READ_ONLY

  override fun getResultSet(): ResultSet = results

  override fun setQueryTimeout(seconds: Int) {
  }

  override fun closeOnCompletion() {}

  override fun setFetchDirection(direction: Int) {
    if (direction != ResultSet.FETCH_FORWARD)
      throw SQLFeatureNotSupportedException("LSQL ResultSets can only be read FETCH_FORWARD")
  }

  override fun getFetchDirection(): Int = ResultSet.FETCH_FORWARD

  override fun getResultSetHoldability(): Int = ResultSet.CLOSE_CURSORS_AT_COMMIT

  // we always fetch all results at once
  override fun getMoreResults(): Boolean = false

  // we always fetch all results at once
  override fun getMoreResults(current: Int): Boolean = false

  // == the following are methods that update and thus are not supported by this read only jdbc interface ==

  override fun execute(sql: String?): Boolean = throw SQLFeatureNotSupportedException()
  override fun execute(sql: String?, autoGeneratedKeys: Int): Boolean = throw SQLFeatureNotSupportedException()
  override fun execute(sql: String?, columnIndexes: IntArray?): Boolean = throw SQLFeatureNotSupportedException()
  override fun execute(sql: String?, columnNames: Array<out String>?): Boolean = throw SQLFeatureNotSupportedException()
  override fun executeBatch(): IntArray = throw SQLFeatureNotSupportedException()
  override fun addBatch(sql: String?) = throw SQLFeatureNotSupportedException()
  override fun getGeneratedKeys(): ResultSet = throw SQLFeatureNotSupportedException()
  override fun clearBatch() = throw SQLFeatureNotSupportedException()
  override fun executeQuery(sql: String?): ResultSet = throw SQLFeatureNotSupportedException()
  override fun executeUpdate(sql: String?): Int = throw SQLFeatureNotSupportedException()
  override fun executeUpdate(sql: String?, autoGeneratedKeys: Int): Int = throw SQLFeatureNotSupportedException()
  override fun executeUpdate(sql: String?, columnIndexes: IntArray?): Int = throw SQLFeatureNotSupportedException()
  override fun executeUpdate(sql: String?, columnNames: Array<out String>?): Int = throw SQLFeatureNotSupportedException()
  override fun setPoolable(poolable: Boolean) = throw SQLFeatureNotSupportedException()
}