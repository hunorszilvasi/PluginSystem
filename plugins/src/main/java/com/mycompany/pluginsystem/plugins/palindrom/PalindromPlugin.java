package com.mycompany.pluginsystem.plugins.palindrom;

import com.mycompany.pluginsystem.api.SzovegPlugin;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Megkeresi a bemenetben szereplő, legalább két betűből álló palindrom szavakat. */
public final class PalindromPlugin implements SzovegPlugin {

    private static final Pattern SZO = Pattern.compile("[\\p{L}\\p{M}]+",
            Pattern.UNICODE_CHARACTER_CLASS);

    @Override
    public String nev() {
        return "palindrom";
    }

    @Override
    public String leiras() {
        return "Kikeresi a palindrom szavakat (kis- és nagybetűtől, ékezettől függetlenül).";
    }

    @Override
    public String feldolgoz(String bemenet) {
        Matcher matcher = SZO.matcher(bemenet);
        List<String> talalatok = new ArrayList<>();
        while (matcher.find()) {
            String szo = matcher.group();
            if (palindrom(szo)) {
                talalatok.add(szo);
            }
        }
        return String.join(" ", talalatok);
    }

    private boolean palindrom(String szo) {
        String normalizalt = Normalizer.normalize(szo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
        int[] kodpontok = normalizalt.codePoints().toArray();
        if (kodpontok.length < 2) {
            return false;
        }
        for (int bal = 0, jobb = kodpontok.length - 1; bal < jobb; bal++, jobb--) {
            if (kodpontok[bal] != kodpontok[jobb]) {
                return false;
            }
        }
        return true;
    }
}
