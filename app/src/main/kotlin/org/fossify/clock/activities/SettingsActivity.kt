package org.fossify.clock.activities

import android.content.Intent
import android.os.Bundle
import org.fossify.clock.databinding.ActivitySettingsBinding
import org.fossify.clock.extensions.config
import org.fossify.clock.helpers.DEFAULT_MAX_ALARM_REMINDER_SECS
import org.fossify.clock.helpers.DEFAULT_MAX_TIMER_REMINDER_SECS
import org.fossify.commons.dialogs.RadioGroupDialog
import org.fossify.commons.extensions.*
import org.fossify.commons.helpers.IS_CUSTOMIZING_COLORS
import org.fossify.commons.helpers.MINUTE_SECONDS
import org.fossify.commons.helpers.NavigationIcon
import org.fossify.commons.helpers.isTiramisuPlus
import org.fossify.commons.models.RadioItem
import java.util.Locale
import kotlin.system.exitProcess

class SettingsActivity : SimpleActivity() {
    private val binding: ActivitySettingsBinding by viewBinding(ActivitySettingsBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        updateMaterialActivityViews(binding.settingsCoordinator, binding.settingsHolder, useTransparentNavigation = true, useTopSearchMenu = false)
        setupMaterialScrollListener(binding.settingsNestedScrollview, binding.settingsToolbar)
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(binding.settingsToolbar, NavigationIcon.Arrow)

        setupPurchaseThankYou()
        setupCustomizeColors()
        setupUseEnglish()
        setupLanguage()
        setupPreventPhoneFromSleeping()
        setupStartWeekOn()
        setupAlarmMaxReminder()
        setupUseSameSnooze()
        setupSnoozeTime()
        setupTimerMaxReminder()
        setupIncreaseVolumeGradually()
        setupCustomizeWidgetColors()
        updateTextColors(binding.settingsHolder)

        arrayOf(
            binding.settingsColorCustomizationSectionLabel,
            binding.settingsGeneralSettingsLabel,
            binding.settingsAlarmTabLabel,
            binding.settingsTimerTabLabel,
        ).forEach {
            it.setTextColor(getProperPrimaryColor())
        }
    }

    private fun setupPurchaseThankYou() {
        binding.settingsPurchaseThankYouHolder.beGoneIf(isOrWasThankYouInstalled())
        binding.settingsPurchaseThankYouHolder.setOnClickListener {
            launchPurchaseThankYouIntent()
        }
    }

    private fun setupCustomizeColors() {
        binding.settingsColorCustomizationLabel.text = getCustomizeColorsString()
        binding.settingsColorCustomizationHolder.setOnClickListener {
            handleCustomizeColorsClick()
        }
    }

    private fun setupUseEnglish() {
        binding.settingsUseEnglishHolder.beVisibleIf((config.wasUseEnglishToggled || Locale.getDefault().language != "en") && !isTiramisuPlus())
        binding.settingsUseEnglish.isChecked = config.useEnglish
        binding.settingsUseEnglishHolder.setOnClickListener {
            binding.settingsUseEnglish.toggle()
            config.useEnglish = binding.settingsUseEnglish.isChecked
            exitProcess(0)
        }
    }

    private fun setupLanguage() {
        binding.settingsLanguage.text = Locale.getDefault().displayLanguage
        binding.settingsLanguageHolder.beVisibleIf(isTiramisuPlus())
        binding.settingsLanguageHolder.setOnClickListener {
            launchChangeAppLanguageIntent()
        }
    }

    private fun setupPreventPhoneFromSleeping() {
        binding.settingsPreventPhoneFromSleeping.isChecked = config.preventPhoneFromSleeping
        binding.settingsPreventPhoneFromSleepingHolder.setOnClickListener {
            binding.settingsPreventPhoneFromSleeping.toggle()
            config.preventPhoneFromSleeping = binding.settingsPreventPhoneFromSleeping.isChecked
        }
    }

    private fun setupStartWeekOn() {
        val items = arrayListOf(
            RadioItem(6, getString(org.fossify.commons.R.string.sunday)),
            RadioItem(0, getString(org.fossify.commons.R.string.monday)),
            RadioItem(1, getString(org.fossify.commons.R.string.tuesday)),
            RadioItem(2, getString(org.fossify.commons.R.string.wednesday)),
            RadioItem(3, getString(org.fossify.commons.R.string.thursday)),
            RadioItem(4, getString(org.fossify.commons.R.string.friday)),
            RadioItem(5, getString(org.fossify.commons.R.string.saturday)),
        )

        binding.settingsStartWeekOn.text = resources.getStringArray(org.fossify.commons.R.array.week_days)[config.firstDayOfWeek]
        binding.settingsStartWeekOnHolder.setOnClickListener {
            RadioGroupDialog(this@SettingsActivity, items, config.firstDayOfWeek) { day ->
                val firstDayOfWeek = day as Int
                config.firstDayOfWeek = firstDayOfWeek
                binding.settingsStartWeekOn.text = resources.getStringArray(org.fossify.commons.R.array.week_days)[config.firstDayOfWeek]
            }
        }
    }

    private fun setupAlarmMaxReminder() {
        updateAlarmMaxReminderText()
        binding.settingsAlarmMaxReminderHolder.setOnClickListener {
            showPickSecondsDialog(config.alarmMaxReminderSecs, true, true) {
                config.alarmMaxReminderSecs = if (it != 0) it else DEFAULT_MAX_ALARM_REMINDER_SECS
                updateAlarmMaxReminderText()
            }
        }
    }

    private fun setupUseSameSnooze() {
        binding.settingsSnoozeTimeHolder.beVisibleIf(config.useSameSnooze)
        binding.settingsUseSameSnooze.isChecked = config.useSameSnooze
        binding.settingsUseSameSnoozeHolder.setOnClickListener {
            binding.settingsUseSameSnooze.toggle()
            config.useSameSnooze = binding.settingsUseSameSnooze.isChecked
            binding.settingsSnoozeTimeHolder.beVisibleIf(config.useSameSnooze)
        }
    }

    private fun setupSnoozeTime() {
        updateSnoozeText()
        binding.settingsSnoozeTimeHolder.setOnClickListener {
            showPickSecondsDialog(config.snoozeTime * MINUTE_SECONDS, true) {
                config.snoozeTime = it / MINUTE_SECONDS
                updateSnoozeText()
            }
        }
    }

    private fun setupTimerMaxReminder() {
        updateTimerMaxReminderText()
        binding.settingsTimerMaxReminderHolder.setOnClickListener {
            showPickSecondsDialog(config.timerMaxReminderSecs, true, true) {
                config.timerMaxReminderSecs = if (it != 0) it else DEFAULT_MAX_TIMER_REMINDER_SECS
                updateTimerMaxReminderText()
            }
        }
    }

    private fun setupIncreaseVolumeGradually() {
        binding.settingsIncreaseVolumeGradually.isChecked = config.increaseVolumeGradually
        binding.settingsIncreaseVolumeGraduallyHolder.setOnClickListener {
            binding.settingsIncreaseVolumeGradually.toggle()
            config.increaseVolumeGradually = binding.settingsIncreaseVolumeGradually.isChecked
        }
    }

    private fun updateSnoozeText() {
        binding.settingsSnoozeTime.text = formatMinutesToTimeString(config.snoozeTime)
    }

    private fun updateAlarmMaxReminderText() {
        binding.settingsAlarmMaxReminder.text = formatSecondsToTimeString(config.alarmMaxReminderSecs)
    }

    private fun updateTimerMaxReminderText() {
        binding.settingsTimerMaxReminder.text = formatSecondsToTimeString(config.timerMaxReminderSecs)
    }

    private fun setupCustomizeWidgetColors() {
        binding.settingsWidgetColorCustomizationHolder.setOnClickListener {
            Intent(this, WidgetDigitalConfigureActivity::class.java).apply {
                putExtra(IS_CUSTOMIZING_COLORS, true)
                startActivity(this)
            }
        }
    }
}
