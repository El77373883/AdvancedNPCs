package com.soyadrianyt001.advancednpcs.gui;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class InteraccionFamiliaGUI {

    private final AdvancedNPCS plugin;
    private final NPCEntity npc;

    public InteraccionFamiliaGUI(AdvancedNPCS plugin, NPCEntity npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            color("&8[&d&l✦ &7Interacciones Familiares &e#" + npc.getId() + " &d&l✦&8]"));
        setBorder(inv);
        inv.setItem(10, createToggle("abrazo", Material.PINK_WOOL,
            "&d&l✦ ABRAZO A LA ESPOSA &d&l✦",
            "&7El NPC abraza a su esposa",
            "&7al estar cerca con particulas."));
        inv.setItem(11, createToggle("beso", Material.ROSE_BUSH,
            "&d&l✦ BESOS &d&l✦",
            "&7La pareja se da besos",
            "&7con explosion de corazones."));
        inv.setItem(12, createToggle("jugar_hijo", Material.SLIME_BALL,
            "&e&l✦ JUGAR CON EL HIJO &e&l✦",
            "&7El padre juega con el hijo.",
            "&7Corren juntos y se divierten."));
        inv.setItem(13, createToggle("comer_juntos", Material.COOKED_BEEF,
            "&6&l✦ COMER JUNTOS &6&l✦",
            "&7La familia come junta",
            "&7en una mesa a horas fijas."));
        inv.setItem(14, createToggle("ensenar", Material.BOOK,
            "&a&l✦ ENSENAR A TRABAJAR &a&l✦",
            "&7El padre lleva al hijo",
            "&7a su zona de trabajo."));
        inv.setItem(15, createToggle("hijo_corre", Material.LEATHER_BOOTS,
            "&e&l✦ HIJO CORRE A LOS PADRES &e&l✦",
            "&7El hijo corre hacia",
            "&7sus padres al verlos."));
        inv.setItem(16, createToggle("paseo", Material.COMPASS,
            "&a&l✦ PASEO FAMILIAR &a&l✦",
            "&7La familia pasea junta",
            "&7por su zona."));
        inv.setItem(19, createToggle("proteger_hijo", Material.SHIELD,
            "&c&l✦ PROTEGER AL HIJO &c&l✦",
            "&7Padres entran en combate",
            "&7si alguien ataca al hijo."));
        if (npc.getFamiliaEsposaId() == -1) {
            inv.setItem(28, createSpawnEsposa());
        }
        if (npc.getFamiliaHijoId() == -1 && npc.getFamiliaEsposaId() != -1) {
            inv.setItem(30, createSpawnHijo());
        }
        inv.setItem(49, createVolver());
        player.openInventory(inv);
    }

    private ItemStack createToggle(String key, Material mat, String name,
                                    String desc1, String desc2) {
        boolean activo = isActive(key);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name + " &8| " + (activo ? "&a&lON" : "&c&lOFF")));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&e&lDESCRIPCION&8:"),
            color("&7" + desc1),
            color("&7" + desc2),
            color(" "),
            color("&7Estado&8: " + (activo ? "&aACTIVADO" : "&cDESACTIVADO")),
            color("&7Click para cambiar estado."),
            color(" "),
            color("&5✦ &7Creado por &bsoyadrianyt001 &5✦"),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        if (activo) {
            Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft("unbreaking"));
            if (ench != null) {
                meta.addEnchant(ench, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSpawnEsposa() {
        ItemStack item = new ItemStack(Material.ROSE_BUSH);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&d&l✦ CREAR PAREJA NPC &d&l✦"));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Crea un NPC pareja"),
            color("&7vinculado a este NPC."),
            color(" "),
            color("&d&l» CLICK PARA CREAR «"),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSpawnHijo() {
        ItemStack item = new ItemStack(Material.EGG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&e&l✦ CREAR HIJO NPC &e&l✦"));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Crea un mini NPC hijo"),
            color("&7para esta familia."),
            color(" "),
            color("&e&l» CLICK PARA CREAR «"),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createVolver() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&7&l← VOLVER AL CONFIG"));
        meta.setLore(Arrays.asList(
            color("&7Regresa al menu de configuracion.")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private boolean isActive(String key) {
        return plugin.getDataManager().getNPCConfig(npc.getId())
            .getBoolean("interacciones." + key, false);
    }

    private void setBorder(Inventory inv) {
        int[] borderSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,50,51,52,53};
        Material[] colors = {
            Material.PINK_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE
        };
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

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
