package com.soyadrianyt001.advancednpcs.commands;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class RepCommand implements CommandExecutor {

    private final AdvancedNPCS plugin;

    public RepCommand(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
            player.sendMessage(plugin.getMessageManager().color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            player.sendMessage(plugin.getMessageManager().color("&b&l✦ &7Tu Reputacion &b&l✦"));
            if (data.contains("reputacion")) {
                for (String faccion : data.getConfigurationSection("reputacion").getKeys(false)) {
                    int rep = data.getInt("reputacion." + faccion);
                    String color = rep >= 500 ? "&a" : rep >= 0 ? "&e" : "&c";
                    String rango = getRango(rep);
                    player.sendMessage(plugin.getMessageManager().color(
                        "&7Faccion &b" + faccion + "&8: " + color + rep + " &8(" + rango + "&8)"));
                }
            } else {
                player.sendMessage(plugin.getMessageManager().color("&7No tienes reputacion con ninguna faccion."));
            }
            player.sendMessage(plugin.getMessageManager().color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        }
        return true;
    }

    private String getRango(int rep) {
        if (rep >= 1000) return "&6👑 Heroe";
        if (rep >= 500) return "&a😄 Aliado";
        if (rep >= 100) return "&a🙂 Amistoso";
        if (rep >= -99) return "&7😐 Neutral";
        if (rep >= -499) return "&c😠 Hostil";
        return "&4☠ Enemigo";
    }
}
