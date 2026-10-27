package com.mycompany.pluginsystem.plugins.nagybetu;

import com.mycompany.pluginsystem.api.SzovegPlugin;
import java.util.Locale;

/** A teljes bemenetet nagybetűssé alakítja. */
public final class NagybetuPlugin implements SzovegPlugin {

    @Override
    public String nev() {
        return "nagybetu";
    }

    @Override
    public String leiras() {
        return "Nagybetűssé alakítja a szöveget.";
    }

    @Override
    public String feldolgoz(String bemenet) {
        return bemenet.toUpperCase(Locale.ROOT);
    }
}
