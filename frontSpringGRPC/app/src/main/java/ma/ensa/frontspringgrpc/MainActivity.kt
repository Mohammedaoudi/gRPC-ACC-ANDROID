package ma.ensa.frontspringgrpc

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ma.ensa.frontspringgrpc.adapter.CompteAdapter
import ma.ensa.frontspringgrpc.databinding.ActivityMainBinding
import ma.ensa.frontspringgrpc.databinding.DialogAddCompteBinding
import ma.ensa.frontspringgrpc.viewmodel.CompteViewModel
import ma.projet.frontspringgrpc.stubs.TypeCompte
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CompteViewModel by viewModels()
    private lateinit var comptesAdapter: CompteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSpinner()
        setupObservers()
        setupFab()

        viewModel.loadComptes()
        viewModel.loadStats()
    }

    private fun setupSpinner() {
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            TypeCompte.values().map { it.name }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun setupRecyclerView() {
        comptesAdapter = CompteAdapter { compte ->
            // Handle delete action
            Toast.makeText(this, "Delete clicked: ${compte.id}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = comptesAdapter
        }
    }

    private fun setupObservers() {
        viewModel.comptes.observe(this) { comptes ->
            comptesAdapter.submitList(comptes)
        }

        viewModel.stats.observe(this) { stats ->
            binding.totalComptesTextView.text = "Total Comptes: ${stats.count}"
            binding.totalSoldeTextView.text = "Total Solde: ${stats.sum} DH"
        }

        viewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddCompteDialog()
        }
    }

    private fun showAddCompteDialog() {
        val dialogBinding = DialogAddCompteBinding.inflate(layoutInflater)

        // Only include EPARGNE and COURANT types
        val accountTypes = listOf(TypeCompte.EPARGNE, TypeCompte.COURANT)

        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            accountTypes.map { it.name }  // This will only show "EPARGNE" and "COURANT"
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        dialogBinding.typeSpinner.adapter = typeAdapter

        MaterialAlertDialogBuilder(this)
            .setTitle("Ajouter un compte")
            .setView(dialogBinding.root)
            .setPositiveButton("Ajouter") { _, _ ->
                val solde = dialogBinding.soldeInput.text.toString().toFloatOrNull() ?: 0f
                val type = TypeCompte.valueOf(dialogBinding.typeSpinner.selectedItem.toString())
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                viewModel.saveCompte(solde, date, type)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

}