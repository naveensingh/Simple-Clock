package org.fossify.clock.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.fossify.clock.extensions.config
import org.fossify.clock.extensions.dbHelper
import org.fossify.clock.extensions.hideNotification
import org.fossify.clock.extensions.setupAlarmClock
import org.fossify.clock.helpers.ALARM_ID
import org.fossify.commons.extensions.showPickSecondsDialog
import org.fossify.commons.helpers.MINUTE_SECONDS
import java.util.Calendar

class SnoozeReminderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra(ALARM_ID, -1)
        val alarm = dbHelper.getAlarmWithId(id) ?: return
        hideNotification(id)
        showPickSecondsDialog(config.snoozeTime * MINUTE_SECONDS, true, cancelCallback = { dialogCancelled() }) {
            config.snoozeTime = it / MINUTE_SECONDS
            setupAlarmClock(alarm, Calendar.getInstance().apply { add(Calendar.SECOND, it) })
            finishActivity()
        }
    }

    private fun dialogCancelled() {
        finishActivity()
    }

    private fun finishActivity() {
        finish()
        overridePendingTransition(0, 0)
    }
}
