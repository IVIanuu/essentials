package androidx.ui.text.font

internal open class EsFontMatcher {

    /**
     * Given a [FontFamily], [FontWeight] and [FontStyle], matches the best font in the
     * [FontFamily] that satisfies the requirements of [FontWeight] and [FontStyle]. If there is
     * not a font that exactly satisfies the given constraints of [FontWeight] and [FontStyle], the
     * best match will be returned. The rules for the best match are defined in
     * [CSS 4 Font Matching](https://www.w3.org/TR/css-fonts-4/#font-style-matching).
     *
     * @param fontFamily FontFamily to choose the [Font] from
     * @param fontWeight desired [FontWeight]
     * @param fontStyle desired [FontStyle]
     */
    open fun matchFont(fontFamily: FontFamily, fontWeight: FontWeight, fontStyle: FontStyle): Font {
        // check for exact match first
        fontFamily.filter { it.weight == fontWeight && it.style == fontStyle }.let {
            // TODO(b/130797349): IR compiler bug was here
            if (it.isNotEmpty()) {
                return it[0]
            }
        }

        // if no exact match, filter with style
        val fonts = fontFamily.filter { it.style == fontStyle }.let {
            if (it.isNotEmpty()) it else fontFamily.fonts
        }

        val result = if (fontWeight < FontWeight.w400) {
            // If the desired weight is less than 400
            // - weights less than or equal to the desired weight are value in descending order
            // - followed by weights above the desired weight in ascending order
            fonts.filter { it.weight <= fontWeight }.maxBy { it.weight }
                ?: fonts.filter { it.weight > fontWeight }.minBy { it.weight }
        } else if (fontWeight > FontWeight.w500) {
            // If the desired weight is greater than 500
            // - weights greater than or equal to the desired weight are value in ascending order
            // - followed by weights below the desired weight in descending order
            fonts.filter { it.weight >= fontWeight }.minBy { it.weight }
                ?: fonts.filter { it.weight < fontWeight }.maxBy { it.weight }
        } else {
            // If the desired weight is inclusively between 400 and 500
            // - weights greater than or equal to the target weight are value in ascending order
            // until 500 is hit and value,
            // - followed by weights less than the target weight in descending order,
            // - followed by weights greater than 500
            fonts.filter { it.weight >= fontWeight && it.weight <= FontWeight.w500 }
                .minBy { it.weight }
                ?: fonts.filter { it.weight < fontWeight }.maxBy { it.weight }
                ?: fonts.filter { it.weight > FontWeight.w500 }.minBy { it.weight }
        }

        return result ?: throw IllegalStateException("Cannot match any font")
    }
}