package data

import data.model.Country
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import kotlinx.coroutines.delay

object CountryApiClient {
    val format = Json { ignoreUnknownKeys = true }
    private val client = HttpClient(CIO){
        install(ContentNegotiation){
            json(format)
        }
    }
    suspend fun getCountries(): List<Country> {
        val url = "https://restcountries.com/v3.1/region/europe"
        val maxAttempts = 5
        var attempts = 0
        val delayTime = 2000L  // 2 секунди затримки між спробами

        while (attempts < maxAttempts) {
            try {
                val response: HttpResponse = client.get(url)
                if (response.status.isSuccess()) {
                    return response.body<List<Country>>() ?: emptyList()
                } else {
                    println("Server returned error: ${response.status}")
                }
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
            }

            attempts++
            if (attempts < maxAttempts) {
                println("Retrying... ($attempts/$maxAttempts)")
                delay(delayTime)
            }
        }

        println("Failed to fetch data after $maxAttempts attempts")
        return emptyList()
    }



}
