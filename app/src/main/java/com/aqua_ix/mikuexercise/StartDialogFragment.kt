package com.aqua_ix.mikuexercise

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class StartDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            isCancelable = false
            val builder = AlertDialog.Builder(it)
            arguments?.let { msg ->
                builder.setMessage(msg.getInt("msg"))
            } ?: builder.setMessage(R.string.dialog_text_situp)
            builder.setPositiveButton(
                R.string.dialog_start,
                DialogButtonClickListener()
            )
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    inner class DialogButtonClickListener : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            val a = activity
            if (a is DialogInterface.OnClickListener) {
                a.onClick(dialog, which)
            }
        }
    }
}