package com.soyadrianyt001.advancednpcs.gui;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.function.Consumer;

public class ConfirmGUI {

    private final AdvancedNPCS plugin;
    private final String titulo;
    private final String descripcion;
    private final Consumer<Boolean> callback;

    public ConfirmGUI(AdvancedNPCS plugin, String titulo, String descripcion, Consumer<Boolean> callback) {
        this.plugin = plugin;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.callback = callback;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9,
            color("&8[&6&l⚠ &7" + titulo + " &6&l⚠&8]"));
        for (int i = 0; i < 9; i++) {
            ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(color("&8 "));
            glass.setItemMeta(meta);
            inv.setItem(i, glass);
        }
        inv.setItem(2, createSi());
        inv.setItem(6, createNo());
        player.openInventory(inv);
    }

    private ItemStack createSi() {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&a&l✔ SI, CONFIRMAR"));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7" + descripcion),
            color(" "),
            color("&a&l» CLICK PARA CONFIRMAR «"),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNo() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&c&l✖ NO, CANCELAR"));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Cancela la accion."),
            color("&7No se hara ningun cambio."),
            color(" "),
            color("&c&l» CLICK PARA CANCELAR «"),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public Consumer<Boolean> getCallback() { return callback; }
}
