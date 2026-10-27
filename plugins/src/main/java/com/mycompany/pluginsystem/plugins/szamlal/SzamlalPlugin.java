package com.mycompany.pluginsystem.plugins.szamlal;

import com.mycompany.pluginsystem.api.SzovegPlugin;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Unicode betűkből és számjegyekből álló szavakat számol. */
public final class SzamlalPlugin implements SzovegPlugin {

    private static final Pattern SZO = Pattern.compile("[\\p{L}\\p{M}\\p{N}]+",
            Pattern.UNICODE_CHARACTER_CLASS);

    @Override
    public String nev() {
        return "szamlal";
    }

    @Override
    public String leiras() {
        return "Megszámolja a bemenet szavait.";
    }

    @Override
    public String feldolgoz(String bemenet) {
        Matcher matcher = SZO.matcher(bemenet);
        int darab = 0;
        while (matcher.find()) {
            darab++;
        }
        return Integer.toString(darab);
    }
}
