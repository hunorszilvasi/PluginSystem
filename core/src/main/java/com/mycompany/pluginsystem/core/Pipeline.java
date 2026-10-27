package com.mycompany.pluginsystem.core;

import com.mycompany.pluginsystem.api.SzovegPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** A kapott pluginokat a megadott sorrendben futtatja. */
public final class Pipeline {

    private static final Logger LOGGER = Logger.getLogger(Pipeline.class.getName());

    private final List<SzovegPlugin> pluginok;

    public Pipeline(List<? extends SzovegPlugin> pluginok) {
        this.pluginok = List.copyOf(pluginok);
    }

    /**
     * Minden plugin az előző sikeres plugin eredményét kapja meg.
     * Hiba esetén az aktuális szöveg megmarad, a plugin kimarad, és fut a lánc tovább.
     */
    public PipelineEredmeny feldolgoz(String bemenet) {
        String aktualisSzoveg = bemenet;
        List<FeldolgozasiHiba> hibak = new ArrayList<>();

        for (SzovegPlugin plugin : pluginok) {
            String pluginNev = plugin.getClass().getSimpleName();
            try {
                pluginNev = plugin.nev();
                if (!plugin.tamogatja(aktualisSzoveg)) {
                    continue;
                }
                aktualisSzoveg = Objects.requireNonNull(
                        plugin.feldolgoz(aktualisSzoveg),
                        "A plugin null eredményt adott");
            } catch (Exception e) {
                String uzenet = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                hibak.add(new FeldolgozasiHiba(pluginNev, uzenet));
                LOGGER.log(Level.WARNING,
                        "A(z) " + pluginNev + " plugin hibát dobott; kihagyom.", e);
            }
        }

        return new PipelineEredmeny(aktualisSzoveg, hibak);
    }
}
