package dev.sanmer.brand

import androidx.annotation.DrawableRes

@Suppress("SpellCheckingInspection")
enum class Brand(
    @DrawableRes val id: Int,
    private val regex: Lazy<Regex>
) {
    Adobe(
        id = R.drawable.brand_adobe,
        regex = lazy { "(?i)Adobe(.com)?".toRegex() }
    ),
    Aliyun(
        id = R.drawable.brand_aliyun,
        regex = lazy { "(?i)Aliyun(.com)?|Alibaba\\s*Cloud".toRegex() }
    ),
    AMD(
        id = R.drawable.brand_amd,
        regex = lazy { "(?i)AMD(.com)?".toRegex() }
    ),
    Apple(
        id = R.drawable.brand_apple,
        regex = lazy { "(?i)Apple(.com)?".toRegex() }
    ),
    Atlassian(
        id = R.drawable.brand_atlassian,
        regex = lazy { "(?i)Atlassian(.com)?".toRegex() }
    ),
    AWS(
        id = R.drawable.brand_aws,
        regex = lazy { "(?i)AWS(.com|.Amazon.com)?|Amazon\\s*Web\\s*Service".toRegex() }
    ),
    Azure(
        id = R.drawable.brand_azure,
        regex = lazy { "(?i)Azure(.com|.Microsoft.com)?|Microsoft\\s*Azure".toRegex() }
    ),
    Binance(
        id = R.drawable.brand_binance,
        regex = lazy { "(?i)Binance(.com)?".toRegex() }
    ),
    Bybit(
        id = R.drawable.brand_bybit,
        regex = lazy { "(?i)Bybit(.com)?".toRegex() }
    ),
    Canva(
        id = R.drawable.brand_canva,
        regex = lazy { "(?i)Canva(.com)?".toRegex() }
    ),
    Cloudflare(
        id = R.drawable.brand_cloudflare,
        regex = lazy { "(?i)Cloudflare(.com)?".toRegex() }
    ),
    Coinbase(
        id = R.drawable.brand_coinbase,
        regex = lazy { "(?i)Coinbase(.com)?".toRegex() }
    ),
    Crowdin(
        id = R.drawable.brand_crowdin,
        regex = lazy { "(?i)Crowdin(.com)?".toRegex() }
    ),
    DigitalOcean(
        id = R.drawable.brand_digitalocean,
        regex = lazy { "(?i)DigitalOcean(.com)?".toRegex() }
    ),
    Discord(
        id = R.drawable.brand_discord,
        regex = lazy { "(?i)Discord(.com)?".toRegex() }
    ),
    Docker(
        id = R.drawable.brand_docker,
        regex = lazy { "(?i).*Docker.*".toRegex() }
    ),
    Dynatrace(
        id = R.drawable.brand_dynatrace,
        regex = lazy { "(?i)Dynatrace(.com)?".toRegex() }
    ),
    Facebook(
        id = R.drawable.brand_facebook,
        regex = lazy { "(?i)Facebook(.com)?".toRegex() }
    ),
    Fansly(
        id = R.drawable.brand_fansly,
        regex = lazy { "(?i)Fansly(.com)?".toRegex() }
    ),
    Figma(
        id = R.drawable.brand_figma,
        regex = lazy { "(?i)Figma(.com)?".toRegex() }
    ),
    Gitea(
        id = R.drawable.brand_gitea,
        regex = lazy { "(?i).*Gitea.*".toRegex() }
    ),
    GitHub(
        id = R.drawable.brand_github,
        regex = lazy { "(?i).*GitHub.*".toRegex() }
    ),
    GitLab(
        id = R.drawable.brand_gitlab,
        regex = lazy { "(?i).*GitLab.*".toRegex() }
    ),
    Google(
        id = R.drawable.brand_google,
        regex = lazy { "(?i)Google(.com)?".toRegex() }
    ),
    GoogleCloud(
        id = R.drawable.brand_googlecloud,
        regex = lazy { "(?i)cloud.Google.com|Google\\s*Cloud".toRegex() }
    ),
    Instagram(
        id = R.drawable.brand_instagram,
        regex = lazy { "(?i)Instagram(.com)?".toRegex() }
    ),
    JetBrains(
        id = R.drawable.brand_jetbrains,
        regex = lazy { "(?i)JetBrains(.com)?".toRegex() }
    ),
    Jotform(
        id = R.drawable.brand_jotform,
        regex = lazy { "(?i)Jotform(.com)?".toRegex() }
    ),
    Lark(
        id = R.drawable.brand_lark,
        regex = lazy { "(?i)Lark(suite.com)?|Feishu(.cn)?".toRegex() }
    ),
    Mega(
        id = R.drawable.brand_mega,
        regex = lazy { "(?i)Mega(.io)?".toRegex() }
    ),
    LinkedIn(
        id = R.drawable.brand_linkedin,
        regex = lazy { "(?i)LinkedIn(.com)?".toRegex() }
    ),
    Meta(
        id = R.drawable.brand_meta,
        regex = lazy { "(?i)Meta(.com)?".toRegex() }
    ),
    Microsoft(
        id = R.drawable.brand_microsoft,
        regex = lazy { "(?i)Microsoft(.com)?".toRegex() }
    ),
    Netflix(
        id = R.drawable.brand_netflix,
        regex = lazy { "(?i)Netflix(.com)?".toRegex() }
    ),
    NVIDIA(
        id = R.drawable.brand_nvidia,
        regex = lazy { "(?i)NVIDIA(.com)?".toRegex() }
    ),
    OKX(
        id = R.drawable.brand_okx,
        regex = lazy { "(?i)OKX(.com)?".toRegex() }
    ),
    OneSignal(
        id = R.drawable.brand_onesignal,
        regex = lazy { "(?i)OneSignal(.com)?".toRegex() }
    ),
    OnlyFans(
        id = R.drawable.brand_onlyfans,
        regex = lazy { "(?i)OnlyFans(.com)?".toRegex() }
    ),
    OpenAI(
        id = R.drawable.brand_openai,
        regex = lazy { "(?i)(ChatGPT|OpenAI)(.com)?".toRegex() }
    ),
    Oracle(
        id = R.drawable.brand_oracle,
        regex = lazy { "(?i)Oracle(.com)?".toRegex() }
    ),
    ORCID(
        id = R.drawable.brand_orcid,
        regex = lazy { "(?i)ORCID(.org)?".toRegex() }
    ),
    Patreon(
        id = R.drawable.brand_patreon,
        regex = lazy { "(?i)Patreon(.com)?".toRegex() }
    ),
    PayPal(
        id = R.drawable.brand_paypal,
        regex = lazy { "(?i)PayPal(.com)?".toRegex() }
    ),
    Pinterest(
        id = R.drawable.brand_pinterest,
        regex = lazy { "(?i)Pinterest(.com)?".toRegex() }
    ),
    Pixiv(
        id = R.drawable.brand_pixiv,
        regex = lazy { "(?i)Pixiv(.net)?".toRegex() }
    ),
    Spotify(
        id = R.drawable.brand_spotify,
        regex = lazy { "(?i)Spotify(.com)?".toRegex() }
    ),
    Stripe(
        id = R.drawable.brand_stripe,
        regex = lazy { "(?i)Stripe(.com)?".toRegex() }
    ),
    Tencent(
        id = R.drawable.brand_tencent,
        regex = lazy { "(?i)Tencent(.com)?".toRegex() }
    ),
    TencentCloud(
        id = R.drawable.brand_tencentcloud,
        regex = lazy { "(?i)cloud.Tencent.com|Tencent\\s*Cloud".toRegex() }
    ),
    Threads(
        id = R.drawable.brand_threads,
        regex = lazy { "(?i)Threads(.com)?".toRegex() }
    ),
    VK(
        id = R.drawable.brand_vk,
        regex = lazy { "(?i)Vk(.com)?".toRegex() }
    ),
    WhatsApp(
        id = R.drawable.brand_whatsapp,
        regex = lazy { "(?i)WhatsApp(.com)?".toRegex() }
    ),
    Wise(
        id = R.drawable.brand_wise,
        regex = lazy { "(?i)Wise(.com)?".toRegex() }
    ),
    X(
        id = R.drawable.brand_x,
        regex = lazy { "(?i)X(.com)?".toRegex() }
    );

    companion object Default {
        fun matches(name: String) = entries.firstOrNull {
            it.regex.value.matches(name)
        }

        fun valueOfOrNull(name: String) = try {
            valueOf(name)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}