package dev.sanmer.logo

import androidx.annotation.DrawableRes

data class Logo(
    @field:DrawableRes val res: Int,
    val name: String,
    val refillable: Boolean
) {
    companion object Default {
        private val logos = hashMapOf<String, Logo?>()

        private fun getOrNull(name: String): Logo? {
            if (logos.contains(name)) return logos[name]

            val brand = Brand.entries.firstOrNull {
                it.regex.toRegex().matches(name)
            }
            if (brand != null) {
                return Logo(
                    res = brand.res,
                    name = brand.name,
                    refillable = false
                ).also { logos[name] = it }
            }

            val normal = Normal.entries.firstOrNull {
                it.regex.toRegex().containsMatchIn(name)
            }
            if (normal != null) {
                return Logo(
                    res = normal.res,
                    name = normal.name,
                    refillable = true
                ).also { logos[name] = it }
            }

            logos[name] = null
            return null
        }

        fun getOrDefault(name: String) =
            getOrNull(name) ?: Logo(
                res = R.drawable.normal_default,
                name = name,
                refillable = true
            )

        fun getOr(name: String, default: () -> Logo) =
            getOrNull(name) ?: default()
    }
}
