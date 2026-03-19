package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SkinManager {

    private final AdvancedNPCS plugin;
    private final Map<String, String> skinCache;

    public SkinManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.skinCache = new HashMap<>();
        plugin.getLogger().info("SkinManager iniciado. Skins manejadas por FancyNPCs.");
    }

    public void getSkinProfile(String playerName, Consumer<String> callback) {
        callback.accept(playerName);
    }

    public void getSkinProperty(String playerName, Consumer<Object> callback) {
        callback.accept(null);
    }

    public void clearCache() {
        skinCache.clear();
    }
}
