package com.thejuki.kformmaster.view

import android.app.Dialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatTextView
import android.view.View
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.R
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.FormPickerDropDownElement

/**
 * Form Picker DropDown ViewBinder
 *
 * View Binder for [FormPickerDropDownElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormPickerDropDownViewBinder(private val context: Context, private val formBuilder: FormBuildHelper) : BaseFormViewBinder() {
    var viewBinder = ViewBinder(R.layout.form_element, FormPickerDropDownElement::class.java) { model, finder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as AppCompatTextView
        val textViewError = finder.find(R.id.formElementError) as AppCompatTextView
        val itemView = finder.getRootView() as View
        baseSetup(model, textViewTitle, textViewError, itemView)

        val editTextValue = finder.find(R.id.formElementValue) as AppCompatEditText

        editTextValue.setText(model.valueAsString)
        editTextValue.hint = model.hint ?: ""

        // reformat the options in format needed
        val options = arrayOfNulls<CharSequence>(model.options?.size ?: 0)

        for (i in model.options!!.indices) {
            options[i] = model.options!![i].toString()
        }

        // prepare the dialog
        val alertDialog: Dialog?

        if (model.arrayAdapter != null) {
            alertDialog = AlertDialog.Builder(context)
                    .setTitle(model.dialogTitle ?: context.getString(R.string.form_master_pick_one))
                    .setAdapter(model.arrayAdapter, { _, which ->
                        editTextValue.setText(model.arrayAdapter!!.getItem(which).toString())
                        model.setValue(model.arrayAdapter!!.getItem(which))
                        model.setError(null) // Reset after value change
                        setError(textViewError, null) // Reset after value change
                        formBuilder.onValueChanged(model)
                        formBuilder.refreshView()
                    })
                    .create()
        } else {
            alertDialog = AlertDialog.Builder(context)
                    .setTitle(model.dialogTitle ?: context.getString(R.string.form_master_pick_one))
                    .setItems(options) { _, which ->
                        editTextValue.setText(options[which])
                        model.setValue(model.options!![which])
                        model.setError(null) // Reset after value change
                        setError(textViewError, null) // Reset after value change
                        formBuilder.onValueChanged(model)
                        formBuilder.refreshView()
                    }
                    .create()
        }

        setOnClickListener(editTextValue, itemView, alertDialog)
    }
}
