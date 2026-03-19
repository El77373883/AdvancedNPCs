package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class SkinManager {

    private final AdvancedNPCS plugin;
    private final Map<String, SkinProperty> skinCache;
    private boolean skinRestorerAvailable;

    public SkinManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.skinCache = new HashMap<>();
        this.skinRestorerAvailable = Bukkit.getPluginManager()
            .getPlugin("SkinsRestorer") != null;
        if (skinRestorerAvailable) {
            plugin.getLogger().info("SkinRestorer detectado. Usando para skins de NPCs.");
        } else {
            plugin.getLogger().info("SkinRestorer no detectado. Usando skin default.");
        }
    }

    public void getSkinProperty(String playerName, Consumer<SkinProperty> callback) {
        if (skinCache.containsKey(playerName.toLowerCase())) {
            callback.accept(skinCache.get(playerName.toLowerCase()));
            return;
        }
        if (!skinRestorerAvailable) {
            callback.accept(null);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Optional<SkinProperty> property = SkinsRestorerProvider.get()
                    .getSkinStorage()
                    .getSkinOfPlayer(playerName);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    SkinProperty prop = property.orElse(null);
                    if (prop != null) {
                        skinCache.put(playerName.toLowerCase(), prop);
                    }
                    callback.accept(prop);
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Error obteniendo skin de " +
                    playerName + ": " + e.getMessage());
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(null));
            }
        });
    }

    public void clearCache() {
        skinCache.clear();
    }

    public boolean isSkinRestorerAvailable() {
        return skinRestorerAvailable;
    }
}
