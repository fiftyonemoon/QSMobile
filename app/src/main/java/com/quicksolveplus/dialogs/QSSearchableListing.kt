package com.quicksolveplus.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.models.Clients
import com.quicksolveplus.qspmobile.clients.models.ClientsItem
import com.quicksolveplus.qspmobile.databinding.DialogQsSearchableListingBinding
import com.quicksolveplus.qspmobile.employee.model.Employees
import com.quicksolveplus.qspmobile.employee.model.EmployeesItem
import java.util.*

/**
 * 19/04/23.
 * Searchable Listing Dialog.
 *
 * @author hardkgosai.
 */
class QSSearchableListing(
    context: Context,
    private val title: String = "",
    private val clients: ArrayList<ClientsItem> = arrayListOf(),
    private val employees: ArrayList<EmployeesItem> = arrayListOf(),
    private val selectedItemName: String,
    private val isClient: Boolean = false,
    private val onItemSelect: (position: Int) -> Unit = {}
) : Dialog(context) {

    private lateinit var binding: DialogQsSearchableListingBinding
    private lateinit var searchEmployees: Employees
    private lateinit var searchClients: Clients

    init {
        searchClients.addAll(clients)
        searchEmployees.addAll(employees)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_insets))

        binding = DialogQsSearchableListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setListView()
    }

    private fun initUI() {
        if (clients.size > 4 || employees.size > 4) {
            window?.setLayout(
                LayoutParams.WRAP_CONTENT,
                context.resources.getDimension(com.intuit.sdp.R.dimen._200sdp).toInt()
            )
        }

        binding.tvTitle.text = title
        binding.tvError.apply {
            text =
                if (isClient) context.getString(R.string.str_no_client_msg) else context.getString(R.string.str_no_employee_msg)
            isVisible = if (isClient) clients.isEmpty() else employees.isEmpty()
        }
        binding.searchView.setOnQueryTextListener(queryTextListener)
    }

    private fun setListView() {
        binding.lv.adapter = if (isClient) getClientsAdapter() else getEmployeesAdapter()
        binding.lv.setOnItemClickListener { _, _, position, _ ->
            onItemSelect(position)
            dismiss()
        }
    }

    private fun getClientsAdapter(): ArrayAdapter<ClientsItem> {
        return object : ArrayAdapter<ClientsItem>(
            /* context = */ context,
            /* resource = */ R.layout.layout_item_list,
            /* textViewResourceId = */ R.id.tv,
            /* objects = */ clients
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val frame = view.findViewById<FrameLayout>(R.id.parent)
                val textView = view.findViewById<TextView>(R.id.tv)

                val client = clients[position]
                val name = "${client.FirstName} ${client.LastName}"
                textView.text = name

                val selected =
                    selectedItemName == name && selectedItemName != context.getString(R.string.str_select) && selectedItemName != context.getString(
                        R.string.lbl_select
                    )

                val bgColor = if (selected) ContextCompat.getColor(
                    context, R.color.app_color
                ) else ContextCompat.getColor(context, R.color.white)

                val textColor = if (selected) ContextCompat.getColor(
                    context, R.color.white
                ) else ContextCompat.getColor(context, R.color.app_color)

                frame.setBackgroundColor(bgColor)
                textView.setTextColor(textColor)
                return view
            }
        }
    }

    private fun getEmployeesAdapter(): ArrayAdapter<EmployeesItem> {
        return object : ArrayAdapter<EmployeesItem>(
            /* context = */ context,
            /* resource = */ R.layout.layout_item_list,
            /* textViewResourceId = */ R.id.tv,
            /* objects = */ employees
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val frame = view.findViewById<FrameLayout>(R.id.parent)
                val textView = view.findViewById<TextView>(R.id.tv)

                val employee = employees[position]
                val name = "${employee.FirstName} ${employee.LastName}"
                textView.text = name

                val selected =
                    selectedItemName == name && selectedItemName != context.getString(R.string.str_select) && selectedItemName != context.getString(
                        R.string.lbl_select
                    )

                val bgColor = if (selected) ContextCompat.getColor(
                    context, R.color.app_color
                ) else ContextCompat.getColor(context, R.color.white)

                val textColor = if (selected) ContextCompat.getColor(
                    context, R.color.white
                ) else ContextCompat.getColor(context, R.color.app_color)

                frame.setBackgroundColor(bgColor)
                textView.setTextColor(textColor)

                return view
            }
        }
    }

    private fun filterClients(text: String?) {
        val charText = text?.lowercase(Locale.getDefault()) ?: ""
        clients.clear()
        if (charText.isEmpty()) {
            clients.addAll(searchClients)
        } else {
            for (client in searchClients) {
                val name: String = client.FirstName + " " + client.LastName
                if (charText.startsWith(" ") || name.lowercase(Locale.getDefault())
                        .contains(charText)
                ) {
                    clients.add(client)
                }
            }
        }
        (binding.lv.adapter as ArrayAdapter<*>).notifyDataSetChanged()
    }

    private fun filterEmployees(text: String?) {
        val charText = text?.lowercase(Locale.getDefault()) ?: ""
        employees.clear()
        if (charText.isEmpty()) {
            employees.addAll(searchEmployees)
        } else {
            for (employee in searchEmployees) {
                val name: String = employee.FirstName + " " + employee.LastName
                if (charText.startsWith(" ") || name.lowercase(Locale.getDefault())
                        .contains(charText)
                ) {
                    employees.add(employee)
                }
            }
        }
        (binding.lv.adapter as ArrayAdapter<*>).notifyDataSetChanged()
    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            if (isClient) filterClients(newText)
            else filterEmployees(newText)
            return true
        }
    }
}