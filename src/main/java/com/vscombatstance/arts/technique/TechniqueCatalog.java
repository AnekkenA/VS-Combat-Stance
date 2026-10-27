package com.vscombatstance.arts.technique;

import java.util.*;

public class TechniqueCatalog {
    public static Technique find(String id) {
        if (id == null) return null;
        Technique art = TechniqueRegistry.get(id);
        if (art != null) return art;
        return LinkerRegistry.get(id);
    }

    public static List<Technique> getAll() {
        List<Technique> all = new ArrayList<>();
        all.addAll(TechniqueRegistry.getAll());
        all.addAll(LinkerRegistry.getAll());
        return all;
    }
}
