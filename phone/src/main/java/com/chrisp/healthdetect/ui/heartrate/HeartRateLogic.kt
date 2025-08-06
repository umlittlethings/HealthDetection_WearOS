package com.chrisp.healthdetect.ui.heartrate

data class HeartRateInterpretation(
    val range: String,
    val interpretation: String,
    val possibleCauses: String,
    val description: String
)

val heartRateInterpretationTable = listOf(
    HeartRateInterpretation(
        range = "< 60 BPM",
        interpretation = "Bradikardia (Lambat)",
        possibleCauses = "Atlet, hipotiroid, obat beta-blocker, syok",
        description = "Detak jantung Anda lebih lambat dari rentang normal. Ini bisa normal bagi atlet, namun bisa juga menandakan kondisi medis tertentu."
    ),
    HeartRateInterpretation(
        range = "60 - 100 BPM",
        interpretation = "Normal",
        possibleCauses = "Normal fisiologis",
        description = "Detak jantung Anda menampilkan hasil normal secara fisiologis"
    ),
    HeartRateInterpretation(
        range = "101 - 120 BPM",
        interpretation = "Takikardia Ringan",
        possibleCauses = "Demam, stres, dehidrasi",
        description = "Detak jantung Anda sedikit lebih cepat dari normal. Hal ini sering disebabkan oleh faktor sementara seperti stres atau demam."
    ),
    HeartRateInterpretation(
        range = "> 120 BPM",
        interpretation = "Takikardia Sedang-Berat",
        possibleCauses = "Infeksi berat, anemia, gagal jantung, syok",
        description = "Detak jantung Anda jauh lebih cepat dari normal. Ini bisa menandakan kondisi serius yang memerlukan perhatian medis."
    )
)

fun getInterpretationForBpm(bpm: Int): HeartRateInterpretation {
    return when {
        bpm < 60 -> heartRateInterpretationTable[0]
        bpm in 60..100 -> heartRateInterpretationTable[1]
        bpm in 101..120 -> heartRateInterpretationTable[2]
        else -> heartRateInterpretationTable[3]
    }
}