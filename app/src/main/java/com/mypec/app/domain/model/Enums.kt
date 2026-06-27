package com.mypec.app.domain.model

enum class SessionStatus { PLANNED, IN_PROGRESS, COMPLETED, SKIPPED }

enum class Muscle(val display: String) {
    CHEST("Chest"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    QUADS("Quads"),
    HAMSTRINGS("Hamstrings"),
    GLUTES("Glutes"),
    CALVES("Calves"),
    CORE("Core"),
    FULL_BODY("Full body");

    companion object {
        fun fromName(name: String): Muscle = entries.firstOrNull { it.name == name } ?: FULL_BODY
    }
}

enum class RecordType(val display: String) {
    MAX_WEIGHT("Heaviest weight"),
    MAX_REPS("Most reps"),
    BEST_E1RM("Best estimated 1RM"),
    MAX_SET_VOLUME("Best set volume");

    companion object {
        fun fromName(name: String): RecordType = entries.firstOrNull { it.name == name } ?: MAX_WEIGHT
    }
}

enum class MeasurementType(val display: String) {
    CHEST("Chest"),
    ARM("Arm"),
    WAIST("Waist"),
    HIPS("Hips"),
    THIGH("Thigh"),
    CALF("Calf"),
    SHOULDERS("Shoulders");

    companion object {
        fun fromName(name: String): MeasurementType = entries.firstOrNull { it.name == name } ?: WAIST
    }
}
