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
import java.util.Arrays;
import java.util.List;

public class TiendaGUI {

    private final AdvancedNPCS plugin;
    private final NPCEntity npc;

    public TiendaGUI(AdvancedNPCS plugin, NPCEntity npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            color("&8[&a&l✦ &7Tienda de &b" + npc.getNombre() + " &a&l✦&8]"));
        setBorder(inv);
        loadItems(inv, player);
        inv.setItem(49, createCredits());
        player.openInventory(inv);
        player.playSound(player.getLocation(),
            org.bukkit.Sound.BLOCK_CHEST_OPEN, 1, 1);
    }

    private void loadItems(Inventory inv, Player player) {
        FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
        if (!config.contains("tienda.items")) {
            ItemStack placeholder = new ItemStack(Material.BARRIER);
            ItemMeta meta = placeholder.getItemMeta();
            meta.setDisplayName(color("&cNo hay items configurados."));
            meta.setLore(Arrays.asList(
                color("&7Edita el archivo"),
                color("&7npcs/npc_" + npc.getId() + ".yml"),
                color("&7para agregar items a la tienda.")
            ));
            placeholder.setItemMeta(meta);
            inv.setItem(22, placeholder);
            return;
        }
        List<String> items = config.getStringList("tienda.items");
        int slot = 10;
        for (String itemStr : items) {
            if (slot >= 44) break;
            String[] parts = itemStr.split(":");
            if (parts.length < 3) continue;
            try {
                Material mat = Material.valueOf(parts[0]);
                double precio = Double.parseDouble(parts[1]);
                String nombre = parts[2];
                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(color("&e" + nombre));
                double balance = plugin.getEconomy().getBalance(player);
                meta.setLore(Arrays.asList(
                    color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
                    color("&7Precio&8: &e$" + precio),
                    color("&7Tu balance&8: &e$" + balance),
                    color(" "),
                    color(balance >= precio ? "&a» CLICK PARA COMPRAR «" : "&cNo tienes suficiente dinero"),
                    color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
                ));
                item.setItemMeta(meta);
                inv.setItem(slot, item);
                slot++;
                if (slot == 17) slot = 19;
                if (slot == 26) slot = 28;
                if (slot == 35) slot = 37;
            } catch (Exception e) {
                plugin.getLogger().warning("Item invalido en tienda NPC " + npc.getId() + ": " + itemStr);
            }
        }
    }

    private void setBorder(Inventory inv) {
        int[] borderSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,50,51,52,53};
        Material[] colors = {Material.GREEN_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE};
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
