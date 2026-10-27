package com.mycompany.pluginsystem.plugins.caesar;

import com.mycompany.pluginsystem.api.SzovegPlugin;

/** Caesar-kódolás három karakteres eltolással az angol ábécé betűin. */
public final class CaesarPlugin implements SzovegPlugin {

    private static final int ELTOLAS = 3;

    @Override
    public String nev() {
        return "caesar";
    }

    @Override
    public String leiras() {
        return "Három hellyel eltolja az angol ábécé betűit (Caesar-kódolás).";
    }

    @Override
    public String feldolgoz(String bemenet) {
        StringBuilder eredmeny = new StringBuilder(bemenet.length());
        for (int i = 0; i < bemenet.length(); i++) {
            char karakter = bemenet.charAt(i);
            if (karakter >= 'a' && karakter <= 'z') {
                karakter = (char) ('a' + (karakter - 'a' + ELTOLAS) % 26);
            } else if (karakter >= 'A' && karakter <= 'Z') {
                karakter = (char) ('A' + (karakter - 'A' + ELTOLAS) % 26);
            }
            eredmeny.append(karakter);
        }
        return eredmeny.toString();
    }
}
