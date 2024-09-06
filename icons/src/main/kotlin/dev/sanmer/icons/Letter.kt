package dev.sanmer.icons

object Letter {
    private val unknown = R.drawable.alpha
    private val letters = mapOf(
        'A' to R.drawable.letter_a,
        'B' to R.drawable.letter_b,
        'C' to R.drawable.letter_c,
        'D' to R.drawable.letter_d,
        'E' to R.drawable.letter_e,
        'F' to R.drawable.letter_f,
        'G' to R.drawable.letter_g,
        'H' to R.drawable.letter_h,
        'I' to R.drawable.letter_i,
        'J' to R.drawable.letter_j,
        'K' to R.drawable.letter_k,
        'L' to R.drawable.letter_l,
        'M' to R.drawable.letter_m,
        'N' to R.drawable.letter_n,
        'O' to R.drawable.letter_o,
        'P' to R.drawable.letter_p,
        'Q' to R.drawable.letter_q,
        'R' to R.drawable.letter_r,
        'S' to R.drawable.letter_s,
        'T' to R.drawable.letter_t,
        'U' to R.drawable.letter_u,
        'V' to R.drawable.letter_v,
        'W' to R.drawable.letter_w,
        'X' to R.drawable.letter_x,
        'Y' to R.drawable.letter_y,
        'Z' to R.drawable.letter_z
    )

    operator fun invoke(id: Char) =
        letters.getOrElse(id.uppercaseChar()) { unknown }

    fun get(value: String) = invoke(value.firstOrNull() ?: '!')
}