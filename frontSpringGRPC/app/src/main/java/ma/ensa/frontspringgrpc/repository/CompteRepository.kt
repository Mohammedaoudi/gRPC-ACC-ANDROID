package ma.ensa.frontspringgrpc.repository

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ma.projet.frontspringgrpc.stubs.Compte
import ma.projet.frontspringgrpc.stubs.CompteRequest
import ma.projet.frontspringgrpc.stubs.CompteServiceGrpc
import ma.projet.frontspringgrpc.stubs.GetAllComptesRequest
import ma.projet.frontspringgrpc.stubs.GetCompteByIdRequest
import ma.projet.frontspringgrpc.stubs.GetTotalSoldeRequest
import ma.projet.frontspringgrpc.stubs.SaveCompteRequest
import ma.projet.frontspringgrpc.stubs.SoldeStats
import ma.projet.frontspringgrpc.stubs.TypeCompte
import java.io.Closeable

class CompteRepository : Closeable {
    private val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress("160.168.0.64", 9090)
        .usePlaintext()
        .build()

    private val stub: CompteServiceGrpc.CompteServiceBlockingStub =
        CompteServiceGrpc.newBlockingStub(channel)

    suspend fun getAllComptes(): List<Compte> = withContext(Dispatchers.IO) {
        val request = GetAllComptesRequest.getDefaultInstance()
        stub.allComptes(request).comptesList
    }

    suspend fun getSoldeStats(): SoldeStats = withContext(Dispatchers.IO) {
        val request = GetTotalSoldeRequest.getDefaultInstance()
        stub.totalSolde(request).stats
    }

    suspend fun saveCompte(solde: Float, dateCreation: String, type: TypeCompte): Compte =
        withContext(Dispatchers.IO) {
            val compteRequest = CompteRequest.newBuilder()
                .setSolde(solde)
                .setDateCreation(dateCreation)
                .setType(type)
                .build()

            val request = SaveCompteRequest.newBuilder()
                .setCompte(compteRequest)
                .build()

            stub.saveCompte(request).compte
        }

    override fun close() {
        channel.shutdown()
    }
}
