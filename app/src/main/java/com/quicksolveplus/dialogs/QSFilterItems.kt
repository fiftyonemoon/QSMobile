package com.quicksolveplus.dialogs

import android.app.Dialog
import android.content.Context

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.databinding.DialogQsFilterBinding

/**
 * 17/03/23.
 * Filter Dialog.
 *
 * @author hardkgosai.
 */
class QSFilterItems(
    context: Context,
    private val title: String = "",
    private val items: ArrayList<String> = arrayListOf(),
    private val itemsTextGravity: Int = Gravity.START,
    private val fromState: Boolean = false,
    private val showError: Boolean = false,
    private val selectedItemPosition: Int = -1,
    private val onItemSelect: (position: Int) -> Unit = {}
) : Dialog(context) {

    private lateinit var binding: DialogQsFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogQsFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setListView()
    }

    private fun initUI() {
        if (items.size > 4) {
            window?.setLayout(
                LayoutParams.WRAP_CONTENT,
                context.resources.getDimension(com.intuit.sdp.R.dimen._200sdp).toInt()
            )
        }
        binding.lv.isVisible = true
        binding.cb.isVisible = false
        binding.divider2.isVisible = false
        binding.tvOk.isVisible = false
        binding.tvError.isVisible = items.isEmpty() && showError

        binding.tvTitle.text = title
    }

    private fun setListView() {
        val adapter = object : ArrayAdapter<String>(
            /* context = */ context,
            /* resource = */ R.layout.layout_item_list,
            /* textViewResourceId = */ R.id.tv,
            /* objects = */ items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textview = view.findViewById<TextView>(R.id.tv)
                val radioView = view.findViewById<RadioButton>(R.id.radioView)

                if (fromState) {
                    textview.isVisible = false
                    radioView.isVisible = true
                    radioView.text = items[position]
                    radioView.isChecked = position == selectedItemPosition
                    radioView.setOnClickListener {
                        onItemSelect(position)
                        dismiss()
                    }
                } else {
                    textview.isVisible = true
                    radioView.isVisible = false
                    textview.text = items[position]
                    textview.gravity = itemsTextGravity
                }

                return view
            }
        }

        binding.lv.adapter = adapter
        binding.lv.setOnItemClickListener { _, _, position, _ ->
            onItemSelect(position)
            dismiss()
        }
    }
}