package ma.ensa.frontspringgrpc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.frontspringgrpc.databinding.ItemCompteBinding
import ma.projet.frontspringgrpc.stubs.Compte




class CompteAdapter(
    private val onDeleteClick: (Compte) -> Unit
) : ListAdapter<Compte, CompteAdapter.CompteViewHolder>(CompteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        return CompteViewHolder(
            ItemCompteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CompteViewHolder(
        private val binding: ItemCompteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(compte: Compte) {
            binding.apply {
                idTextView.text = "ID: ${compte.id}"
                soldeTextView.text = "${compte.solde} DH"
                typeTextView.text = "Type: ${compte.type.name}"
                dateTextView.text = "Date: ${compte.dateCreation}"

                deleteButton.setOnClickListener {
                    onDeleteClick(compte)
                }
            }
        }
    }

    private class CompteDiffCallback : DiffUtil.ItemCallback<Compte>() {
        override fun areItemsTheSame(oldItem: Compte, newItem: Compte) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Compte, newItem: Compte) =
            oldItem == newItem
    }
}
