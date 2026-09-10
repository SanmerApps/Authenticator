package dev.sanmer.brand

import androidx.annotation.DrawableRes

@Suppress("SpellCheckingInspection")
enum class Brand(
    @DrawableRes val id: Int,
    val label: String,
    val domains: List<String>,
    private val regex: Lazy<Regex>
) {
    Adobe(
        id = R.drawable.brand_adobe,
        label = "Adobe",
        domains = listOf("adobe.com"),
        regex = lazy { "(?i)Adobe(.com)?".toRegex() }
    ),
    Aliyun(
        id = R.drawable.brand_aliyun,
        label = "Alibaba Cloud",
        domains = listOf("aliyun.com"),
        regex = lazy { "(?i)Aliyun(.com)?|Alibaba\\s*Cloud".toRegex() }
    ),
    AMD(
        id = R.drawable.brand_amd,
        label = "AMD",
        domains = listOf("amd.com"),
        regex = lazy { "(?i)AMD(.com)?".toRegex() }
    ),
    Apple(
        id = R.drawable.brand_apple,
        label = "Apple",
        domains = listOf("apple.com"),
        regex = lazy { "(?i)Apple(.com)?".toRegex() }
    ),
    Atlassian(
        id = R.drawable.brand_atlassian,
        label = "Atlassian",
        domains = listOf("atlassian.com"),
        regex = lazy { "(?i)Atlassian(.com)?".toRegex() }
    ),
    AWS(
        id = R.drawable.brand_aws,
        label = "Amazon Web Services",
        domains = listOf("aws.amazon.com", "aws.com"),
        regex = lazy { "(?i)AWS(.Amazon.com|.com)?|Amazon\\s*Web\\s*Service".toRegex() }
    ),
    Azure(
        id = R.drawable.brand_azure,
        label = "Microsoft Azure",
        domains = listOf("azure.microsoft.com", "azure.com"),
        regex = lazy { "(?i)Azure(.Microsoft.com|.com)?|Microsoft\\s*Azure".toRegex() }
    ),
    Binance(
        id = R.drawable.brand_binance,
        label = "Binance",
        domains = listOf("binance.com"),
        regex = lazy { "(?i)Binance(.com)?".toRegex() }
    ),
    Bybit(
        id = R.drawable.brand_bybit,
        label = "Bybit",
        domains = listOf("bybit.com"),
        regex = lazy { "(?i)Bybit(.com)?".toRegex() }
    ),
    Canva(
        id = R.drawable.brand_canva,
        label = "Canva",
        domains = listOf("canva.com"),
        regex = lazy { "(?i)Canva(.com)?".toRegex() }
    ),
    Cloudflare(
        id = R.drawable.brand_cloudflare,
        label = "Cloudflare",
        domains = listOf("cloudflare.com"),
        regex = lazy { "(?i)Cloudflare(.com)?".toRegex() }
    ),
    Coinbase(
        id = R.drawable.brand_coinbase,
        label = "Coinbase",
        domains = listOf("coinbase.com"),
        regex = lazy { "(?i)Coinbase(.com)?".toRegex() }
    ),
    Crowdin(
        id = R.drawable.brand_crowdin,
        label = "Crowdin",
        domains = listOf("crowdin.com"),
        regex = lazy { "(?i)Crowdin(.com)?".toRegex() }
    ),
    DigitalOcean(
        id = R.drawable.brand_digitalocean,
        label = "Digital Ocean",
        domains = listOf("digitalocean.com"),
        regex = lazy { "(?i)DigitalOcean(.com)?".toRegex() }
    ),
    Discord(
        id = R.drawable.brand_discord,
        label = "Discord",
        domains = listOf("discord.com"),
        regex = lazy { "(?i)Discord(.com)?".toRegex() }
    ),
    Docker(
        id = R.drawable.brand_docker,
        label = "Docker",
        domains = listOf("docker.com"),
        regex = lazy { "(?i).*Docker.*".toRegex() }
    ),
    Dynatrace(
        id = R.drawable.brand_dynatrace,
        label = "Dynatrace",
        domains = listOf("dynatrace.com"),
        regex = lazy { "(?i)Dynatrace(.com)?".toRegex() }
    ),
    Facebook(
        id = R.drawable.brand_facebook,
        label = "Facebook",
        domains = listOf("facebook.com"),
        regex = lazy { "(?i)Facebook(.com)?".toRegex() }
    ),
    Fansly(
        id = R.drawable.brand_fansly,
        label = "Fansly",
        domains = listOf("fansly.com"),
        regex = lazy { "(?i)Fansly(.com)?".toRegex() }
    ),
    Figma(
        id = R.drawable.brand_figma,
        label = "Figma",
        domains = listOf("figma.com"),
        regex = lazy { "(?i)Figma(.com)?".toRegex() }
    ),
    Gitea(
        id = R.drawable.brand_gitea,
        label = "Gitea",
        domains = listOf("gitea.com"),
        regex = lazy { "(?i).*Gitea.*".toRegex() }
    ),
    GitHub(
        id = R.drawable.brand_github,
        label = "GitHub",
        domains = listOf("github.com"),
        regex = lazy { "(?i).*GitHub.*".toRegex() }
    ),
    GitLab(
        id = R.drawable.brand_gitlab,
        label = "GitLab",
        domains = listOf("gitlab.com"),
        regex = lazy { "(?i).*GitLab.*".toRegex() }
    ),
    Google(
        id = R.drawable.brand_google,
        label = "Google",
        domains = listOf("google.com"),
        regex = lazy { "(?i)Google(.com)?".toRegex() }
    ),
    GoogleCloud(
        id = R.drawable.brand_googlecloud,
        label = "Google Cloud",
        domains = listOf("cloud.google.com"),
        regex = lazy { "(?i)cloud.Google.com|Google\\s*Cloud".toRegex() }
    ),
    Instagram(
        id = R.drawable.brand_instagram,
        label = "Instagram",
        domains = listOf("instagram.com"),
        regex = lazy { "(?i)Instagram(.com)?".toRegex() }
    ),
    JetBrains(
        id = R.drawable.brand_jetbrains,
        label = "JetBrains",
        domains = listOf("jetbrains.com"),
        regex = lazy { "(?i)JetBrains(.com)?".toRegex() }
    ),
    Jotform(
        id = R.drawable.brand_jotform,
        label = "Jotform",
        domains = listOf("jotform.com"),
        regex = lazy { "(?i)Jotform(.com)?".toRegex() }
    ),
    Lark(
        id = R.drawable.brand_lark,
        label = "Lark",
        domains = listOf("larksuite.com", "feishu.cn"),
        regex = lazy { "(?i)Lark(suite.com)?|Feishu(.cn)?".toRegex() }
    ),
    Mega(
        id = R.drawable.brand_mega,
        label = "Mega",
        domains = listOf("mega.io"),
        regex = lazy { "(?i)Mega(.io)?".toRegex() }
    ),
    LinkedIn(
        id = R.drawable.brand_linkedin,
        label = "LinkedIn",
        domains = listOf("linkedin.com"),
        regex = lazy { "(?i)LinkedIn(.com)?".toRegex() }
    ),
    Meta(
        id = R.drawable.brand_meta,
        label = "Meta",
        domains = listOf("meta.com"),
        regex = lazy { "(?i)Meta(.com)?".toRegex() }
    ),
    Microsoft(
        id = R.drawable.brand_microsoft,
        label = "Microsoft",
        domains = listOf("microsoft.com"),
        regex = lazy { "(?i)Microsoft(.com)?".toRegex() }
    ),
    Netflix(
        id = R.drawable.brand_netflix,
        label = "Netflix",
        domains = listOf("netflix.com"),
        regex = lazy { "(?i)Netflix(.com)?".toRegex() }
    ),
    NVIDIA(
        id = R.drawable.brand_nvidia,
        label = "NVIDIA",
        domains = listOf("nvidia.com"),
        regex = lazy { "(?i)NVIDIA(.com)?".toRegex() }
    ),
    OKX(
        id = R.drawable.brand_okx,
        label = "OKX",
        domains = listOf("okx.com"),
        regex = lazy { "(?i)OKX(.com)?".toRegex() }
    ),
    OneSignal(
        id = R.drawable.brand_onesignal,
        label = "OneSignal",
        domains = listOf("onesignal.com"),
        regex = lazy { "(?i)OneSignal(.com)?".toRegex() }
    ),
    OnlyFans(
        id = R.drawable.brand_onlyfans,
        label = "OnlyFans",
        domains = listOf("onlyfans.com"),
        regex = lazy { "(?i)OnlyFans(.com)?".toRegex() }
    ),
    OpenAI(
        id = R.drawable.brand_openai,
        label = "OpenAI",
        domains = listOf("openai.com", "chatgpt.com"),
        regex = lazy { "(?i)(ChatGPT|OpenAI)(.com)?".toRegex() }
    ),
    Oracle(
        id = R.drawable.brand_oracle,
        label = "Oracle",
        domains = listOf("oracle.com"),
        regex = lazy { "(?i)Oracle(.com)?".toRegex() }
    ),
    ORCID(
        id = R.drawable.brand_orcid,
        label = "ORCID",
        domains = listOf("orcid.org"),
        regex = lazy { "(?i)ORCID(.org)?".toRegex() }
    ),
    Patreon(
        id = R.drawable.brand_patreon,
        label = "Patreon",
        domains = listOf("patreon.com"),
        regex = lazy { "(?i)Patreon(.com)?".toRegex() }
    ),
    PayPal(
        id = R.drawable.brand_paypal,
        label = "PayPal",
        domains = listOf("paypal.com"),
        regex = lazy { "(?i)PayPal(.com)?".toRegex() }
    ),
    Pinterest(
        id = R.drawable.brand_pinterest,
        label = "Pinterest",
        domains = listOf("pinterest.com"),
        regex = lazy { "(?i)Pinterest(.com)?".toRegex() }
    ),
    Pixiv(
        id = R.drawable.brand_pixiv,
        label = "Pixiv",
        domains = listOf("pixiv.net"),
        regex = lazy { "(?i)Pixiv(.net)?".toRegex() }
    ),
    Spotify(
        id = R.drawable.brand_spotify,
        label = "Spotify",
        domains = listOf("spotify.com"),
        regex = lazy { "(?i)Spotify(.com)?".toRegex() }
    ),
    Stripe(
        id = R.drawable.brand_stripe,
        label = "Stripe",
        domains = listOf("stripe.com"),
        regex = lazy { "(?i)Stripe(.com)?".toRegex() }
    ),
    Tencent(
        id = R.drawable.brand_tencent,
        label = "Tencent",
        domains = listOf("tencent.com"),
        regex = lazy { "(?i)Tencent(.com)?".toRegex() }
    ),
    TencentCloud(
        id = R.drawable.brand_tencentcloud,
        label = "Tencent Cloud",
        domains = listOf("cloud.tencent.com"),
        regex = lazy { "(?i)cloud.Tencent.com|Tencent\\s*Cloud".toRegex() }
    ),
    Threads(
        id = R.drawable.brand_threads,
        label = "Threads",
        domains = listOf("threads.com"),
        regex = lazy { "(?i)Threads(.com)?".toRegex() }
    ),
    VK(
        id = R.drawable.brand_vk,
        label = "VK",
        domains = listOf("vk.ru", "vk.com"),
        regex = lazy { "(?i)Vk(.ru|.com)?".toRegex() }
    ),
    WhatsApp(
        id = R.drawable.brand_whatsapp,
        label = "WhatsApp",
        domains = listOf("whatsapp.com"),
        regex = lazy { "(?i)WhatsApp(.com)?".toRegex() }
    ),
    Wise(
        id = R.drawable.brand_wise,
        label = "Wise",
        domains = listOf("wise.com"),
        regex = lazy { "(?i)Wise(.com)?".toRegex() }
    ),
    X(
        id = R.drawable.brand_x,
        label = "X",
        domains = listOf("x.com", "twitter.com"),
        regex = lazy { "(?i)(X|Twitter)(.com)?".toRegex() }
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