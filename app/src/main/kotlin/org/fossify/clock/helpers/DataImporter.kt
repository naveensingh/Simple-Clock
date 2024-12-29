package org.fossify.clock.helpers

import android.app.Activity
import org.fossify.clock.models.Alarm
import org.fossify.clock.models.Timer
import org.fossify.commons.extensions.showErrorToast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DataImporter(
    private val activity: Activity,
    private val dbHelper: DBHelper,
    private val timerHelper: TimerHelper
) {

    enum class ImportResult {
        IMPORT_INCOMPLETE,
        IMPORT_FAIL,
        IMPORT_OK
    }

    fun importData(path: String): ImportResult {
        return try {
            val inputStream = File(path).inputStream()
            val jsonString = inputStream.bufferedReader().use { it.readText().trimEnd() }
            val jsonObject = JSONObject(jsonString)
            val alarmsFromJson = jsonObject.getJSONArray("alarms")
            val timersFromJson = jsonObject.getJSONArray("timers")

            val importedAlarms = insertAlarmsFromJSON(alarmsFromJson)
            val importedTimers = insertTimersFromJSON(timersFromJson)

            if (importedAlarms > 0 || importedTimers > 0) {
                ImportResult.IMPORT_OK
            } else if (importedAlarms == 0 && importedTimers == 0) {
                ImportResult.IMPORT_INCOMPLETE
            } else {
                ImportResult.IMPORT_FAIL
            }
        } catch (e: Exception) {
            activity.showErrorToast(e)
            ImportResult.IMPORT_FAIL
        }
    }


    private fun insertAlarmsFromJSON(jsonArray: JSONArray): Int {
        val existingAlarms = dbHelper.getAlarms()
        var insertedCount = 0
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (Alarm.parseFromJSON(jsonObject) != null) {
                val alarm = Alarm.parseFromJSON(jsonObject) as Alarm
                if (!isAlarmAlreadyInserted(alarm, existingAlarms)) {
                    if (dbHelper.insertAlarm(alarm) != -1) {
                        insertedCount++
                    }
                }
            }
        }
        return insertedCount
    }

    private fun insertTimersFromJSON(jsonArray: JSONArray): Int {
        var insertedCount = 0
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (Timer.parseFromJSON(jsonObject) != null) {
                val timer = Timer.parseFromJSON(jsonObject) as Timer
                timerHelper.getTimers { existingTimers ->
                    timer.id = if (existingTimers.isNotEmpty()) {
                        existingTimers.last().id?.plus(1)
                    } else {
                        1
                    }
                    if (!isTimerAlreadyInserted(timer, existingTimers)) {
                        timerHelper.insertOrUpdateTimer(timer) { id ->
                            if (id != -1L) {
                                insertedCount++
                            }
                        }
                    }
                }
            }
        }
        return insertedCount
    }

    private fun isAlarmAlreadyInserted(alarm: Alarm, existingAlarms: List<Alarm>): Boolean {
        for (existingAlarm in existingAlarms) {
            if (alarm.timeInMinutes == existingAlarm.timeInMinutes &&
                alarm.days == existingAlarm.days &&
                alarm.vibrate == existingAlarm.vibrate &&
                alarm.soundTitle == existingAlarm.soundTitle &&
                alarm.soundUri == existingAlarm.soundUri &&
                alarm.label == existingAlarm.label &&
                alarm.oneShot == existingAlarm.oneShot
            ) {
                return true
            }
        }
        return false
    }

    private fun isTimerAlreadyInserted(timer: Timer, existingTimers: List<Timer>): Boolean {
        for (existingTimer in existingTimers) {
            if (timer.seconds == existingTimer.seconds &&
                timer.vibrate == existingTimer.vibrate &&
                timer.soundUri == existingTimer.soundUri &&
                timer.soundTitle == existingTimer.soundTitle &&
                timer.label == existingTimer.label &&
                timer.createdAt == existingTimer.createdAt
            ) {
                return true
            }
        }
        return false
    }
}

