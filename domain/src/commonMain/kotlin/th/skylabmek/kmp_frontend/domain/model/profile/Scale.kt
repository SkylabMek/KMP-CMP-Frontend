package th.skylabmek.kmp_frontend.domain.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Scale(
    val id: String,
    val min: Double,
    val max: Double,
    @SerialName("is_decimal") val isDecimal: Boolean
) {
    companion object {
        val UNKNOWN = Scale("unknown", 0.0, 0.0, false)
        val SCALE_0_10_DECIMAL = Scale("scale_0_10_decimal", 0.0, 10.0, true)
        val SCALE_0_100_INT = Scale("scale_0_100_int", 0.0, 100.0, false)
        val SCALE_0_5_DECIMAL = Scale("scale_0_5_decimal", 0.0, 5.0, true)
        val SCALE_1_10_INT = Scale("scale_1_10_int", 1.0, 10.0, false)
        val SCALE_1_5_INT = Scale("scale_1_5_int", 1.0, 5.0, false)
        val SCALE_CEFR = Scale("scale_cefr", 1.0, 6.0, false)
        val SCALE_FAMILIAR_10 = Scale("scale_familiar_10", 0.0, 10.0, true)
        val SCALE_REFERENCE = Scale("scale_reference", 1.0, 1.0, false)
        val SCALE_FAMILIAR_TEXT = Scale("scale_familiar_text", 1.0, 4.0, false)
        val allScales = listOf(
            SCALE_0_10_DECIMAL,
            SCALE_0_100_INT,
            SCALE_0_5_DECIMAL,
            SCALE_1_10_INT,
            SCALE_1_5_INT,
            SCALE_CEFR,
            SCALE_FAMILIAR_10,
            SCALE_REFERENCE,
            SCALE_FAMILIAR_TEXT,
            UNKNOWN
        )

        fun fromId(id: String?): Scale = allScales.find { it.id == id } ?: UNKNOWN
    }
}
