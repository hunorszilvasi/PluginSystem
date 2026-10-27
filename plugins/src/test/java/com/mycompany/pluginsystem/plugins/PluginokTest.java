package com.mycompany.pluginsystem.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.pluginsystem.api.SzovegPlugin;
import com.mycompany.pluginsystem.core.Pipeline;
import com.mycompany.pluginsystem.core.PipelineEredmeny;
import com.mycompany.pluginsystem.core.PluginTar;
import com.mycompany.pluginsystem.plugins.caesar.CaesarPlugin;
import com.mycompany.pluginsystem.plugins.ekezettelen.EkezettelenPlugin;
import com.mycompany.pluginsystem.plugins.nagybetu.NagybetuPlugin;
import com.mycompany.pluginsystem.plugins.palindrom.PalindromPlugin;
import com.mycompany.pluginsystem.plugins.szamlal.SzamlalPlugin;
import java.util.List;
import org.junit.jupiter.api.Test;

class PluginokTest {

    @Test
    void szamlalUnicodeSzavakatSzamol() {
        assertEquals("3", new SzamlalPlugin().feldolgoz("Árvíztűrő tükörfúrógép 123!"));
    }

    @Test
    void nagybetuNagybetusseAlakit() {
        assertEquals("ÁRVÍZ TŰRŐ", new NagybetuPlugin().feldolgoz("árvíz tűrő"));
    }

    @Test
    void ekezeteltavolitasMegorziAzEkezetNelkuliSzoveget() {
        assertEquals("Arvizturo or", new EkezettelenPlugin().feldolgoz("Árvíztűrő őr"));
    }

    @Test
    void palindromMegtalaljaAzEkezetesSzot() {
        assertEquals("görög", new PalindromPlugin().feldolgoz("Indul a görög, aludni!"));
    }

    @Test
    void caesarHarommalEltoljaAzAngolBetuket() {
        assertEquals("Def abc! Á", new CaesarPlugin().feldolgoz("Abc xyz! Á"));
    }

    @Test
    void pipelineAmegadottSorrendbenFuttat() {
        PipelineEredmeny eredmeny = new Pipeline(
                List.of(new CaesarPlugin(), new EkezettelenPlugin())).feldolgoz("ábc");

        assertEquals("aef", eredmeny.szoveg());
        assertEquals("def", new Pipeline(
                List.of(new EkezettelenPlugin(), new CaesarPlugin()))
                .feldolgoz("ábc").szoveg());
        assertTrue(eredmeny.hibak().isEmpty());
    }

    @Test
    void hibasPluginKimaradEsATobbiTovabbFut() {
        SzovegPlugin hibas = new SzovegPlugin() {
            @Override
            public String nev() {
                return "hibas-teszt";
            }

            @Override
            public String leiras() {
                return "Szándékosan hibázó tesztplugin.";
            }

            @Override
            public String feldolgoz(String bemenet) {
                throw new IllegalStateException("teszthiba");
            }
        };

        PipelineEredmeny eredmeny = new Pipeline(
                List.of(hibas, new NagybetuPlugin())).feldolgoz("szöveg");

        assertEquals("SZÖVEG", eredmeny.szoveg());
        assertEquals(1, eredmeny.hibak().size());
        assertEquals("hibas-teszt", eredmeny.hibak().getFirst().pluginNev());
    }

    @Test
    void serviceLoaderMindAzOtPlugintFelfedezi() {
        assertEquals(5, PluginTar.felfedez().pluginok().size());
        assertTrue(PluginTar.felfedez().pluginok().containsKey("caesar"));
    }
}
