package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MisionManager {

    private final AdvancedNPCS plugin;

    public MisionManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    public void listQuests(Player player) {
        File questFolder = new File(plugin.getDataFolder(), "quests");
        File[] files = questFolder.listFiles((d, name) -> name.endsWith(".yml"));
        player.sendMessage(plugin.getMessageManager().color(
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        player.sendMessage(plugin.getMessageManager().color(
            "&b&l✦ &7Misiones Disponibles &b&l✦"));
        if (files == null || files.length == 0) {
            player.sendMessage(plugin.getMessageManager().color(
                "&7No hay misiones configuradas."));
        } else {
            FileConfiguration playerData = plugin.getDataManager()
                .getPlayerData(player.getUniqueId());
            for (File file : files) {
                FileConfiguration quest = YamlConfiguration.loadConfiguration(file);
                String id = quest.getString("id", "desconocido");
                String nombre = quest.getString("nombre", "Sin nombre");
                String estado = playerData.getString("misiones." + id + ".estado", "DISPONIBLE");
                String color = estado.equals("COMPLETADA") ? "&a" : estado.equals("EN_PROGRESO") ? "&e" : "&7";
                player.sendMessage(plugin.getMessageManager().color(
                    color + nombre + " &8[" + estado + "&8]"));
            }
        }
        player.sendMessage(plugin.getMessageManager().color(
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
    }

    public void startQuest(Player player, String questId) {
        File file = new File(plugin.getDataFolder(), "quests/" + questId + ".yml");
        if (!file.exists()) return;
        FileConfiguration quest = YamlConfiguration.loadConfiguration(file);
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        data.set("misiones." + questId + ".estado", "EN_PROGRESO");
        data.set("misiones." + questId + ".progreso", 0);
        plugin.getDataManager().savePlayerData(player.getUniqueId());
        plugin.getMessageManager().send(player, "mision_iniciada",
            "%mision_nombre%", quest.getString("nombre", questId));
    }

    public void completeQuest(Player player, String questId) {
        File file = new File(plugin.getDataFolder(), "quests/" + questId + ".yml");
        if (!file.exists()) return;
        FileConfiguration quest = YamlConfiguration.loadConfiguration(file);
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        data.set("misiones." + questId + ".estado", "COMPLETADA");
        plugin.getDataManager().savePlayerData(player.getUniqueId());
        double recompensa = quest.getDouble("recompensa.dinero", 0);
        if (recompensa > 0) {
            plugin.getEconomy().depositPlayer(player, recompensa);
        }
        plugin.getMessageManager().send(player, "mision_completada",
            "%mision_nombre%", quest.getString("nombre", questId));
        plugin.getMessageManager().send(player, "recompensa_recibida",
            "%dinero%", String.valueOf(recompensa),
            "%items%", quest.getString("recompensa.items", "ninguno"));
        plugin.getLogManager().log(-1, player.getName() + " completo mision: " + questId);
    }

    public void resetQuest(Player player, String questId) {
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        data.set("misiones." + questId, null);
        plugin.getDataManager().savePlayerData(player.getUniqueId());
    }

    public String getQuestState(Player player, String questId) {
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        return data.getString("misiones." + questId + ".estado", "DISPONIBLE");
    }
}
