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
    Azure(
        res = R.drawable.brand_azure,
        regex = "(?i)Azure"
    ),
    Binance(
        res = R.drawable.brand_binance,
        regex = "(?i)Binance"
    ),
    Bybit(
        res = R.drawable.brand_bybit,
        regex = "(?i)Bybit"
    ),
    Cloudflare(
        res = R.drawable.brand_cloudflare,
        regex = "(?i)Cloudflare"
    ),
    Coinbase(
        res = R.drawable.brand_coinbase,
        regex = "(?i)Coinbase"
    ),
    Crowdin(
        res = R.drawable.brand_crowdin,
        regex = "(?i)Crowdin"
    ),
    DigitalOcean(
        res = R.drawable.brand_digitalocean,
        regex = "(?i)Digital\\s*Ocean"
    ),
    Discord(
        res = R.drawable.brand_discord,
        regex = "(?i)Discord"
    ),
    Facebook(
        res = R.drawable.brand_facebook,
        regex = "(?i)Facebook"
    ),
    Gitea(
        res = R.drawable.brand_gitea,
        regex = "(?i)Gitea"
    ),
    Github(
        res = R.drawable.brand_github,
        regex = "(?i)GitHub"
    ),
    GitLab(
        res = R.drawable.brand_gitlab,
        regex = "(?i)GitLab"
    ),
    Google(
        res = R.drawable.brand_google,
        regex = "(?i)Google"
    ),
    Instagram(
        res = R.drawable.brand_instagram,
        regex = "(?i)Instagram"
    ),
    JetBrains(
        res = R.drawable.brand_jetbrains,
        regex = "(?i)JetBrains"
    ),
    Lark(
        res = R.drawable.brand_lark,
        regex = "(?i)Lark|Feishu"
    ),
    Mega(
        res = R.drawable.brand_mega,
        regex = "(?i)Mega"
    ),
    Meta(
        res = R.drawable.brand_meta,
        regex = "(?i)Meta"
    ),
    Microsoft(
        res = R.drawable.brand_microsoft,
        regex = "(?i)Microsoft"
    ),
    OKX(
        res = R.drawable.brand_okx,
        regex = "(?i)OKX"
    ),
    OnlyFans(
        res = R.drawable.brand_onlyfans,
        regex = "(?i)OnlyFans"
    ),
    OpenAI(
        res = R.drawable.brand_openai,
        regex = "(?i)OpenAI"
    ),
    ORCID(
        res = R.drawable.brand_orcid,
        regex = "(?i)ORCID"
    ),
    Patreon(
        res = R.drawable.brand_patreon,
        regex = "(?i)Patreon"
    ),
    PayPal(
        res = R.drawable.brand_paypal,
        regex = "(?i)PayPal"
    ),
    Pixiv(
        res = R.drawable.brand_pixiv,
        regex = "(?i)Pixiv"
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