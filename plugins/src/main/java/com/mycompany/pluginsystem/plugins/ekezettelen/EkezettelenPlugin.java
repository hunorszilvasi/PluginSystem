package com.mycompany.pluginsystem.plugins.ekezettelen;

import com.mycompany.pluginsystem.api.SzovegPlugin;
import java.text.Normalizer;

/** Eltávolítja az ékezetet adó Unicode kombináló jeleket a betűkről. */
public final class EkezettelenPlugin implements SzovegPlugin {

    @Override
    public String nev() {
        return "ekezettelen";
    }

    @Override
    public String leiras() {
        return "Eltávolítja a szöveg ékezeteit.";
    }

    @Override
    public String feldolgoz(String bemenet) {
        String felbontott = Normalizer.normalize(bemenet, Normalizer.Form.NFD);
        return felbontott.replaceAll("\\p{M}+", "");
    }
}
