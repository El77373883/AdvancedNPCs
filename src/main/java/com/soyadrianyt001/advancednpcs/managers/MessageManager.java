package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;

public class MessageManager {

    private final AdvancedNPCS plugin;
    private FileConfiguration messages;
    private File messagesFile;

    public MessageManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) plugin.saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        loadMessages();
    }

    public String get(String key) {
        String msg = messages.getString(key, "&cMensaje no encontrado: " + key);
        return color(msg);
    }

    public String get(String key, String... replacements) {
        String msg = get(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace(replacements[i], replacements[i + 1]);
        }
        return msg;
    }

    public void send(Player player, String key) {
        player.sendMessage(get(key));
    }

    public void send(Player player, String key, String... replacements) {
        player.sendMessage(get(key, replacements));
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getPrefix() {
        return color(plugin.getConfig().getString("prefix",
            "&8[&b&lAdvancedNPCS &5✦ &d&lPremium&8]"));
    }

    public void sendWithPrefix(Player player, String message) {
        player.sendMessage(getPrefix() + " " + color(message));
    }
}
