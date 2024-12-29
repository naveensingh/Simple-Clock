package org.fossify.clock.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.fossify.clock.interfaces.JSONConvertible
import org.json.JSONObject

@Entity(tableName = "timers")
@Keep
data class Timer(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    var seconds: Int,
    val state: TimerState,
    var vibrate: Boolean,
    var soundUri: String,
    var soundTitle: String,
    var label: String,
    var createdAt: Long,
    var channelId: String? = null,
    var oneShot: Boolean = false,
) : JSONConvertible {
    override fun toJSON(): String {
        val jsonObject = JSONObject()
        jsonObject.put("id", id)
        jsonObject.put("seconds", seconds)
        jsonObject.put("vibrate", vibrate)
        jsonObject.put("soundUri", soundUri)
        jsonObject.put("soundTitle", soundTitle)
        jsonObject.put("label", label)
        jsonObject.put("createdAt", createdAt)
        jsonObject.put("oneShot", oneShot)
        return jsonObject.toString()
    }

    companion object {
        fun parseFromJSON(jsonObject: JSONObject): Timer? {

            if (!jsonObject.has("id") ||
                !jsonObject.has("seconds") ||
                !jsonObject.has("vibrate") ||
                !jsonObject.has("soundUri") ||
                !jsonObject.has("soundTitle") ||
                !jsonObject.has("label") ||
                !jsonObject.has("createdAt")
            ) {
                return null
            }

            val id = jsonObject.getInt("id")
            val second = jsonObject.getInt("seconds")
            val vibrate = jsonObject.getBoolean("vibrate")
            val soundUri = jsonObject.getString("soundUri")
            val soundTitle = jsonObject.getString("soundTitle")
            val label = jsonObject.getString("label")
            val createdAt = jsonObject.getLong("createdAt")

            return Timer(
                id,
                second,
                TimerState.Idle,
                vibrate,
                soundUri,
                soundTitle,
                label,
                createdAt
            )
        }
    }
}

@Keep
data class ObfuscatedTimer(
    var a: Int?,
    var b: Int,
    // We ignore timer state and will just use idle
    val c: Map<Any, Any>,
    var d: Boolean,
    var e: String,
    var f: String,
    var g: String,
    var h: Long,
    var i: String? = null,
    var j: Boolean = false,
) {
    fun toTimer() = Timer(a, b, TimerState.Idle, d, e, f, g, h, i, j)
}
