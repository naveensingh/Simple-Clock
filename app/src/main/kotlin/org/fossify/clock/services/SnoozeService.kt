package org.fossify.clock.services

import android.app.IntentService
import android.content.Intent
import org.fossify.clock.extensions.config
import org.fossify.clock.extensions.dbHelper
import org.fossify.clock.extensions.hideNotification
import org.fossify.clock.extensions.setupAlarmClock
import org.fossify.clock.helpers.ALARM_ID
import org.fossify.commons.helpers.MINUTE_SECONDS
import java.util.Calendar

class SnoozeService : IntentService("Snooze") {
    override fun onHandleIntent(intent: Intent?) {
        val id = intent!!.getIntExtra(ALARM_ID, -1)
        val alarm = dbHelper.getAlarmWithId(id) ?: return
        hideNotification(id)
        setupAlarmClock(alarm, Calendar.getInstance().apply { add(Calendar.MINUTE, config.snoozeTime) })
    }
}
