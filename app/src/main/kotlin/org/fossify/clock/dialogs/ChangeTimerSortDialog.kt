package org.fossify.clock.dialogs

import org.fossify.clock.R
import org.fossify.clock.databinding.DialogChangeTimerSortBinding
import org.fossify.clock.extensions.config
import org.fossify.clock.helpers.SORT_BY_CREATION_ORDER
import org.fossify.clock.helpers.SORT_BY_TIMER_DURATION
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.setupDialogStuff

class ChangeTimerSortDialog(val activity: BaseSimpleActivity, val callback: () -> Unit) {
    private val binding = DialogChangeTimerSortBinding.inflate(activity.layoutInflater).apply {
        val activeRadioButton = when (activity.config.timerSort) {
            SORT_BY_TIMER_DURATION -> sortingDialogRadioTimerDuration
            else -> sortingDialogRadioCreationOrder
        }
        activeRadioButton.isChecked = true
    }

    init {
        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.ok) { _, _ -> dialogConfirmed() }
            .setNegativeButton(org.fossify.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, org.fossify.commons.R.string.sort_by)
            }
    }

    private fun dialogConfirmed() {
        val sort = when (binding.sortingDialogRadioSorting.checkedRadioButtonId) {
            R.id.sorting_dialog_radio_timer_duration -> SORT_BY_TIMER_DURATION
            else -> SORT_BY_CREATION_ORDER
        }

        activity.config.timerSort = sort
        callback()
    }
}
