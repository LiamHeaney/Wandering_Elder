package com.example.wanderingelder.SettingScreen

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskTransform(): VisualTransformation
{
    override fun filter(text: AnnotatedString): TransformedText {
        return maskFilter(text)
    }

    private fun maskFilter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i==2||i==5) out += "-"
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
//                Log.e("Offset", ""+offset)
                if (offset <=2) return offset
                if (offset <= 5) return offset+1
                if (offset<= 10) return offset+2

                return 12

            }

            override fun transformedToOriginal(offset: Int): Int {
//                Log.e("Offset", ""+offset)
                if (offset <=4) return offset
                if (offset <=8) return offset-1
                return 9
            }
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }
}