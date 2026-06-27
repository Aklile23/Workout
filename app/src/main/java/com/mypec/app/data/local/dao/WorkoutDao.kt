package com.mypec.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mypec.app.data.local.entity.SetLogEntity
import com.mypec.app.data.local.entity.WorkoutSessionEntity
import com.mypec.app.data.local.relation.SetLogWithExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Upsert
    suspend fun upsertSession(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getSession(id: String): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    fun observeSession(id: String): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE isDeleted = 0 ORDER BY dateEpochDay DESC, startedAt DESC")
    fun observeAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE isDeleted = 0 AND dateEpochDay BETWEEN :from AND :to ORDER BY dateEpochDay ASC")
    fun observeSessionsBetween(from: Long, to: Long): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE isDeleted = 0 AND dateEpochDay = :day LIMIT 1")
    suspend fun getSessionForDay(day: Long): WorkoutSessionEntity?

    @Query("UPDATE workout_sessions SET isDeleted = 1, updatedAt = :now, dirty = 1 WHERE dateEpochDay = :day AND status = 'SKIPPED' AND isDeleted = 0")
    suspend fun softDeleteSkippedForDay(day: Long, now: Long)

    @Query("SELECT * FROM workout_sessions WHERE status = 'IN_PROGRESS' AND isDeleted = 0 ORDER BY startedAt DESC LIMIT 1")
    fun observeActiveSession(): Flow<WorkoutSessionEntity?>

    @Upsert
    suspend fun upsertSet(set: SetLogEntity)

    @Delete
    suspend fun deleteSet(set: SetLogEntity)

    @Transaction
    @Query("SELECT * FROM set_logs WHERE sessionId = :sessionId AND isDeleted = 0 ORDER BY position ASC, setNumber ASC")
    fun observeSetsForSession(sessionId: String): Flow<List<SetLogWithExercise>>

    @Query("SELECT COALESCE(MAX(position), -1) FROM set_logs WHERE sessionId = :sessionId")
    suspend fun maxPosition(sessionId: String): Int

    @Query("SELECT * FROM set_logs WHERE sessionId = :sessionId AND isDeleted = 0 ORDER BY setNumber ASC")
    suspend fun getSetsForSession(sessionId: String): List<SetLogEntity>

    @Transaction
    @Query(
        "SELECT sl.* FROM set_logs sl " +
            "INNER JOIN workout_sessions ws ON sl.sessionId = ws.id " +
            "WHERE sl.exerciseId = :exerciseId AND sl.isDeleted = 0 AND sl.isCompleted = 1 " +
            "ORDER BY ws.dateEpochDay DESC, sl.setNumber ASC"
    )
    suspend fun getHistoryForExercise(exerciseId: String): List<SetLogWithExercise>

    @Query(
        "SELECT sl.* FROM set_logs sl " +
            "INNER JOIN workout_sessions ws ON sl.sessionId = ws.id " +
            "WHERE sl.exerciseId = :exerciseId AND sl.isDeleted = 0 AND sl.isCompleted = 1 " +
            "AND ws.id != :excludeSessionId " +
            "ORDER BY ws.dateEpochDay DESC, sl.setNumber ASC"
    )
    suspend fun getPreviousSets(exerciseId: String, excludeSessionId: String): List<SetLogEntity>

    @Query("SELECT * FROM set_logs WHERE isDeleted = 0 AND isCompleted = 1")
    fun observeAllCompletedSets(): Flow<List<SetLogEntity>>
}
