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

public class ConfigGUI {

    private final AdvancedNPCS plugin;
    private final NPCEntity npc;

    public ConfigGUI(AdvancedNPCS plugin, NPCEntity npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            color("&8[&b&lNPC &e#" + npc.getId() + " &8- &7" + npc.getNombre() + " &5✦ &d&lConfig&8]"));
        setBorder(inv);
        inv.setItem(10, createInfo());
        inv.setItem(11, createNombre());
        inv.setItem(12, createSkin());
        inv.setItem(13, createModo());
        inv.setItem(14, createProfesion());
        inv.setItem(15, createDialogo());
        inv.setItem(16, createParticulas());
        inv.setItem(19, createEfectos());
        inv.setItem(20, createTamano());
        inv.setItem(21, createHorario());
        inv.setItem(22, createReputacion());
        inv.setItem(23, createTienda());
        inv.setItem(24, createMision());
        inv.setItem(25, createEmocion());
        inv.setItem(28, createEquipamiento());
        inv.setItem(29, createMascota());
        inv.setItem(30, createMontura());
        inv.setItem(31, createConstruir());
        inv.setItem(32, createInteracciones());
        inv.setItem(37, createMover());
        inv.setItem(39, createEliminar());
        inv.setItem(44, createVolver());
        inv.setItem(49, createCredits());
        player.openInventory(inv);
    }

    private void setBorder(Inventory inv) {
        int[] borderSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,49,50,51,52,53};
        Material[] colors = {
            Material.CYAN_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
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

    private ItemStack createInfo() {
        return createItem(Material.PLAYER_HEAD, "&b&l✦ INFORMACION DEL NPC &b&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7ID&8: &e#" + npc.getId(),
            "&7Nombre&8: &b" + npc.getNombre(),
            "&7Mundo&8: &a" + npc.getMundo(),
            "&7Modo&8: &d" + npc.getModo(),
            "&7Profesion&8: &6" + npc.getProfesion(),
            "&7Estado&8: &a" + npc.getEstado(),
            "&7Emocion&8: &d" + npc.getEmocion(),
            "&7Vida&8: &c" + npc.getVidaActual() + "&7/&c" + npc.getVidaMaxima(),
            "&7Escala&8: &e" + npc.getEscala(),
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createNombre() {
        return createItem(Material.NAME_TAG, "&e&l✦ CAMBIAR NOMBRE &e&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Nombre actual&8: &e" + npc.getNombre(),
            " ",
            "&7Cambia el nombre visible",
            "&7del NPC en el mundo.",
            "&7Soporta colores con &7&.",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&e&l» CLICK PARA CAMBIAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createSkin() {
        return createItem(Material.PAPER, "&d&l✦ CAMBIAR SKIN &d&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Skin actual&8: &e" + npc.getSkin(),
            " ",
            "&7Cambia la apariencia del NPC.",
            "&d➤ &7Por nombre de jugador",
            "&d➤ &7Por URL de textura",
            "&d➤ &7Skin default Steve",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&d&l» CLICK PARA CAMBIAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createModo() {
        return createItem(Material.COMPASS, "&b&l✦ MODO DEL NPC &b&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Modo actual&8: &e" + npc.getModo(),
            " ",
            "&7Cambia el comportamiento",
            "&7base del NPC.",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&b&l» CLICK PARA CAMBIAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createProfesion() {
        return createItem(Material.DIAMOND, "&6&l✦ PROFESION DEL NPC &6&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Profesion actual&8: &e" + npc.getProfesion(),
            " ",
            "&7Asigna una profesion al NPC",
            "&7para darle comportamiento.",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&6&l» CLICK PARA ELEGIR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createDialogo() {
        return createItem(Material.BOOK, "&b&l✦ CONFIGURAR DIALOGO &b&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Edita los dialogos y",
            "&7respuestas del NPC.",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&b&l» CLICK PARA EDITAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createParticulas() {
        return createItem(Material.BLAZE_POWDER, "&5&l✦ PARTICULAS &5&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Configura las particulas",
            "&7del NPC.",
            "&7Estado&8: &e" + (npc.getParticulas().isEmpty() ? "Ninguna" : npc.getParticulas().size() + " activas"),
            " ",
            "&5&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createEfectos() {
        return createItem(Material.GLOWSTONE_DUST, "&e&l✦ EFECTOS VISUALES &e&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Glow, tamanio, animaciones.",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&e&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createTamano() {
        return createItem(Material.PISTON, "&7&l✦ TAMANIO DEL NPC &7&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Escala actual&8: &e" + npc.getEscala(),
            "&7Minimo&8: &c0.1 &7Maximo&8: &c5.0",
            " ",
            "&7&l» CLICK PARA CAMBIAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createHorario() {
        return createItem(Material.CLOCK, "&6&l✦ HORARIO DE ACTIVIDAD &6&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Define en que horas",
            "&7el NPC estara activo.",
            " ",
            "&6&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createReputacion() {
        return createItem(Material.GOLD_INGOT, "&e&l✦ FACCION Y REPUTACION &e&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Faccion actual&8: &b" + npc.getFaccion(),
            " ",
            "&e&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createTienda() {
        return createItem(Material.CHEST, "&a&l✦ CONFIGURAR TIENDA &a&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Estado&8: &e" + (npc.isTiendaActiva() ? "&aACTIVA" : "&cINACTIVA"),
            " ",
            "&a&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createMision() {
        return createItem(Material.MAP, "&d&l✦ ASIGNAR MISION &d&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Asigna misiones a este NPC.",
            " ",
            "&d&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createEmocion() {
        return createItem(Material.POPPY, "&c&l✦ ESTADO DE ANIMO &c&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Estado actual&8: &e" + npc.getEmocion(),
            " ",
            "&c&l» CLICK PARA CAMBIAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createEquipamiento() {
        return createItem(Material.IRON_CHESTPLATE, "&c&l✦ EQUIPAMIENTO &c&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Armadura y armas visibles.",
            " ",
            "&c&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createMascota() {
        return createItem(Material.BONE, "&a&l✦ MASCOTA &a&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Asigna un animal al NPC.",
            " ",
            "&a&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createMontura() {
        return createItem(Material.SADDLE, "&6&l✦ MONTURA &6&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Entidad que monta el NPC.",
            " ",
            "&6&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createConstruir() {
        return createItem(Material.BRICKS, "&6&l✦ CONSTRUIR &6&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7El NPC construira casas",
            "&7y estructuras.",
            " ",
            "&5✦ &7Creado por &bsoyadrianyt001 &5✦",
            "&6&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createInteracciones() {
        return createItem(Material.PINK_WOOL, "&d&l✦ INTERACCIONES FAMILIARES &d&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Configura interacciones",
            "&7entre la familia de NPCs.",
            " ",
            "&d&l» CLICK PARA CONFIGURAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createMover() {
        return createItem(Material.COMPASS, "&b&l✦ MOVER NPC &b&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Mueve el NPC a tu posicion.",
            " ",
            "&b&l» CLICK PARA MOVER «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createEliminar() {
        return createItem(Material.BARRIER, "&c&l✦ ELIMINAR NPC &c&l✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&cATENCION&7: Accion permanente.",
            "&7Se eliminara el NPC &e#" + npc.getId(),
            "&7y todos sus datos.",
            " ",
            "&c&l» CLICK PARA ELIMINAR «",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private ItemStack createVolver() {
        return createItem(Material.ARROW, "&7&l← VOLVER AL PANEL",
            "&7Regresa al panel principal.");
    }

    private ItemStack createCredits() {
        return createItem(Material.NETHER_STAR, "&5✦ &bAdvancedNPCS &d&lPremium &5✦",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
            "&7Creado por &bsoyadrianyt001",
            "&7Version&8: &ev1.0.0",
            "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public NPCEntity getNpc() { return npc; }
}
