package com.soyadrianyt001.advancednpcs.gui;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class ModoGUI {

    private final AdvancedNPCS plugin;
    private final NPCEntity npc;

    public ModoGUI(AdvancedNPCS plugin, NPCEntity npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            color("&8[&b&l✦ &7Elige el Modo del NPC &b&l✦&8]"));
        setBorder(inv);
        inv.setItem(10, createModo("ESTATICO", Material.STONE, "&7&l✦ MODO ESTATICO &7&l✦",
            "&7Quieto, ejecuta comandos al click.",
            "&7➤ &7No se mueve nunca",
            "&7➤ &7Ejecuta comandos al click",
            "&7➤ &7Puede tener dialogo y tienda",
            "&e&lIDEAL PARA&8: &7Portales, tiendas fijas."));
        inv.setItem(12, createModo("VIDA_PROPIA", Material.HEART_OF_THE_SEA, "&a&l✦ MODO VIDA PROPIA &a&l✦",
            "&7Todo autonomo.",
            "&a➤ &7Camina naturalmente",
            "&a➤ &7Duerme y come solo",
            "&a➤ &7Reacciona al mundo",
            "&e&lIDEAL PARA&8: &7Ciudades vivas, RPG."));
        inv.setItem(14, createModo("COMBATE", Material.NETHERITE_SWORD, "&c&l✦ MODO COMBATE &c&l✦",
            "&7Solo existe para pelear.",
            "&c➤ &7Ataca segun configuracion",
            "&c➤ &7Nunca huye",
            "&c➤ &7Drops al morir",
            "&e&lIDEAL PARA&8: &7Mazmorras, arenas."));
        inv.setItem(16, createModo("COMERCIANTE", Material.GOLD_INGOT, "&6&l✦ MODO COMERCIANTE &6&l✦",
            "&7Solo existe para vender.",
            "&6➤ &7Tienda al hacer click",
            "&6➤ &7Precios configurables",
            "&6➤ &7Stock limitado opcional",
            "&e&lIDEAL PARA&8: &7Mercados, tiendas."));
        inv.setItem(19, createModo("GUARDAESPALDAS", Material.NETHERITE_CHESTPLATE, "&b&l✦ MODO GUARDAESPALDAS &b&l✦",
            "&7Sigue y protege al jugador.",
            "&b➤ &7Sigue al jugador dueno",
            "&b➤ &7Ataca amenazas cercanas",
            "&b➤ &7Respawnea al morir",
            "&e&lIDEAL PARA&8: &7Admins, jugadores VIP."));
        inv.setItem(21, createModo("DECORATIVO", Material.PAINTING, "&5&l✦ MODO DECORATIVO &5&l✦",
            "&7No hace nada, solo se ve.",
            "&5➤ &7No interactua",
            "&5➤ &7Solo particulas y efectos",
            "&5➤ &7Poses estaticas",
            "&e&lIDEAL PARA&8: &7Decoracion, builds."));
        inv.setItem(23, createModo("JEFE", Material.WITHER_SKELETON_SKULL, "&4&l✦ MODO JEFE (BOSS) &4&l✦",
            "&7Boss con vida y poderes.",
            "&4➤ &7Barra de vida tipo Boss Bar",
            "&4➤ &7Fases de combate",
            "&4➤ &7Drops especiales al morir",
            "&e&lIDEAL PARA&8: &7Mazmorras, eventos PvE."));
        inv.setItem(25, createModo("EVENTO", Material.FIREWORK_ROCKET, "&d&l✦ MODO EVENTO &d&l✦",
            "&7Solo aparece en eventos.",
            "&d➤ &7Invisible fuera del evento",
            "&d➤ &7Animacion de aparicion epica",
            "&d➤ &7/anpc event start/stop",
            "&e&lIDEAL PARA&8: &7Eventos, raids, fiestas."));
        inv.setItem(49, createCredits());
        player.openInventory(inv);
    }

    private ItemStack createModo(String id, Material mat, String name, String desc, String... funciones) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        lore.add(color("&e&lDESCRIPCION&8:"));
        lore.add(color("&7" + desc));
        lore.add(color(" "));
        lore.add(color("&e&lFUNCIONES&8:"));
        for (int i = 0; i < funciones.length - 1; i++) lore.add(color(funciones[i]));
        lore.add(color(" "));
        lore.add(color(funciones[funciones.length - 1]));
        lore.add(color(" "));
        boolean activo = npc.getModo().equals(id);
        lore.add(color(activo ? "&a✔ ACTIVO - Click para cambiar" : "&7Click para elegir este modo"));
        lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        if (activo) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void setBorder(Inventory inv) {
        int[] borderSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,50,51,52,53};
        Material[] colors = {Material.BLUE_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE};
        long time = System.currentTimeMillis() / 500;
        int colorIndex = (int)(time % colors.length);
        for (int slot : borderSlots) {
            ItemStack glass = new ItemStack(colors[colorIndex]);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(color("&5✦ &7AdvancedNPCS &d&lPremium &5✦"));
            glass.setItemMeta(meta);
            inv.setItem(slot, glass);
        }
    }

    private ItemStack createCredits() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&5✦ &bAdvancedNPCS &d&lPremium &5✦"));
        meta.setLore(Arrays.asList(
            color("&7Creado por &bsoyadrianyt001"),
            color("&7Version&8: &ev1.0.0")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
