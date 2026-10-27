package com.mycompany.pluginsystem.cli;

import com.mycompany.pluginsystem.api.SzovegPlugin;
import com.mycompany.pluginsystem.core.FeldolgozasiHiba;
import com.mycompany.pluginsystem.core.Pipeline;
import com.mycompany.pluginsystem.core.PipelineEredmeny;
import com.mycompany.pluginsystem.core.PluginTar;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Argumentumfeldolgozás és fájl/stdin/stdout kezelés. */
public final class Parancssor {

    private static final String HASZNALAT = "Használat: java ... --plugin név[,név...] [--input fájl] [--output fájl]";

    private Parancssor() {
    }

    /**
     * Futtatja a CLI-t. A stream paraméterekkel a bemenet és kimenet külön irányítható,
     * ezért az alkalmazás tesztelhető és nem zárja le a standard streameket.
     */
    public static int futtat(String[] args, InputStream standardBemenet,
            OutputStream standardKimenet, PrintStream hibakimenet) {
        Beallitasok beallitasok;
        try {
            beallitasok = parse(args);
        } catch (IllegalArgumentException e) {
            hibakimenet.println("Hiba: " + e.getMessage());
            hibakimenet.println(HASZNALAT);
            return 2;
        }

        if (beallitasok.sugo()) {
            hibakimenet.println(HASZNALAT);
            hibakimenet.println("A --input és --output elhagyásakor stdin-ről olvas, illetve stdout-ra ír.");
            return 0;
        }

        PluginTar tar;
        try {
            tar = PluginTar.felfedez();
        } catch (RuntimeException e) {
            hibakimenet.println("Hiba: a pluginok felfedezése nem sikerült: " + uzenet(e));
            return 1;
        }

        List<SzovegPlugin> kivalasztott = new ArrayList<>();
        for (String nev : beallitasok.pluginNevek()) {
            SzovegPlugin plugin = tar.pluginok().get(nev);
            if (plugin == null) {
                hibakimenet.println("Hiba: ismeretlen plugin: " + nev);
                hibakimenet.println("Elérhető pluginok: " + String.join(", ", tar.pluginok().keySet()));
                return 2;
            }
            kivalasztott.add(plugin);
        }

        try {
            String bemenet = beallitasok.bemenetiFajl() == null
                    ? new String(standardBemenet.readAllBytes(), StandardCharsets.UTF_8)
                    : Files.readString(beallitasok.bemenetiFajl(), StandardCharsets.UTF_8);

            PipelineEredmeny eredmeny = new Pipeline(kivalasztott).feldolgoz(bemenet);
            for (FeldolgozasiHiba hiba : eredmeny.hibak()) {
                hibakimenet.println("Figyelmeztetés: a(z) " + hiba.pluginNev()
                        + " plugin kimaradt: " + hiba.uzenet());
            }

            if (beallitasok.kimenetiFajl() == null) {
                standardKimenet.write(eredmeny.szoveg().getBytes(StandardCharsets.UTF_8));
                standardKimenet.flush();
            } else {
                Files.writeString(beallitasok.kimenetiFajl(), eredmeny.szoveg(), StandardCharsets.UTF_8);
            }
            return 0;
        } catch (IOException e) {
            hibakimenet.println("Hiba a bemenet vagy kimenet kezelésekor: " + uzenet(e));
            return 1;
        }
    }

    private static Beallitasok parse(String[] args) {
        String pluginLista = null;
        Path bemenetiFajl = null;
        Path kimenetiFajl = null;
        boolean sugo = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help", "-h" -> sugo = true;
                case "--plugin" -> {
                    if (pluginLista != null) {
                        throw new IllegalArgumentException("A --plugin csak egyszer adható meg.");
                    }
                    pluginLista = kovetkezoErtek(args, ++i, "--plugin");
                }
                case "--input" -> {
                    if (bemenetiFajl != null) {
                        throw new IllegalArgumentException("A --input csak egyszer adható meg.");
                    }
                    bemenetiFajl = Path.of(kovetkezoErtek(args, ++i, "--input"));
                }
                case "--output" -> {
                    if (kimenetiFajl != null) {
                        throw new IllegalArgumentException("A --output csak egyszer adható meg.");
                    }
                    kimenetiFajl = Path.of(kovetkezoErtek(args, ++i, "--output"));
                }
                default -> throw new IllegalArgumentException("Ismeretlen argumentum: " + args[i]);
            }
        }

        if (sugo) {
            return new Beallitasok(List.of(), null, null, true);
        }
        if (pluginLista == null || pluginLista.isBlank()) {
            throw new IllegalArgumentException("A --plugin megadása kötelező.");
        }

        List<String> pluginNevek = new ArrayList<>();
        for (String nev : pluginLista.split(",", -1)) {
            String tisztitottNev = nev.trim();
            if (tisztitottNev.isEmpty()) {
                throw new IllegalArgumentException("A pluginlista nem tartalmazhat üres nevet.");
            }
            pluginNevek.add(tisztitottNev);
        }
        return new Beallitasok(List.copyOf(pluginNevek), bemenetiFajl, kimenetiFajl, false);
    }

    private static String kovetkezoErtek(String[] args, int index, String kapcsolo) {
        if (index >= args.length || args[index].startsWith("--")) {
            throw new IllegalArgumentException("Hiányzó érték a(z) " + kapcsolo + " kapcsoló után.");
        }
        return args[index];
    }

    private static String uzenet(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    private record Beallitasok(List<String> pluginNevek, Path bemenetiFajl,
            Path kimenetiFajl, boolean sugo) {
    }
}
