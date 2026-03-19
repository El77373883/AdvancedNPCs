package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final AdvancedNPCS plugin;
    private final File playerDataFolder;
    private final File npcFolder;
    private final File questFolder;
    private final File shopFolder;
    private final File logsFolder;
    private final File buildsFolder;
    private final Map<UUID, FileConfiguration> playerDataCache;

    public DataManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.playerDataCache = new HashMap<>();
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.npcFolder = new File(plugin.getDataFolder(), "npcs");
        this.questFolder = new File(plugin.getDataFolder(), "quests");
        this.shopFolder = new File(plugin.getDataFolder(), "shops");
        this.logsFolder = new File(plugin.getDataFolder(), "logs");
        this.buildsFolder = new File(plugin.getDataFolder(), "builds");
        createFolders();
    }

    private void createFolders() {
        playerDataFolder.mkdirs();
        npcFolder.mkdirs();
        questFolder.mkdirs();
        shopFolder.mkdirs();
        logsFolder.mkdirs();
        buildsFolder.mkdirs();
    }

    public FileConfiguration getPlayerData(UUID uuid) {
        if (playerDataCache.containsKey(uuid)) return playerDataCache.get(uuid);
        File file = new File(playerDataFolder, uuid.toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        playerDataCache.put(uuid, config);
        return config;
    }

    public void savePlayerData(UUID uuid) {
        if (!playerDataCache.containsKey(uuid)) return;
        File file = new File(playerDataFolder, uuid.toString() + ".yml");
        try {
            playerDataCache.get(uuid).save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error guardando datos de jugador: " + uuid);
        }
    }

    public FileConfiguration getNPCConfig(int id) {
        File file = new File(npcFolder, "npc_" + id + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveNPCConfig(int id, FileConfiguration config) {
        File file = new File(npcFolder, "npc_" + id + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error guardando NPC: " + id);
        }
    }

    public void deleteNPCConfig(int id) {
        File file = new File(npcFolder, "npc_" + id + ".yml");
        if (file.exists()) file.delete();
    }

    public File getNpcFolder() { return npcFolder; }
    public File getPlayerDataFolder() { return playerDataFolder; }
    public File getLogsFolder() { return logsFolder; }
    public File getBuildsFolder() { return buildsFolder; }

    public void saveAll() {
        for (UUID uuid : playerDataCache.keySet()) {
            savePlayerData(uuid);
        }
    }
}
