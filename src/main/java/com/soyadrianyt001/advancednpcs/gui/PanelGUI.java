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
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PanelGUI {

    private final AdvancedNPCS plugin;
    private int page;

    public PanelGUI(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.page = 0;
    }

    public PanelGUI(AdvancedNPCS plugin, int page) {
        this.plugin = plugin;
        this.page = page;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            color("&8[&b&l⚡ AdvancedNPCS &5✦ &d&lPremium &8⚡]"));
        setBorder(inv);
        inv.setItem(10, createCrearNPC());
        List<NPCEntity> npcs = plugin.getNPCManager().getAllNPCs();
        int startIndex = page * 28;
        int slot = 11;
        for (int i = startIndex; i < Math.min(startIndex + 28, npcs.size()); i++) {
            if (slot == 45) break;
            inv.setItem(slot, createNPCHead(npcs.get(i)));
            slot++;
            if (slot == 18) slot = 19;
            if (slot == 27) slot = 28;
            if (slot == 36) slot = 37;
            if (slot == 44) slot = 45;
        }
        inv.setItem(45, createArrow("&7&l← PAGINA ANTERIOR", page, getTotalPages(npcs.size())));
        inv.setItem(53, createArrow("&e&lPAGINA SIGUIENTE →", page, getTotalPages(npcs.size())));
        inv.setItem(49, createCredits());
        player.openInventory(inv);
    }

    private void setBorder(Inventory inv) {
        int[] borderSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,50,51,52,53};
        Material[] colors = {
            Material.RED_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE
        };
        long time = System.currentTimeMillis() / 500;
        int colorIndex = (int)(time % colors.length);
        for (int i = 0; i < borderSlots.length; i++) {
            Material mat = (i == 0 || i == 7 || i == 17 || i == 24) ?
                Material.GOLD_BLOCK : colors[colorIndex];
            ItemStack glass = new ItemStack(mat);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName(color("&5✦"));
            glass.setItemMeta(meta);
            inv.setItem(borderSlots[i], glass);
        }
    }

    private ItemStack createCrearNPC() {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&a&l✦ CREAR NUEVO NPC &a&l✦"));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Crea un nuevo NPC en tu"),
            color("&7posicion actual del mundo."),
            color(" "),
            color("&a➤ &7ID asignado automaticamente"),
            color("&a➤ &7Sin limite de NPCs"),
            color("&a➤ &7Se registra en la config"),
            color(" "),
            color("&5✦ &7Creado por &bsoyadrianyt001 &5✦"),
            color("&a&l» CLICK PARA CREAR «"),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNPCHead(NPCEntity npc) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(color("&b" + npc.getNombre()));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7ID&8: &e#" + npc.getId()),
            color("&7Modo&8: &d" + npc.getModo()),
            color("&7Profesion&8: &6" + npc.getProfesion()),
            color("&7Mundo&8: &a" + npc.getMundo()),
            color("&7Estado&8: &a" + npc.getEstado()),
            color(" "),
            color("&a✔ &7Editable por panel o config"),
            color("&7Archivo&8: &7npcs/npc_&e" + npc.getId() + "&7.yml"),
            color(" "),
            color("&5✦ &7AdvancedNPCS &d&lPremium"),
            color("&5✦ &7Creado por &bsoyadrianyt001"),
            color("&b&l» CLICK PARA ADMINISTRAR «"),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createArrow(String name, int currentPage, int totalPages) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Pagina actual&8: &e" + (currentPage + 1)),
            color("&7Total paginas&8: &e" + totalPages),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCredits() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&5✦ &bAdvancedNPCS &d&lPremium &5✦"));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Creado por &bsoyadrianyt001"),
            color("&7Version&8: &ev1.0.0"),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private int getTotalPages(int total) {
        return Math.max(1, (int) Math.ceil(total / 28.0));
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public int getPage() { return page; }
}
