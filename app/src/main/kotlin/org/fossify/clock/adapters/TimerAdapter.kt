package org.fossify.clock.adapters

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import me.grantland.widget.AutofitHelper
import org.fossify.clock.R
import org.fossify.clock.activities.SimpleActivity
import org.fossify.clock.databinding.ItemTimerBinding
import org.fossify.clock.extensions.getFormattedDuration
import org.fossify.clock.extensions.hideTimerNotification
import org.fossify.clock.extensions.secondsToMillis
import org.fossify.clock.models.Timer
import org.fossify.clock.models.TimerEvent
import org.fossify.clock.models.TimerState
import org.fossify.commons.adapters.MyRecyclerViewAdapter
import org.fossify.commons.dialogs.PermissionRequiredDialog
import org.fossify.commons.extensions.*
import org.fossify.commons.views.MyRecyclerView
import org.greenrobot.eventbus.EventBus

class TimerAdapter(
    activity: SimpleActivity,
    var timers: ArrayList<Timer>,
    recyclerView: MyRecyclerView,
    itemClick: (Any) -> Unit,
) : MyRecyclerViewAdapter(activity, recyclerView, itemClick) {

    init {
        setupDragListener(true)
    }

    override fun getActionMenuId() = R.menu.cab_alarms

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {
        if (selectedKeys.isEmpty()) {
            return
        }

        when (id) {
            R.id.cab_delete -> deleteItems()
        }
    }

    override fun getSelectableItemCount() = timers.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = timers.getOrNull(position)?.id

    override fun getItemKeyPosition(key: Int) = timers.indexOfFirst { it.id == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return createViewHolder(ItemTimerBinding.inflate(layoutInflater, parent, false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timer = timers[position]
        holder.bindView(timer, true, true) { itemView, _ ->
            setupView(itemView, timer)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = timers.size

    fun updateItems(newItems: ArrayList<Timer>) {
        timers = newItems
        notifyDataSetChanged()
        finishActMode()
    }

    private fun deleteItems() {
        val timersToRemove = ArrayList<Timer>()
        val positions = getSelectedItemPositions()
        getSelectedItems().forEach {
            timersToRemove.add(it)
        }
        timers.removeAll(timersToRemove)
        removeSelectedItems(positions)
        timersToRemove.forEach(::deleteTimer)
    }

    private fun getSelectedItems() = timers.filter { selectedKeys.contains(it.id) } as ArrayList<Timer>

    private fun setupView(view: View, timer: Timer) {
        ItemTimerBinding.bind(view).apply {
            val isSelected = selectedKeys.contains(timer.id)
            timerHolder.isSelected = isSelected

            timerLabel.setTextColor(textColor)
            timerLabel.setHintTextColor(textColor.adjustAlpha(0.7f))
            timerLabel.text = timer.label

            AutofitHelper.create(timerTime)
            timerTime.setTextColor(textColor)
            timerTime.text = when (timer.state) {
                is TimerState.Finished -> 0.getFormattedDuration()
                is TimerState.Idle -> timer.seconds.getFormattedDuration()
                is TimerState.Paused -> timer.state.tick.getFormattedDuration()
                is TimerState.Running -> timer.state.tick.getFormattedDuration()
            }

            timerReset.applyColorFilter(textColor)
            timerReset.setOnClickListener {
                resetTimer(timer)
            }

            timerPlayPause.applyColorFilter(textColor)
            timerPlayPause.setOnClickListener {
                (activity as SimpleActivity).handleNotificationPermission { granted ->
                    if (granted) {
                        when (val state = timer.state) {
                            is TimerState.Idle -> EventBus.getDefault().post(TimerEvent.Start(timer.id!!, timer.seconds.secondsToMillis))
                            is TimerState.Paused -> EventBus.getDefault().post(TimerEvent.Start(timer.id!!, state.tick))
                            is TimerState.Running -> EventBus.getDefault().post(TimerEvent.Pause(timer.id!!, state.tick))
                            is TimerState.Finished -> EventBus.getDefault().post(TimerEvent.Start(timer.id!!, timer.seconds.secondsToMillis))
                        }
                    } else {
                        PermissionRequiredDialog(
                            activity,
                            org.fossify.commons.R.string.allow_notifications_reminders,
                            { activity.openNotificationSettings() })
                    }
                }
            }

            val state = timer.state
            val resetPossible = state is TimerState.Running || state is TimerState.Paused || state is TimerState.Finished
            timerReset.beInvisibleIf(!resetPossible)
            val drawableId = if (state is TimerState.Running) {
                org.fossify.commons.R.drawable.ic_pause_vector
            } else {
                org.fossify.commons.R.drawable.ic_play_vector
            }
            timerPlayPause.setImageDrawable(activity.resources.getColoredDrawableWithColor(drawableId, textColor))
        }
    }

    private fun resetTimer(timer: Timer) {
        EventBus.getDefault().post(TimerEvent.Reset(timer.id!!))
        activity.hideTimerNotification(timer.id!!)
    }

    private fun deleteTimer(timer: Timer) {
        EventBus.getDefault().post(TimerEvent.Delete(timer.id!!))
        activity.hideTimerNotification(timer.id!!)
    }
}
