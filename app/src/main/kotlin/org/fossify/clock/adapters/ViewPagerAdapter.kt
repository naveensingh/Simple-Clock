package org.fossify.clock.adapters

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import org.fossify.clock.fragments.AlarmFragment
import org.fossify.clock.fragments.ClockFragment
import org.fossify.clock.fragments.StopwatchFragment
import org.fossify.clock.fragments.TimerFragment
import org.fossify.clock.helpers.*
import org.fossify.commons.models.AlarmSound

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val fragments = HashMap<Int, Fragment>()

    override fun getItem(position: Int): Fragment {
        return getFragment(position)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        if (fragment is Fragment) {
            fragments[position] = fragment
        }
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        fragments.remove(position)
        super.destroyItem(container, position, item)
    }

    override fun getCount() = TABS_COUNT

    private fun getFragment(position: Int) = when (position) {
        TAB_CLOCK_INDEX -> ClockFragment()
        TAB_ALARM_INDEX -> AlarmFragment()
        TAB_STOPWATCH_INDEX -> StopwatchFragment()
        TAB_TIMER_INDEX -> TimerFragment()
        else -> throw RuntimeException("Trying to fetch unknown fragment id $position")
    }

    fun showAlarmSortDialog() {
        (fragments[TAB_ALARM_INDEX] as? AlarmFragment)?.showSortingDialog()
    }

    fun updateClockTabAlarm() {
        (fragments[TAB_CLOCK_INDEX] as? ClockFragment)?.updateAlarm()
    }

    fun updateAlarmTabAlarmSound(alarmSound: AlarmSound) {
        (fragments[TAB_ALARM_INDEX] as? AlarmFragment)?.updateAlarmSound(alarmSound)
    }

    fun updateTimerTabAlarmSound(alarmSound: AlarmSound) {
        (fragments[TAB_TIMER_INDEX] as? TimerFragment)?.updateAlarmSound(alarmSound)
    }

    fun updateTimerPosition(timerId: Int) {
        (fragments[TAB_TIMER_INDEX] as? TimerFragment)?.updatePosition(timerId)
    }

    fun startStopWatch() {
        (fragments[TAB_STOPWATCH_INDEX] as? StopwatchFragment)?.startStopWatch()
    }
}
