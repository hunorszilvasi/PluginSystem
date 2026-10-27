package com.mycompany.pluginsystem.core;

import java.util.List;

/** A pipeline végső szövege és a futás során kihagyott pluginok hibái. */
public record PipelineEredmeny(String szoveg, List<FeldolgozasiHiba> hibak) {

    public PipelineEredmeny {
        hibak = List.copyOf(hibak);
    }
}
