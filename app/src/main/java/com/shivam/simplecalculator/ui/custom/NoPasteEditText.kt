package com.shivam.simplecalculator.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText

class NoPasteEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        background = null
        highlightColor = Color.TRANSPARENT
        
        setPadding(0, 0, 0, 0)
        includeFontPadding = false
        minHeight = 0
        minimumHeight = 0
        
        inputType = inputType or android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        val disablePasteCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean = false
            override fun onDestroyActionMode(mode: ActionMode?) {}
        }
        customSelectionActionModeCallback = disablePasteCallback
        customInsertionActionModeCallback = disablePasteCallback
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        if (id == android.R.id.paste || id == android.R.id.pasteAsPlainText) {
            Toast.makeText(context, "Pasting is disabled", Toast.LENGTH_SHORT).show()
            return false
        }
        return super.onTextContextMenuItem(id)
    }
}
