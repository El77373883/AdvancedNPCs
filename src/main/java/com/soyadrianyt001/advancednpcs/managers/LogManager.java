package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogManager {

    private final AdvancedNPCS plugin;
    private final SimpleDateFormat dateFormat;

    public LogManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void log(int npcId, String mensaje) {
        File file = new File(plugin.getDataManager().getLogsFolder(), "npc_" + npcId + "_log.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> logs = config.getStringList("logs");
        String entry = "[" + dateFormat.format(new Date()) + "] " + mensaje;
        logs.add(entry);
        config.set("logs", logs);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Error guardando log NPC " + npcId);
        }
    }

    public List<String> getLogs(int npcId) {
        File file = new File(plugin.getDataManager().getLogsFolder(), "npc_" + npcId + "_log.yml");
        if (!file.exists()) return new ArrayList<>();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getStringList("logs");
    }

    public void clearLogs(int npcId) {
        File file = new File(plugin.getDataManager().getLogsFolder(), "npc_" + npcId + "_log.yml");
        if (file.exists()) file.delete();
    }

    public void clearAllLogs() {
        File folder = plugin.getDataManager().getLogsFolder();
        File[] files = folder.listFiles((d, name) -> name.endsWith("_log.yml"));
        if (files == null) return;
        for (File file : files) file.delete();
    }
}
