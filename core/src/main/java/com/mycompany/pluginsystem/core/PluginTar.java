package com.mycompany.pluginsystem.core;

import com.mycompany.pluginsystem.api.SzovegPlugin;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

/** A classpathon regisztrált {@link SzovegPlugin} implementációk tárháza. */
public final class PluginTar {

    private final Map<String, SzovegPlugin> pluginok;

    private PluginTar(Map<String, SzovegPlugin> pluginok) {
        this.pluginok = Collections.unmodifiableMap(new LinkedHashMap<>(pluginok));
    }

    /**
     * Felfedezi a futtatókörnyezetben regisztrált pluginokat.
     * A konkrét pluginosztályokat a core nem ismeri és nem példányosítja közvetlenül.
     */
    public static PluginTar felfedez() {
        Map<String, SzovegPlugin> talalatok = new LinkedHashMap<>();
        for (SzovegPlugin plugin : ServiceLoader.load(SzovegPlugin.class)) {
            SzovegPlugin korabbi = talalatok.putIfAbsent(plugin.nev(), plugin);
            if (korabbi != null) {
                throw new IllegalStateException("Duplikált plugin név: " + plugin.nev());
            }
        }
        return new PluginTar(talalatok);
    }

    /** Visszaadja a felfedezett pluginokat név szerint, bejárási sorrendben. */
    public Map<String, SzovegPlugin> pluginok() {
        return pluginok;
    }
}
