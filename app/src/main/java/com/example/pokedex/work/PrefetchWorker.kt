package com.example.pokedex.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ImageRequest
import com.example.pokedex.data.PokemonRep
import kotlinx.coroutines.CancellationException
import java.util.concurrent.TimeUnit

// фоновая задача: подгружает картинки первых покемонов, чтобы список открывался быстрее
class PrefetchWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result =
        try {
            val items = PokemonRep().getAll()
            val loader = applicationContext.imageLoader
            items.take(20).forEach { item ->
                loader.execute(
                    ImageRequest.Builder(applicationContext)
                        .data(item.imageUrl)
                        .build()
                )
            }
            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.retry()
        }
}

fun schedulePrefetch(context: Context) {
    val request = PeriodicWorkRequestBuilder<PrefetchWorker>(6, TimeUnit.HOURS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "pokemon_prefetch",
        ExistingPeriodicWorkPolicy.KEEP,
        request
    )
}