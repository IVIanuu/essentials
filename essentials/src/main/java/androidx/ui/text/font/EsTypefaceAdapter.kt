package androidx.ui.text.font

import android.graphics.Typeface
import android.os.Build
import androidx.collection.LruCache

/**
 * Creates a Typeface based on generic font family or a custom [FontFamily].
 *
 * @param fontMatcher [EsFontMatcher] class to be used to match given [FontWeight] and [FontStyle]
 *                    constraints to select a [Font] from a [FontFamily]
 *
 * @param resourceLoader [Font.ResourceLoader] for Android.
 */
// TODO(Migration/siyamed): font matcher should be at an upper layer such as Paragraph, whoever
// will call EsTypefaceAdapter can know about a single font
internal open class EsTypefaceAdapter(
    val fontMatcher: EsFontMatcher = EsFontMatcher(),
    val resourceLoader: Font.ResourceLoader
) {

    data class CacheKey(
        val fontFamily: FontFamily? = null,
        val fontWeight: FontWeight,
        val fontStyle: FontStyle,
        val fontSynthesis: FontSynthesis
    )

    companion object {
        // Accept FontWeights at and above 600 to be bold. 600 comes from FontFamily.cpp#computeFakery
        // function in minikin
        private val ANDROID_BOLD = FontWeight.w600

        // 16 is a random number and is not based on any strong logic
        val typefaceCache = LruCache<CacheKey, Typeface>(16)
    }

    /**
     * Creates a Typeface based on the [fontFamily] and the selection constraints [fontStyle] and
     * [fontWeight].
     *
     * @param fontFamily [FontFamily] that defines the system family or a set of custom fonts
     * @param fontWeight the font weight to create the typeface in
     * @param fontStyle the font style to create the typeface in
     */
    open fun create(
        fontFamily: FontFamily? = null,
        fontWeight: FontWeight = FontWeight.normal,
        fontStyle: FontStyle = FontStyle.Normal,
        fontSynthesis: FontSynthesis = FontSynthesis.All
    ): Typeface {
        val cacheKey = CacheKey(
            fontFamily,
            fontWeight,
            fontStyle,
            fontSynthesis
        )
        val cachedTypeface = typefaceCache.get(cacheKey)
        if (cachedTypeface != null) return cachedTypeface

        val typeface = if (fontFamily != null && fontFamily.isNotEmpty()) {
            create(
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                fontSynthesis = fontSynthesis
            )
        } else {
            // there is no option to control fontSynthesis in framework for system fonts
            create(
                genericFontFamily = fontFamily?.genericFamily,
                fontWeight = fontWeight,
                fontStyle = fontStyle
            )
        }

        // For system Typeface, on different framework versions Typeface might not be cached,
        // therefore it is safer to cache this result on our code and the cost is minimal.
        typefaceCache.put(cacheKey, typeface)

        return typeface
    }

    /**
     * Creates a Typeface object based on the system installed fonts. [genericFontFamily] is used
     * to define the main family to create the Typeface such as serif, sans-serif.
     *
     * [fontWeight] is used to define the thickness of the Typeface. Before Android 28 font weight
     * cannot be defined therefore this function assumes anything at and above [FontWeight.w600]
     * is bold and any value less than [FontWeight.w600] is normal.
     *
     * @param genericFontFamily generic font family name such as serif, sans-serif
     * @param fontWeight the font weight to create the typeface in
     * @param fontStyle the font style to create the typeface in
     */
    private fun create(
        genericFontFamily: String? = null,
        fontWeight: FontWeight = FontWeight.normal,
        fontStyle: FontStyle = FontStyle.Normal
    ): Typeface {
        if (fontStyle == FontStyle.Normal &&
            fontWeight == FontWeight.normal &&
            genericFontFamily.isNullOrEmpty()
        ) {
            return Typeface.DEFAULT
        }

        // TODO(Migration/siyamed): ideally we should not have platform dependent if's here.
        // will think more and move to ui-text later.
        val result = if (Build.VERSION.SDK_INT < 28) {
            val targetStyle = getTypefaceStyle(fontWeight, fontStyle)
            if (genericFontFamily.isNullOrEmpty()) {
                Typeface.defaultFromStyle(targetStyle)
            } else {
                Typeface.create(genericFontFamily, targetStyle)
            }
        } else {
            val familyTypeface: Typeface
            if (genericFontFamily == null) {
                familyTypeface = Typeface.DEFAULT
            } else {
                familyTypeface = Typeface.create(genericFontFamily, Typeface.NORMAL)
            }

            Typeface.create(
                familyTypeface,
                FontAccessor.getFontWeight(fontWeight),
                fontStyle == FontStyle.Italic
            )
        }

        return result
    }

    /**
     * Creates a [Typeface] based on the [fontFamily] the requested [FontWeight], [FontStyle]. If
     * the requested [FontWeight] and [FontStyle] exists in the [FontFamily], the exact match is
     * returned. If it does not, the matching is defined based on CSS Font Matching. See
     * [EsFontMatcher] for more information.
     *
     * @param fontStyle the font style to create the typeface in
     * @param fontWeight the font weight to create the typeface in
     * @param fontFamily [FontFamily] that contains the list of [Font]s
     * @param fontSynthesis [FontSynthesis] which attributes of the font family to synthesize
     *        custom fonts for if they are not already present in the font family
     */
    private fun create(
        fontStyle: FontStyle = FontStyle.Normal,
        fontWeight: FontWeight = FontWeight.normal,
        fontFamily: FontFamily,
        fontSynthesis: FontSynthesis = FontSynthesis.All
    ): Typeface {
        // TODO(siyamed): add genericFontFamily : String? = null for fallback
        // TODO(siyamed): add support for multiple font families

        val font = fontMatcher.matchFont(fontFamily, fontWeight, fontStyle)

        val typeface = try {
            resourceLoader.load(font) as Typeface
        } catch (e: Exception) {
            throw IllegalStateException("Cannot create Typeface from $font")
        }

        val loadedFontIsSameAsRequest = fontWeight == font.weight && fontStyle == font.style
        // if synthesis is not requested or there is an exact match we don't need synthesis
        if (fontSynthesis == FontSynthesis.None || loadedFontIsSameAsRequest) {
            return typeface
        }

        return synthesize(typeface, font, fontWeight, fontStyle, fontSynthesis)
    }

    fun synthesize(
        typeface: Typeface,
        font: Font,
        fontWeight: FontWeight,
        fontStyle: FontStyle,
        fontSynthesis: FontSynthesis
    ): Typeface {

        val synthesizeWeight = FontAccessor.isWeightOn(fontSynthesis) &&
                (fontWeight >= ANDROID_BOLD && font.weight < ANDROID_BOLD)

        val synthesizeStyle = FontAccessor.isStyleOn(fontSynthesis) && fontStyle != font.style

        if (!synthesizeStyle && !synthesizeWeight) return typeface

        return if (Build.VERSION.SDK_INT < 28) {
            val targetStyle = getTypefaceStyle(
                isBold = synthesizeWeight,
                isItalic = synthesizeStyle && fontStyle == FontStyle.Italic
            )
            Typeface.create(typeface, targetStyle)
        } else {
            val finalFontWeight = if (synthesizeWeight) {
                // if we want to synthesize weight, we send the requested fontWeight
                FontAccessor.getFontWeight(fontWeight)
            } else {
                // if we do not want to synthesize weight, we keep the loaded font weight
                FontAccessor.getFontWeight(font.weight)
            }

            val finalFontStyle = if (synthesizeStyle) {
                // if we want to synthesize style, we send the requested fontStyle
                fontStyle == FontStyle.Italic
            } else {
                // if we do not want to synthesize style, we keep the loaded font style
                font.style == FontStyle.Italic
            }

            Typeface.create(typeface, finalFontWeight, finalFontStyle)
        }
    }

    /**
     * Convert given [FontWeight] and [FontStyle] to one of [Typeface.NORMAL], [Typeface.BOLD],
     * [Typeface.ITALIC], [Typeface.BOLD_ITALIC]. This function should be called for API < 28
     * since at those API levels system does not accept [FontWeight].
     */
    fun getTypefaceStyle(fontWeight: FontWeight, fontStyle: FontStyle): Int {
        return getTypefaceStyle(fontWeight >= ANDROID_BOLD, fontStyle == FontStyle.Italic)
    }

    fun getTypefaceStyle(isBold: Boolean, isItalic: Boolean): Int {
        return if (isItalic && isBold) {
            Typeface.BOLD_ITALIC
        } else if (isBold) {
            Typeface.BOLD
        } else if (isItalic) {
            Typeface.ITALIC
        } else {
            Typeface.NORMAL
        }
    }
}