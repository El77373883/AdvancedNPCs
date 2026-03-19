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

public class ConstruirGUI {

    private final AdvancedNPCS plugin;
    private final NPCEntity npc;

    public ConstruirGUI(AdvancedNPCS plugin, NPCEntity npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            color("&8[&6&l✦ &7Constructor &b" + npc.getNombre() + " &6&l✦&8]"));
        setBorder(inv);
        inv.setItem(10, createDiseño());
        inv.setItem(20, createPuntoInicio());
        inv.setItem(22, createCofre());
        inv.setItem(24, createVelocidad());
        inv.setItem(31, createIniciar());
        inv.setItem(32, createPausar());
        inv.setItem(33, createCancelar());
        inv.setItem(49, createCredits());
        player.openInventory(inv);
    }

    private ItemStack createDiseño() {
        return createItem(Material.DARK_OAK_LOG, "&6&l✦ ELEGIR DISEÑO &6&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Elige el tipo de casa o",
            "&7estructura a construir.",
            " ",
            "&6➤ &7Casa pequena de madera",
            "&6➤ &7Casa mediana de piedra",
            "&6➤ &7Casa grande de ladrillo",
            "&6➤ &7Castillo",
            "&6➤ &7Casa en el arbol",
            "&6➤ &7Casa de playa",
            "&6➤ &7Casa de cristal",
            "&6➤ &7Casa de netherite",
            "&6➤ &7Diseno personalizado YAML",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&6&l» CLICK PARA ELEGIR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createPuntoInicio() {
        return createItem(Material.COMPASS, "&a&l✦ PUNTO DE INICIO &a&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Define donde empezara",
            "&7a construir el NPC.",
            "&7Por defecto tu posicion.",
            " ",
            "&a&l» CLICK PARA DEFINIR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createCofre() {
        return createItem(Material.CHEST, "&6&l✦ COFRE DE MATERIALES &6&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Asigna el cofre del que",
            "&7el NPC tomara los bloques.",
            " ",
            "&6&l» CLICK PARA ASIGNAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createVelocidad() {
        return createItem(Material.FEATHER, "&b&l✦ VELOCIDAD &b&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Velocidad actual&8: &eNormal",
            " ",
            "&b➤ &7Lenta - 1 bloque cada 2s",
            "&b➤ &7Normal - 1 bloque por segundo",
            "&b➤ &7Rapida - 3 bloques por segundo",
            "&b➤ &7Ultra - 5 bloques por segundo",
            " ",
            "&b&l» CLICK PARA CAMBIAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createIniciar() {
        return createItem(Material.LIME_CONCRETE, "&a&l✦ INICIAR CONSTRUCCION &a&l✦",
            "&7El NPC comenzara a",
            "&7construir inmediatamente.",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&a&l» CLICK PARA INICIAR «");
    }

    private ItemStack createPausar() {
        return createItem(Material.YELLOW_CONCRETE, "&e&l✦ PAUSAR CONSTRUCCION &e&l✦",
            "&7Pausa la construccion.",
            "&7El NPC recordara donde",
            "&7se quedo al reanudar.",
            " ",
            "&e&l» CLICK PARA PAUSAR «");
    }

    private ItemStack createCancelar() {
        return createItem(Material.RED_CONCRETE, "&c&l✦ CANCELAR CONSTRUCCION &c&l✦",
            "&7Cancela la construccion.",
            "&cATENCION&7: Los bloques ya",
            "&7colocados no se eliminaran.",
            " ",
            "&c&l» CLICK PARA CANCELAR «");
    }

    private void setBorder(Inventory inv) {
        int[] borderSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,50,51,52,53};
        Material[] colors = {Material.ORANGE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE};
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

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        java.util.List<String> loreList = new java.util.ArrayList<>();
        for (String l : lore) loreList.add(color(l));
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
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
