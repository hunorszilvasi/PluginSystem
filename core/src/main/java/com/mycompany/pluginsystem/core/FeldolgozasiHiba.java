package com.mycompany.pluginsystem.core;

/** Egy plugin futtatása közben rögzített hiba. */
public record FeldolgozasiHiba(String pluginNev, String uzenet) {
}
