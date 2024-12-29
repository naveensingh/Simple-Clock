package org.fossify.clock.helpers

import org.fossify.clock.interfaces.JSONConvertible
import org.fossify.commons.helpers.ExportResult
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream

object DataExporter {

    fun exportData(
        alarms: List<JSONConvertible>,
        timers: List<JSONConvertible>,
        outputStream: OutputStream?,
        callback: (result: ExportResult) -> Unit,
    ) {
        if (outputStream == null) {
            callback.invoke(ExportResult.EXPORT_FAIL)
            return
        }

        val alarmsJsonArray = toJsonArray(alarms)
        val timersJsonArray = toJsonArray(timers)

        val jsonObject = JSONObject().apply {
            put("alarms", alarmsJsonArray)
            put("timers", timersJsonArray)
        }

        try {
            outputStream.bufferedWriter().use { out ->
                out.write(jsonObject.toString())
            }
            callback.invoke(ExportResult.EXPORT_OK)
        } catch (e: Exception) {
            callback.invoke(ExportResult.EXPORT_FAIL)
        }
    }

    private fun toJsonArray(list: List<JSONConvertible>): JSONArray {
        return if (list.isEmpty()) {
            JSONArray()
        } else {
            JSONArray().apply {
                list.forEach { item ->
                    put(JSONObject(item.toJSON()))
                }
            }
        }
    }
}
