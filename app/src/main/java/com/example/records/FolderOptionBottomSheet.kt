package com.example.records


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FolderOptionsBottomSheet(
    private val onRenameClick: () -> Unit,
    private val onDeleteClick: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_folder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listeners
        view.findViewById<TextView>(R.id.renameOption).setOnClickListener {
            dismiss() // Close the bottom sheet
            onRenameClick() // Trigger the rename action
        }

        view.findViewById<TextView>(R.id.deleteOption).setOnClickListener {
            dismiss() // Close the bottom sheet
            onDeleteClick() // Trigger the delete action
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.BLACK) // Set background to black
        }
        return dialog
    }
}
