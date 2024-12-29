package org.fossify.clock.models

import androidx.annotation.Keep
import org.fossify.clock.interfaces.JSONConvertible
import org.json.JSONObject

@Keep
data class Alarm(
    var id: Int,
    var timeInMinutes: Int,
    var days: Int,
    var isEnabled: Boolean,
    var vibrate: Boolean,
    var soundTitle: String,
    var soundUri: String,
    var label: String,
    var oneShot: Boolean = false,
) : JSONConvertible {
    override fun toJSON(): String {
        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("timeInMinutes", timeInMinutes)
        jsonObject.put("days", days)
        jsonObject.put("vibrate", vibrate)
        jsonObject.put("soundTitle", soundTitle)
        jsonObject.put("soundUri", soundUri)
        jsonObject.put("label", label)
        return jsonObject.toString()
    }

    companion object {
        fun parseFromJSON(jsonObject: JSONObject): Alarm? {

            if (!jsonObject.has("id") ||
                !jsonObject.has("timeInMinutes") ||
                !jsonObject.has("days") ||
                !jsonObject.has("vibrate") ||
                !jsonObject.has("soundTitle") ||
                !jsonObject.has("soundUri") ||
                !jsonObject.has("label")
            ) {
                return null
            }

            val id = jsonObject.getInt("id")
            val timeInMinutes = jsonObject.getInt("timeInMinutes")
            val days = jsonObject.getInt("days")
            val vibrate = jsonObject.getBoolean("vibrate")
            val soundTitle = jsonObject.getString("soundTitle")
            val soundUri = jsonObject.getString("soundUri")
            val label = jsonObject.getString("label")

            return Alarm(
                id,
                timeInMinutes,
                days,
                false,
                vibrate,
                soundTitle,
                soundUri,
                label
            )
        }
    }
}

@Keep
data class ObfuscatedAlarm(
    var a: Int,
    var b: Int,
    var c: Int,
    var d: Boolean,
    var e: Boolean,
    var f: String,
    var g: String,
    var h: String,
    var i: Boolean = false,
) {
    fun toAlarm() = Alarm(a, b, c, d, e, f, g, h, i)
}
