package org.fossify.clock.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.fossify.clock.activities.SimpleActivity
import org.fossify.clock.adapters.TimerAdapter
import org.fossify.clock.databinding.FragmentTimerBinding
import org.fossify.clock.dialogs.ChangeTimerSortDialog
import org.fossify.clock.dialogs.EditTimerDialog
import org.fossify.clock.extensions.config
import org.fossify.clock.extensions.createNewTimer
import org.fossify.clock.extensions.timerHelper
import org.fossify.clock.helpers.SORT_BY_TIMER_DURATION
import org.fossify.clock.models.Timer
import org.fossify.clock.models.TimerEvent
import org.fossify.commons.extensions.getProperBackgroundColor
import org.fossify.commons.extensions.getProperTextColor
import org.fossify.commons.extensions.hideKeyboard
import org.fossify.commons.extensions.updateTextColors
import org.fossify.commons.helpers.SORT_BY_DATE_CREATED
import org.fossify.commons.models.AlarmSound
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TimerFragment : Fragment() {
    private var timers = ArrayList<Timer>()
    private lateinit var binding: FragmentTimerBinding
    private var currentEditAlarmDialog: EditTimerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTimerBinding.inflate(inflater, container, false).apply {
            requireContext().updateTextColors(timerFragment)
            timerAdd.setOnClickListener {
                activity?.run {
                    hideKeyboard()
                    openEditTimer(createNewTimer())
                }
            }
        }

        refreshTimers()

        // the initial timer is created asynchronously at first launch, make sure we show it once created
        if (context?.config?.appRunCount == 1) {
            Handler(Looper.getMainLooper()).postDelayed({
                refreshTimers()
            }, 1000)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshTimers()
    }

    fun showSortingDialog() {
        ChangeTimerSortDialog(activity as SimpleActivity) {
            refreshTimers()
        }
    }

    private fun refreshTimers() {
        activity?.timerHelper?.getTimers { timersFromDB ->
            timers = timersFromDB
            when (requireContext().config.timerSort) {
                SORT_BY_TIMER_DURATION -> timers.sortBy { it.seconds }
                SORT_BY_DATE_CREATED -> timers.sortBy { it.id }
            }
            activity?.runOnUiThread {
                val currAdapter = binding.timersList.adapter
                if (currAdapter == null) {
                    TimerAdapter(activity as SimpleActivity, timers, binding.timersList) {
                        openEditTimer(it as Timer)
                    }.apply {
                        binding.timersList.adapter = this
                    }
                } else {
                    (currAdapter as TimerAdapter).apply {
                        updatePrimaryColor()
                        updateBackgroundColor(requireContext().getProperBackgroundColor())
                        updateTextColor(requireContext().getProperTextColor())
                        updateItems(this@TimerFragment.timers)
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TimerEvent.Refresh) {
        refreshTimers()
    }

    fun updateAlarmSound(alarmSound: AlarmSound) {
        currentEditAlarmDialog?.updateAlarmSound(alarmSound)
    }

    private fun openEditTimer(timer: Timer) {
        currentEditAlarmDialog = EditTimerDialog(activity as SimpleActivity, timer) {
            currentEditAlarmDialog = null
            refreshTimers()
        }
    }
}
