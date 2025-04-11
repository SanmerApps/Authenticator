package dev.sanmer.logo

import androidx.annotation.DrawableRes

@Suppress("SpellCheckingInspection")
enum class Brand(
    @DrawableRes
    val res: Int,
    internal val regex: String
) {
    Aliyun(
        res = R.drawable.brand_aliyun,
        regex = "(?i)Aliyun|Alibaba\\s*Cloud"
    ),
    Apple(
        res = R.drawable.brand_apple,
        regex = "(?i)Apple"
    ),
    AWS(
        res = R.drawable.brand_aws,
        regex = "(?i)AWS|Amazon\\s*Web\\s*Service"
    ),
    Cloudflare(
        res = R.drawable.brand_cloudflare,
        regex = "(?i)Cloudflare"
    ),
    Gitea(
        res = R.drawable.brand_gitea,
        regex = "(?i)Gitea"
    ),
    Github(
        res = R.drawable.brand_github,
        regex = "(?i)GitHub"
    ),
    Google(
        res = R.drawable.brand_google,
        regex = "(?i)Google"
    ),
    JetBrains(
        res = R.drawable.brand_jetbrains,
        regex = "(?i)JetBrains"
    ),
    Lark(
        res = R.drawable.brand_lark,
        regex = "(?i)Lark|Feishu"
    ),
    Meta(
        res = R.drawable.brand_meta,
        regex = "(?i)Meta|Facebook"
    ),
    Microsoft(
        res = R.drawable.brand_microsoft,
        regex = "(?i)Microsoft"
    ),
    OKX(
        res = R.drawable.brand_okx,
        regex = "(?i)OKX"
    ),
    ORCID(
        res = R.drawable.brand_orcid,
        regex = "(?i)ORCID"
    ),
    TencentCloud(
        res = R.drawable.brand_tencent_cloud,
        regex = "(?i)Tencent\\s*Cloud"
    ),
    Wise(
        res = R.drawable.brand_wise,
        regex = "(?i)Wise"
    ),
    X(
        res = R.drawable.brand_x,
        regex = "(?i)X|Twitter"
    )
}