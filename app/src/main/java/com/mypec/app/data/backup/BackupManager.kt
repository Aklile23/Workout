package com.mypec.app.data.backup

import com.mypec.app.domain.repository.MetricsRepository
import com.mypec.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
private data class BackupSession(
    val title: String,
    val dateEpochDay: Long,
    val status: String,
)

@Serializable
private data class BackupBodyWeight(val dateEpochDay: Long, val weightKg: Double)

@Serializable
private data class BackupPayload(
    val version: Int = 1,
    val exportedAtMillis: Long,
    val sessions: List<BackupSession>,
    val bodyWeights: List<BackupBodyWeight>,
)

@Singleton
class BackupManager @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val metricsRepository: MetricsRepository,
) {
    private val json = Json { prettyPrint = true }

    suspend fun exportJson(): String {
        val sessions = workoutRepository.observeAllSessions().first()
            .map { BackupSession(it.title, it.dateEpochDay, it.status) }
        val weights = metricsRepository.observeBodyWeights().first()
            .map { BackupBodyWeight(it.dateEpochDay, it.weightKg) }
        val payload = BackupPayload(
            exportedAtMillis = System.currentTimeMillis(),
            sessions = sessions,
            bodyWeights = weights,
        )
        return json.encodeToString(payload)
    }

    suspend fun exportCsv(): String {
        val sessions = workoutRepository.observeAllSessions().first()
        val sb = StringBuilder("date_epoch_day,title,status\n")
        sessions.forEach { sb.append("${it.dateEpochDay},\"${it.title}\",${it.status}\n") }
        return sb.toString()
    }
}
