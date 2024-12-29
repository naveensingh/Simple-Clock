package org.fossify.clock.helpers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import org.fossify.clock.R
import org.fossify.clock.activities.SplashActivity
import org.fossify.clock.extensions.config
import org.fossify.clock.extensions.getClosestEnabledAlarmString
import org.fossify.commons.extensions.applyColorFilter
import org.fossify.commons.extensions.getLaunchIntent
import org.fossify.commons.extensions.setText
import org.fossify.commons.extensions.setVisibleIf

class MyDigitalTimeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        performUpdate(context)
    }

    private fun performUpdate(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
        context.getClosestEnabledAlarmString { nextAlarm ->
            appWidgetManager.getAppWidgetIds(getComponentName(context)).forEach {
                RemoteViews(context.packageName, R.layout.widget_digital).apply {
                    updateTexts(context, this, nextAlarm)
                    updateColors(context, this)
                    setupAppOpenIntent(context, this)
                    appWidgetManager.updateAppWidget(it, this)
                }
            }
        }
    }

    private fun updateTexts(context: Context, views: RemoteViews, nextAlarm: String) {
        views.apply {
            setText(R.id.widget_next_alarm, nextAlarm)
            setVisibleIf(R.id.widget_alarm_holder, nextAlarm.isNotEmpty())
            val clockToHide = if (context.config.use24HourFormat) R.id.widget_text_clock_12 else R.id.widget_text_clock_24
            val clockToShow = if (context.config.use24HourFormat) R.id.widget_text_clock_24 else R.id.widget_text_clock_12
            setViewVisibility(clockToHide, View.GONE)
            setViewVisibility(clockToShow, View.VISIBLE)
        }
    }

    private fun updateColors(context: Context, views: RemoteViews) {
        val config = context.config
        val widgetTextColor = config.widgetTextColor

        views.apply {
            applyColorFilter(R.id.widget_background, config.widgetBgColor)
            setTextColor(R.id.widget_text_clock_24, widgetTextColor)
            setTextColor(R.id.widget_text_clock_12, widgetTextColor)
            setTextColor(R.id.widget_date, widgetTextColor)
            setTextColor(R.id.widget_next_alarm, widgetTextColor)

            val bitmap = getMultiplyColoredBitmap(R.drawable.ic_clock_shadowed, widgetTextColor, context)
            setImageViewBitmap(R.id.widget_next_alarm_image, bitmap)
        }
    }

    private fun getComponentName(context: Context) = ComponentName(context, this::class.java)

    private fun setupAppOpenIntent(context: Context, views: RemoteViews) {
        (context.getLaunchIntent() ?: Intent(context, SplashActivity::class.java)).apply {
            putExtra(OPEN_TAB, TAB_CLOCK)
            val pendingIntent = PendingIntent.getActivity(context, OPEN_APP_INTENT_ID, this, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_date_time_holder, pendingIntent)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        performUpdate(context)
    }

    private fun getMultiplyColoredBitmap(resourceId: Int, newColor: Int, context: Context): Bitmap {
        val options = BitmapFactory.Options()
        options.inMutable = true
        val bmp = BitmapFactory.decodeResource(context.resources, resourceId, options)
        val paint = Paint()
        val filter = PorterDuffColorFilter(newColor, PorterDuff.Mode.MULTIPLY)
        paint.colorFilter = filter
        val canvas = Canvas(bmp)
        canvas.drawBitmap(bmp, 0f, 0f, paint)
        return bmp
    }
}
