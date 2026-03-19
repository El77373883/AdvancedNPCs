package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Base64;

public class SkinManager {

    private final AdvancedNPCS plugin;
    private final Map<String, PlayerProfile> skinCache;

    public SkinManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.skinCache = new HashMap<>();
    }

    public void getSkinProfile(String playerName, Consumer<PlayerProfile> callback) {
        if (skinCache.containsKey(playerName.toLowerCase())) {
            callback.accept(skinCache.get(playerName.toLowerCase()));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection uuidConn = (HttpURLConnection) new URL(
                    "https://api.mojang.com/users/profiles/minecraft/" + playerName)
                    .openConnection();
                uuidConn.setConnectTimeout(5000);
                uuidConn.setReadTimeout(5000);
                if (uuidConn.getResponseCode() != 200) {
                    Bukkit.getScheduler().runTask(plugin, () -> callback.accept(getDefaultProfile()));
                    return;
                }
                JsonObject uuidJson = JsonParser.parseReader(
                    new InputStreamReader(uuidConn.getInputStream())).getAsJsonObject();
                String uuid = uuidJson.get("id").getAsString();
                String formattedUUID = uuid.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5");
                HttpURLConnection skinConn = (HttpURLConnection) new URL(
                    "https://sessionserver.mojang.com/session/minecraft/profile/" +
                    formattedUUID + "?unsigned=false").openConnection();
                skinConn.setConnectTimeout(5000);
                skinConn.setReadTimeout(5000);
                if (skinConn.getResponseCode() != 200) {
                    Bukkit.getScheduler().runTask(plugin, () -> callback.accept(getDefaultProfile()));
                    return;
                }
                JsonObject profileJson = JsonParser.parseReader(
                    new InputStreamReader(skinConn.getInputStream())).getAsJsonObject();
                String textureBase64 = profileJson.getAsJsonArray("properties")
                    .get(0).getAsJsonObject().get("value").getAsString();
                String decoded = new String(Base64.getDecoder().decode(textureBase64));
                JsonObject textureJson = JsonParser.parseString(decoded).getAsJsonObject();
                String skinUrl = textureJson.getAsJsonObject("textures")
                    .getAsJsonObject("SKIN").get("url").getAsString();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        PlayerProfile profile = Bukkit.createPlayerProfile(
                            UUID.fromString(formattedUUID), playerName);
                        PlayerTextures textures = profile.getTextures();
                        textures.setSkin(new URL(skinUrl));
                        profile.setTextures(textures);
                        skinCache.put(playerName.toLowerCase(), profile);
                        callback.accept(profile);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error aplicando skin de " + playerName);
                        callback.accept(getDefaultProfile());
                    }
                });
            } catch (Exception e) {
                plugin.getLogger().warning("Error obteniendo skin de " + playerName + ": " + e.getMessage());
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(getDefaultProfile()));
            }
        });
    }

    public PlayerProfile getDefaultProfile() {
        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "Steve");
            return profile;
        } catch (Exception e) {
            return null;
        }
    }

    public void clearCache() {
        skinCache.clear();
    }
}

