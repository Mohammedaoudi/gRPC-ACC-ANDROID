package ma.ensa.frontspringgrpc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ma.ensa.frontspringgrpc.repository.CompteRepository
import ma.projet.frontspringgrpc.stubs.Compte
import ma.projet.frontspringgrpc.stubs.CompteRequest
import ma.projet.frontspringgrpc.stubs.CompteServiceGrpc
import ma.projet.frontspringgrpc.stubs.GetAllComptesRequest
import ma.projet.frontspringgrpc.stubs.GetTotalSoldeRequest
import ma.projet.frontspringgrpc.stubs.SaveCompteRequest
import ma.projet.frontspringgrpc.stubs.SoldeStats
import ma.projet.frontspringgrpc.stubs.TypeCompte

class CompteViewModel : ViewModel() {
    private val repository = CompteRepository()

    private val _comptes = MutableLiveData<List<Compte>>()
    val comptes: LiveData<List<Compte>> = _comptes

    private val _stats = MutableLiveData<SoldeStats>()
    val stats: LiveData<SoldeStats> = _stats

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadComptes() {
        viewModelScope.launch {
            try {
                _comptes.value = repository.getAllComptes()
                loadStats() // Refresh stats when accounts are loaded
            } catch (e: Exception) {
                _error.value = "Error loading accounts: ${e.message}"
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            try {
                _stats.value = repository.getSoldeStats()
            } catch (e: Exception) {
                _error.value = "Error loading stats: ${e.message}"
            }
        }
    }

    fun saveCompte(solde: Float, dateCreation: String, type: TypeCompte) {
        viewModelScope.launch {
            try {
                repository.saveCompte(solde, dateCreation, type)
                loadComptes() // Refresh the list after saving
            } catch (e: Exception) {
                _error.value = "Error saving account: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}