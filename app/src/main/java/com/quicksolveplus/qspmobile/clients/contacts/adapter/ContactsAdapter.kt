package com.quicksolveplus.qspmobile.clients.contacts.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quicksolveplus.qspmobile.R
import com.quicksolveplus.qspmobile.clients.contacts.model.ContactsItem
import com.quicksolveplus.qspmobile.databinding.LayoutItemContactsBinding

class ContactsAdapter(
    private var context: Context,
    private var contactsList: ArrayList<ContactsItem>,
    val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutItemContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val context: Context = itemView.context

        init {
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }


        fun setData(contactsItem: ContactsItem) {
            if (contactsItem.FirstName.isEmpty() && contactsItem.LastName.isEmpty()) {
                binding.tvName.text = contactsItem.CompanyName
            } else {
                binding.tvName.text = contactsItem.FirstName.plus(" ").plus(contactsItem.LastName)
            }

            if (contactsItem.Relationship.isNotEmpty() && contactsItem.ContactTypeDesc.isNotEmpty()) {
                binding.tvType.text = contactsItem.Relationship.plus(" | ")
                    .plus(translatedContactType(contactsItem.ContactTypeDesc))
            } else if (contactsItem.Relationship.isNotEmpty() && contactsItem.ContactTypeDesc.isEmpty()) {
                binding.tvType.text = contactsItem.Relationship
            } else if (contactsItem.ContactTypeDesc.isNotEmpty() && contactsItem.Relationship.isEmpty()) {
                binding.tvType.text = translatedContactType(contactsItem.ContactTypeDesc)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutItemContactsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(contactsList[position])
    }

    private fun translatedContactType(contactTypeDesc: String): String {
        val contactType: String = when (contactTypeDesc) {
            "Agency" -> context.resources.getString(R.string.str_agency)
            "Important" -> context.resources.getString(R.string.str_important)
            "Medical" -> context.resources.getString(R.string.str_medical)
            else -> contactTypeDesc
        }
        return contactType
    }
}