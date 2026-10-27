package com.mycompany.pluginsystem.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** A szövegfeldolgozó alkalmazás parancssori belépési pontja. */
public final class PluginSystem {

    private PluginSystem() {
    }

    public static void main(String[] args) {
        int kilepesiKod = Parancssor.futtat(args, System.in, System.out, System.err);
        if (kilepesiKod != 0) {
            System.exit(kilepesiKod);
        }
    }
}
