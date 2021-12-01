package com.syukron.mymealdiary.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.syukron.mymealdiary.databinding.DialogEditTextBinding

typealias DialogWithTextFieldClickListener = (
    dialog: DialogInterface,
    which: Int,
    editText: AppCompatEditText
) -> Unit

fun showDialogWithTextField(
    context: Context,
    title: String,
    inputType: Int? = null,
    hint: String? = null,
    positiveText: String = "Ok",
    negativeText: String = "Cancel",
    positiveListener: DialogWithTextFieldClickListener? = null,
    negativeListener: DialogWithTextFieldClickListener? = null
) {
    // EditText settings
    val dialogLayout =
        DialogEditTextBinding.inflate(LayoutInflater.from(context))
    val editText = dialogLayout.edit
    editText.apply {
        this.hint = hint
        if (inputType != null) this.inputType = inputType
        requestFocus()
    }
    // Dialog settings
    val dialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setView(editText.rootView)
        .setPositiveButton(positiveText) { dialog, which ->
            if (TextUtils.isEmpty(editText.text.toString())) {
                Toast.makeText(context, "No value set change", Toast.LENGTH_SHORT).show()
            }
            else {
                positiveListener?.invoke(dialog, which, editText)
                Toast.makeText(context, "Value set is changed !", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton(negativeText) { dialog, which ->
            negativeListener?.invoke(dialog, which, editText)
        }
        .create()
    // Dismiss focus when leaving the dialog

    dialog.window?.clearFlags(
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
    )
    // Opens keyboard when the dialog appears
    dialog.window?.setSoftInputMode(
        WindowManager.LayoutParams
            .SOFT_INPUT_STATE_VISIBLE
    )
    dialog.show()
}
