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

public class ProfesionGUI {

    private final AdvancedNPCS plugin;
    private final NPCEntity npc;

    public ProfesionGUI(AdvancedNPCS plugin, NPCEntity npc) {
        this.plugin = plugin;
        this.npc = npc;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54,
            color("&8[&b&l✦ &7Profesion del NPC &e#" + npc.getId() + " &b&l✦&8]"));
        setBorder(inv);
        inv.setItem(10, createProfesion("POLICIA", Material.IRON_SWORD, "&c&l✦ POLICIA &c&l✦",
            "&7Patrulla, arresta y multa.",
            "&c➤ &7Patrulla zonas", "&c➤ &7Arresta jugadores", "&c➤ &7Multa con dinero"));
        inv.setItem(11, createProfesion("GUARDIA", Material.SHIELD, "&6&l✦ GUARDIA &6&l✦",
            "&7Protege una zona.",
            "&6➤ &7Defiende su zona", "&6➤ &7Ataca intrusos", "&6➤ &7Respawnea al morir"));
        inv.setItem(12, createProfesion("CUIDADOR_OVEJAS", Material.WHITE_WOOL, "&f&l✦ CUIDADOR DE OVEJAS &f&l✦",
            "&7Esquila y cuida ovejas.",
            "&f➤ &7Esquila ovejas", "&f➤ &7Recoge lana", "&f➤ &7Protege el rebano"));
        inv.setItem(13, createProfesion("CUIDADOR_ANIMALES", Material.WHEAT, "&a&l✦ CUIDADOR DE ANIMALES &a&l✦",
            "&7Cuida todo tipo de animales.",
            "&a➤ &7Alimenta animales", "&a➤ &7Recoge huevos", "&a➤ &7Ordena vacas"));
        inv.setItem(14, createProfesion("FARMEADOR", Material.GOLDEN_HOE, "&e&l✦ FARMEADOR &e&l✦",
            "&7Gestiona granjas.",
            "&e➤ &7Prepara tierra", "&e➤ &7Planta semillas", "&e➤ &7Riega cultivos"));
        inv.setItem(15, createProfesion("COSECHADOR", Material.DIAMOND_HOE, "&6&l✦ COSECHADOR &6&l✦",
            "&7Cosecha cultivos.",
            "&6➤ &7Cosecha cultivos", "&6➤ &7Replanta automatico", "&6➤ &7Deposita en cofre"));
        inv.setItem(16, createProfesion("MINERO", Material.DIAMOND_PICKAXE, "&b&l✦ MINERO &b&l✦",
            "&7Mina minerales.",
            "&b➤ &7Mina minerales reales", "&b➤ &7Deposita en cofre", "&b➤ &7Zona configurable"));
        inv.setItem(19, createProfesion("DEFENSOR", Material.NETHERITE_SWORD, "&4&l✦ DEFENSOR &4&l✦",
            "&7Defiende su territorio.",
            "&4➤ &7Ataca enemigos", "&4➤ &7Nunca abandona zona", "&4➤ &7Respawnea al morir"));
        inv.setItem(20, createProfesion("TALADOR", Material.DIAMOND_AXE, "&2&l✦ TALADOR &2&l✦",
            "&7Corta arboles.",
            "&2➤ &7Corta arboles", "&2➤ &7Replanta automatico", "&2➤ &7Deposita troncos"));
        inv.setItem(21, createProfesion("CONSTRUCTOR", Material.BRICKS, "&6&l✦ CONSTRUCTOR &6&l✦",
            "&7Construye estructuras.",
            "&6➤ &7Construye bloque a bloque", "&6➤ &7Toma materiales del cofre", "&6➤ &7Puede demoler"));
        inv.setItem(22, createProfesion("PESCADOR", Material.FISHING_ROD, "&3&l✦ PESCADOR &3&l✦",
            "&7Pesca automaticamente.",
            "&3➤ &7Pesca automatico", "&3➤ &7Deposita peces", "&3➤ &7Zona configurable"));
        inv.setItem(23, createProfesion("GUARDABOSQUES", Material.OAK_SAPLING, "&a&l✦ GUARDABOSQUES &a&l✦",
            "&7Protege zonas naturales.",
            "&a➤ &7Protege arboles", "&a➤ &7Ataca taladores sin permiso", "&a➤ &7Replanta zonas"));
        inv.setItem(24, createProfesion("HERRERO", Material.ANVIL, "&7&l✦ HERRERO &7&l✦",
            "&7Repara items por dinero.",
            "&7➤ &7Repara items", "&7➤ &7Cobra con Vault", "&7➤ &7Precio configurable"));
        inv.setItem(25, createProfesion("MEDICO", Material.POTION, "&d&l✦ MEDICO &d&l✦",
            "&7Cura jugadores.",
            "&d➤ &7Cura vida", "&d➤ &7Quita efectos negativos", "&d➤ &7Precio configurable"));
        inv.setItem(28, createProfesion("TRANSPORTISTA", Material.CHEST, "&6&l✦ TRANSPORTISTA &6&l✦",
            "&7Mueve items entre cofres.",
            "&6➤ &7Transporta items", "&6➤ &7Ruta configurable", "&6➤ &7Horario de trabajo"));
        inv.setItem(29, createProfesion("MAGO", Material.BREWING_STAND, "&5&l✦ MAGO &5&l✦",
            "&7Vende pociones.",
            "&5➤ &7Vende pociones", "&5➤ &7Catalogo configurable", "&5➤ &7GUI al hacer click"));
        inv.setItem(30, createProfesion("GUARDADOR_INVENTARIO", Material.ENDER_CHEST, "&e&l✦ GUARDADOR INVENTARIO &e&l✦",
            "&7Guarda inventario del jugador.",
            "&e➤ &754 slots por jugador", "&e➤ &7Nunca pierde contenido", "&e➤ &7Agacharse para abrir"));
        inv.setItem(31, createProfesion("AMIGO", Material.GOLDEN_APPLE, "&d&l✦ AMIGO &d&l✦",
            "&7Amigo personal.",
            "&d➤ &7Te saluda al entrar", "&d➤ &7Te sigue y defiende", "&d➤ &7Recuerda conversaciones"));
        inv.setItem(32, createProfesion("CONGELADOR", Material.PACKED_ICE, "&b&l✦ CONGELADOR &b&l✦",
            "&7Congela jugadores.",
            "&b➤ &7Aplica slowness nivel 10", "&b➤ &7Particulas de hielo", "&b➤ &7Duracion configurable"));
        inv.setItem(33, createProfesion("CURANDERO", Material.GLISTERING_MELON_SLICE, "&a&l✦ CURANDERO &a&l✦",
            "&7Cura aliados.",
            "&a➤ &7Cura jugadores aliados", "&a➤ &7Cura NPCs aliados", "&a➤ &7Rango configurable"));
        inv.setItem(34, createProfesion("ENVENENADOR", Material.SPIDER_EYE, "&2&l✦ ENVENENADOR &2&l✦",
            "&7Envenena enemigos.",
            "&2➤ &7Aplica poison y wither", "&2➤ &7Segun reputacion", "&2➤ &7Cooldown configurable"));
        inv.setItem(35, createProfesion("INVOCADOR", Material.TOTEM_OF_UNDYING, "&5&l✦ INVOCADOR &5&l✦",
            "&7Invoca mobs.",
            "&5➤ &7Invoca mobs en combate", "&5➤ &7Tipo configurable", "&5➤ &7Desaparecen solos"));
        inv.setItem(36, createProfesion("COMERCIANTE_AMBULANTE", Material.EMERALD, "&6&l✦ COMERCIANTE AMBULANTE &6&l✦",
            "&7Camina vendiendo.",
            "&6➤ &7Camina por ruta", "&6➤ &7Abre tienda al acercarte", "&6➤ &7Anuncia su presencia"));
        inv.setItem(37, createProfesion("ESPIA", Material.GRAY_DYE, "&8&l✦ ESPIA &8&l✦",
            "&7Sigue sin ser visto.",
            "&8➤ &7Invisible para jugadores", "&8➤ &7Reporta posicion al admin", "&8➤ &7Sin particulas"));
        inv.setItem(38, createProfesion("CARCELERO", Material.IRON_BARS, "&7&l✦ CARCELERO &7&l✦",
            "&7Vigila la prision.",
            "&7➤ &7Patrulla la prision", "&7➤ &7Teletransporta fugados", "&7➤ &7Cobra fianza"));
        inv.setItem(39, createProfesion("ENTRENADOR", Material.EXPERIENCE_BOTTLE, "&e&l✦ ENTRENADOR &e&l✦",
            "&7Entrena jugadores.",
            "&e➤ &7Sube stats del jugador", "&e➤ &7Da experiencia", "&e➤ &7Precio configurable"));
        inv.setItem(40, createProfesion("SEGUIDOR", Material.COMPASS, "&a&l✦ SEGUIDOR &a&l✦",
            "&7Te sigue a donde vayas.",
            "&a➤ &7Sigue al jugador", "&a➤ &7Corre si tu corres", "&a➤ &7Compatible con todo"));
        inv.setItem(41, createProfesion("COMBATIENTE", Material.NETHERITE_SWORD, "&c&l✦ COMBATIENTE &c&l✦",
            "&7Combate personal.",
            "&c➤ &7Ataca cuando lo permites", "&c➤ &7Defiende al dueno", "&c➤ &7Dano configurable"));
        inv.setItem(42, createProfesion("PAREJA", Material.ROSE_BUSH, "&d&l✦ PAREJA &d&l✦",
            "&7Tu pareja en el servidor.",
            "&d➤ &7Frases romanticas", "&d➤ &7Particulas de corazones", "&d➤ &7Permite hijos NPC"));
        inv.setItem(43, createProfesion("HIJO", Material.EGG, "&e&l✦ HIJO &e&l✦",
            "&7Tu hijo NPC.",
            "&e➤ &7Tamano pequeno auto", "&e➤ &7Te llama papa o mama", "&e➤ &7Puede crecer"));
        inv.setItem(44, createProfesion("ESCLAVO", Material.CHAIN, "&8&l✦ ESCLAVO &8&l✦",
            "&7Obedece tus ordenes.",
            "&8➤ &7Obedece todo", "&8➤ &7Carga items por ti", "&8➤ &7Sin voluntad propia"));
        inv.setItem(46, createToggle("DORMIR", Material.RED_BED, npc.isDormirActivo()));
        inv.setItem(47, createToggle("COMER", Material.COOKED_BEEF, npc.isComerActivo()));
        inv.setItem(48, createToggle("CAMINAR", Material.LEATHER_BOOTS, npc.isCaminarActivo()));
        inv.setItem(49, createVolver());
        inv.setItem(50, createSinProfesion());
        player.openInventory(inv);
    }

    private ItemStack createProfesion(String id, Material mat, String name,
                                       String desc, String... funciones) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        lore.add(color("&e&lDESCRIPCION&8:"));
        lore.add(color("&7" + desc));
        lore.add(color(" "));
        lore.add(color("&e&lFUNCIONES&8:"));
        for (String f : funciones) lore.add(color(f));
        lore.add(color(" "));
        boolean activa = npc.getProfesion().equals(id);
        lore.add(color(activa
            ? "&a✔ &aACTIVA &7- Click para &cdesactivar"
            : "&c✖ &cINACTIVA &7- Click para &aactivar"));
        lore.add(color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        if (activa) {
            meta.addEnchant(
                Enchantment.getByKey(NamespacedKey.minecraft("unbreaking")), 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createToggle(String nombre, Material mat, boolean activo) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color((activo ? "&a&l✦ " : "&c&l✦ ") + nombre +
            (activo ? " &8| &a&lACTIVADO" : " &8| &c&lDESACTIVADO")));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Estado&8: " + (activo ? "&aACTIVADO" : "&cDESACTIVADO")),
            color("&7Click para cambiar estado."),
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSinProfesion() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color("&c&l✦ SIN PROFESION &c&l✦"));
        meta.setLore(Arrays.asList(
            color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
            color("&7Elimina la profesion del NPC."),
            color("&7Lo convierte en decorativo."),
            color("&c⚠ &7Desactiva todo."),
            color("&c&l» CLICK PARA QUITAR «"),
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

    private void setBorder(Inventory inv) {
        int[] borderSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,45,53};
        Material[] colors = {
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

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public NPCEntity getNpc() { return npc; }
}
