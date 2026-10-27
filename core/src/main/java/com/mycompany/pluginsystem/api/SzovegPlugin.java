package com.mycompany.pluginsystem.api;

/**
 * Szerződés a futásidőben felfedezhető szövegfeldolgozó pluginokhoz.
 * A pluginok a core modultól függetlenül valósítják meg ezt az interfészt.
 */
public interface SzovegPlugin {

    /** A plugin egyedi, parancssorból is használható neve. */
    String nev();

    /** Rövid, felhasználónak szóló leírás. */
    String leiras();

    /** Feldolgozza a bemenetet, és visszaadja az eredményt. */
    String feldolgoz(String bemenet);

    /** Megadja, hogy a plugin el tudja-e fogadni a bemenetet. */
    default boolean tamogatja(String bemenet) {
        return bemenet != null;
    }
}
