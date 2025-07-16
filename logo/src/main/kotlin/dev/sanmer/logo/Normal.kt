package dev.sanmer.logo

import androidx.annotation.DrawableRes

enum class Normal(
    @field:DrawableRes val res: Int,
    internal val regex: String
) {
    Cloud(
        res = R.drawable.normal_cloud,
        regex = "(?i)Cloud"
    )
}