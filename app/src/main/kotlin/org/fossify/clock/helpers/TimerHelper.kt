package org.fossify.clock.helpers

import android.content.Context
import org.fossify.clock.extensions.timerDb
import org.fossify.clock.models.Timer
import org.fossify.commons.helpers.ensureBackgroundThread

class TimerHelper(val context: Context) {
    private val timerDao = context.timerDb

    fun getTimers(callback: (timers: ArrayList<Timer>) -> Unit) {
        ensureBackgroundThread {
            callback.invoke(timerDao.getTimers() as ArrayList<Timer>)
        }
    }

    fun getTimer(timerId: Int, callback: (timer: Timer) -> Unit) {
        ensureBackgroundThread {
            callback.invoke(timerDao.getTimer(timerId)!!)
        }
    }

    fun tryGetTimer(timerId: Int, callback: (timer: Timer?) -> Unit) {
        ensureBackgroundThread {
            callback.invoke(timerDao.getTimer(timerId))
        }
    }

    fun findTimers(seconds: Int, label: String, callback: (timers: List<Timer>) -> Unit) {
        ensureBackgroundThread {
            callback.invoke(timerDao.findTimers(seconds, label))
        }
    }

    fun insertOrUpdateTimer(timer: Timer, callback: (id: Long) -> Unit = {}) {
        ensureBackgroundThread {
            val id = timerDao.insertOrUpdateTimer(timer)
            callback.invoke(id)
        }
    }

    fun deleteTimer(id: Int, callback: () -> Unit = {}) {
        ensureBackgroundThread {
            timerDao.deleteTimer(id)
            callback.invoke()
        }
    }

    fun deleteTimers(timers: List<Timer>, callback: () -> Unit = {}) {
        ensureBackgroundThread {
            timerDao.deleteTimers(timers)
            callback.invoke()
        }
    }
}
