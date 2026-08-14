package dev.sanmer.authenticator.database.model

import androidx.room3.Embedded
import androidx.room3.Relation

data class AuthProperties(
    @Embedded
    val auth: Auth,
    @Relation(parentColumns = ["id"], entityColumns = ["authId"])
    val properties: List<AuthProperty>,
) {
    private val propertyMap by lazy { properties.associateBy { it.key } }

    fun <T> getValue(key: AuthProperty.Key, transform: (String) -> T) =
        transform(requireNotNull(propertyMap[key]) { "Expect $key" }.value)

    fun <T> getValue(key: AuthProperty.Key, default: T, transform: (String) -> T) =
        propertyMap[key]?.let { transform(it.value) } ?: default

    inline fun protectValue(transform: (String) -> String) = copy(
        auth = auth,
        properties = properties.map {
            when (it.key) {
                AuthProperty.Key.Secret -> it.copy(value = transform(it.value))
                else -> it
            }
        }
    )

    companion object Default {
        fun build(
            auth: Auth,
            properties: List<Pair<AuthProperty.Key, String>>
        ) = AuthProperties(
            auth = auth,
            properties = properties.map { (key, value) ->
                AuthProperty(authId = auth.id, key = key, value = value)
            }
        )
    }
}
