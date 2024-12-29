package org.fossify.clock.dialogs

import androidx.appcompat.app.AlertDialog
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.dialogs.FilePickerDialog
import org.fossify.commons.extensions.*
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.clock.R
import org.fossify.clock.databinding.DialogExportDataBinding
import org.fossify.clock.extensions.config
import org.fossify.clock.helpers.DATA_EXPORT_EXTENSION
import java.io.File

class ExportDataDialog(
    val activity: BaseSimpleActivity,
    val path: String,
    val hidePath: Boolean,
    callback: (file: File) -> Unit,
) {
    private var realPath = path.ifEmpty { activity.internalStoragePath }
    private val config = activity.config

    init {
        val view = DialogExportDataBinding.inflate(activity.layoutInflater, null, false).apply {
            exportDataFolder.text = activity.humanizePath(realPath)
            exportDataFilename.setText("${activity.getString(R.string.settings_export_data)}_${activity.getCurrentFormattedDateTime()}")

            if (hidePath) {
                exportDataFolderLabel.beGone()
                exportDataFolder.beGone()
            } else {
                exportDataFolder.setOnClickListener {
                    FilePickerDialog(activity, realPath, false, showFAB = true) {
                        exportDataFolder.text = activity.humanizePath(it)
                        realPath = it
                    }
                }
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.ok, null)
            .setNegativeButton(org.fossify.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(view.root, this, R.string.settings_export_data) { alertDialog ->
                    alertDialog.showKeyboard(view.exportDataFilename)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val filename = view.exportDataFilename.value
                        when {
                            filename.isEmpty() -> activity.toast(org.fossify.commons.R.string.empty_name)
                            filename.isAValidFilename() -> {
                                val file = File(realPath, "$filename$DATA_EXPORT_EXTENSION")
                                if (!hidePath && file.exists()) {
                                    activity.toast(org.fossify.commons.R.string.name_taken)
                                    return@setOnClickListener
                                }

                                ensureBackgroundThread {
                                    config.lastDataExportPath = file.absolutePath.getParentPath()
                                    callback(file)
                                    alertDialog.dismiss()
                                }
                            }

                            else -> activity.toast(org.fossify.commons.R.string.invalid_name)
                        }
                    }
                }
            }
    }
}

