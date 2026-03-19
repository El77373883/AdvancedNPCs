package com.soyadrianyt001.advancednpcs.gui;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class InventarioGUI {

    private final AdvancedNPCS plugin;
    private final NPCEntity npc;

    public InventarioGUI(AdvancedNPCS plugin, NPCEntity npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            color("&8[&e&l✦ &7Inventario de &b" + npc.getNombre() + " &e&l✦&8]"));
        loadInventory(inv, player);
        player.openInventory(inv);
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_CHEST_OPEN, 1, 1);
        player.sendMessage(plugin.getMessageManager().color(
            "&8[&b&lAdvancedNPCS &5✦ &d&lPremium&8] &e✦ &7Abriendo tu inventario guardado en &b" + npc.getNombre() + "&7."));
    }

    private void loadInventory(Inventory inv, Player player) {
        String key = "inventario_npc_" + npc.getId();
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        if (!data.contains(key)) return;
        for (int i = 0; i < 54; i++) {
            ItemStack item = data.getItemStack(key + ".slot_" + i);
            if (item != null) inv.setItem(i, item);
        }
    }

    public void saveInventory(Inventory inv, Player player) {
        String key = "inventario_npc_" + npc.getId();
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        for (int i = 0; i < 54; i++) {
            ItemStack item = inv.getItem(i);
            data.set(key + ".slot_" + i, item);
        }
        plugin.getDataManager().savePlayerData(player.getUniqueId());
        player.sendMessage(plugin.getMessageManager().get("inventario_guardado"));
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_CHEST_CLOSE, 1, 1);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
