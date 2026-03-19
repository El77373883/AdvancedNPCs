package com.soyadrianyt001.advancednpcs.gui;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {

    private final AdvancedNPCS plugin;

    public GUIListener(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;
        String title = ChatColor.stripColor(event.getView().getTitle());

        if (title.contains("AdvancedNPCS") && title.contains("Premium") && title.contains("⚡")) {
            event.setCancelled(true);
            handlePanelClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (title.contains("Config") && title.contains("NPC #")) {
            event.setCancelled(true);
            handleConfigClick(player, event.getSlot(), event.getCurrentItem(), title);
            return;
        }
        if (title.contains("Profesion del NPC")) {
            event.setCancelled(true);
            handleProfesionClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (title.contains("Modo del NPC")) {
            event.setCancelled(true);
            handleModoClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (title.contains("⚠") && title.contains("seguro")) {
            event.setCancelled(true);
            handleConfirmClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (title.contains("Tienda de")) {
            event.setCancelled(true);
            handleTiendaClick(player, event.getSlot(), event.getCurrentItem(), title);
            return;
        }
        if (title.contains("Interacciones Familiares")) {
            event.setCancelled(true);
            handleInteraccionClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (title.contains("Constructor")) {
            event.setCancelled(true);
            handleConstruirClick(player, event.getSlot(), event.getCurrentItem(), title);
            return;
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (title.contains("Inventario de")) {
            for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
                if (title.contains(npc.getNombre())) {
                    new InventarioGUI(plugin, npc).saveInventory(event.getInventory(), player);
                    break;
                }
            }
        }
    }

    private void handlePanelClick(Player player, int slot, ItemStack item) {
        if (slot == 10 && item.getType() == Material.EMERALD) {
            player.closeInventory();
            plugin.getListeners().getChatListener().awaitInput(player,
                "¿Como quieres llamar a tu NPC?", nombre -> {
                    NPCEntity npc = plugin.getNPCManager().createNPC(
                        nombre, player.getLocation(), player);
                    plugin.getMessageManager().send(player, "npc_creado",
                        "%npc_id%", String.valueOf(npc.getId()));
                    player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    plugin.getParticulasManager().spawnHearts(player.getLocation());
                    new ModoGUI(plugin, npc).open(player);
                });
            return;
        }
        if (slot == 45 && item.getType() == Material.ARROW) {
            int page = getCurrentPage(player);
            if (page > 0) new PanelGUI(plugin, page - 1).open(player);
            return;
        }
        if (slot == 53 && item.getType() == Material.ARROW) {
            int page = getCurrentPage(player);
            int total = plugin.getNPCManager().getTotalNPCs();
            int totalPages = (int) Math.ceil(total / 28.0);
            if (page < totalPages - 1) new PanelGUI(plugin, page + 1).open(player);
            return;
        }
        if (item.getType() == Material.PLAYER_HEAD && item.getItemMeta() != null) {
            String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            NPCEntity npc = plugin.getNPCManager().getNPCByName(displayName);
            if (npc != null) new ConfigGUI(plugin, npc).open(player);
        }
    }

    private void handleConfigClick(Player player, int slot, ItemStack item, String title) {
        int npcId = extractNPCId(title);
        if (npcId == -1) return;
        NPCEntity npc = plugin.getNPCManager().getNPC(npcId);
        if (npc == null) return;
        switch (slot) {
            case 11 -> {
                player.closeInventory();
                plugin.getListeners().getChatListener().awaitInput(player,
                    "Escribe el nuevo nombre del NPC:", nombre -> {
                        npc.setNombre(nombre);
                        npc.saveToConfig();
                        npc.respawn();
                        plugin.getMessageManager().send(player, "npc_editado",
                            "%npc_id%", String.valueOf(npcId));
                        new ConfigGUI(plugin, npc).open(player);
                    });
            }
            case 12 -> {
                player.closeInventory();
                plugin.getListeners().getChatListener().awaitInput(player,
                    "Escribe el nombre del jugador para la skin:", skinName -> {
                        npc.setSkin(skinName);
                        npc.saveToConfig();
                        npc.respawn();
                        plugin.getMessageManager().send(player, "skin_cambiada",
                            "%npc_id%", String.valueOf(npcId));
                        new ConfigGUI(plugin, npc).open(player);
                    });
            }
            case 13 -> new ModoGUI(plugin, npc).open(player);
            case 14 -> new ProfesionGUI(plugin, npc).open(player);
            case 20 -> {
                player.closeInventory();
                plugin.getListeners().getChatListener().awaitInput(player,
                    "Escribe la nueva escala (0.1 - 5.0):", escalaStr -> {
                        try {
                            double escala = Double.parseDouble(escalaStr);
                            escala = Math.max(0.1, Math.min(5.0, escala));
                            npc.setEscala(escala);
                            npc.saveToConfig();
                            npc.respawn();
                            plugin.getMessageManager().sendWithPrefix(player,
                                "&a✔ &7Escala cambiada a &e" + escala);
                        } catch (NumberFormatException e) {
                            plugin.getMessageManager().sendWithPrefix(player, "&cEscala invalida.");
                        }
                        new ConfigGUI(plugin, npc).open(player);
                    });
            }
            case 23 -> {
                npc.setTiendaActiva(!npc.isTiendaActiva());
                npc.saveToConfig();
                new ConfigGUI(plugin, npc).open(player);
            }
            case 31 -> new ConstruirGUI(plugin, npc).open(player);
            case 32 -> new InteraccionFamiliaGUI(plugin, npc).open(player);
            case 37 -> {
                plugin.getConfirmCallbacks().register(player, confirmed -> {
                    if (confirmed) {
                        plugin.getPacketManager().moveNPC(npc, player.getLocation());
                        npc.saveToConfig();
                        plugin.getMessageManager().sendWithPrefix(player, "&a✔ &7NPC movido.");
                    }
                    new ConfigGUI(plugin, npc).open(player);
                });
                new ConfirmGUI(plugin, "Mover NPC",
                    "Mueve el NPC a tu posicion actual.",
                    confirmed -> {}).open(player);
            }
            case 39 -> {
                plugin.getConfirmCallbacks().register(player, confirmed -> {
                    if (confirmed) {
                        plugin.getNPCManager().deleteNPC(npcId);
                        plugin.getMessageManager().send(player, "npc_eliminado",
                            "%npc_id%", String.valueOf(npcId));
                        new PanelGUI(plugin).open(player);
                    } else {
                        new ConfigGUI(plugin, npc).open(player);
                    }
                });
                new ConfirmGUI(plugin, "Eliminar NPC",
                    "Eliminar NPC #" + npcId + " permanentemente.",
                    confirmed -> {}).open(player);
            }
            case 44 -> new PanelGUI(plugin).open(player);
        }
    }

    private void handleProfesionClick(Player player, int slot, ItemStack item) {
        String title = ChatColor.stripColor(player.getOpenInventory().getTitle());
        int npcId = extractNPCId(title);
        if (npcId == -1) {
            for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
                if (title.contains(npc.getNombre())) {
                    npcId = npc.getId();
                    break;
                }
            }
        }
        if (npcId == -1) return;
        final int finalNpcId = npcId;
        NPCEntity npc = plugin.getNPCManager().getNPC(npcId);
        if (npc == null) return;
        if (slot == 46) {
            npc.setDormirActivo(!npc.isDormirActivo());
            npc.saveToConfig();
            new ProfesionGUI(plugin, npc).open(player);
            return;
        }
        if (slot == 47) {
            npc.setComerActivo(!npc.isComerActivo());
            npc.saveToConfig();
            new ProfesionGUI(plugin, npc).open(player);
            return;
        }
        if (slot == 48) {
            npc.setCaminarActivo(!npc.isCaminarActivo());
            npc.saveToConfig();
            new ProfesionGUI(plugin, npc).open(player);
            return;
        }
        if (slot == 50) {
            npc.setProfesion("NINGUNA");
            npc.saveToConfig();
            new ProfesionGUI(plugin, npc).open(player);
            return;
        }
        String profesion = getProfesionFromSlot(slot);
        if (profesion == null) return;
        final String profFinal = profesion;
        plugin.getConfirmCallbacks().register(player, confirmed -> {
            if (confirmed) {
                npc.setProfesion(profFinal);
                npc.saveToConfig();
                plugin.getMessageManager().send(player, "profesion_activada",
                    "%profesion%", profFinal);
                player.playSound(player.getLocation(),
                    org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                plugin.getTrabajoManager().startTrabajo(npc);
            }
            new ProfesionGUI(plugin, npc).open(player);
        });
        new ConfirmGUI(plugin, "Asignar Profesion",
            "Asignar " + profFinal + " al NPC " + npc.getNombre(),
            confirmed -> {}).open(player);
    }

    private void handleModoClick(Player player, int slot, ItemStack item) {
        String title = ChatColor.stripColor(player.getOpenInventory().getTitle());
        NPCEntity npc = null;
        for (NPCEntity n : plugin.getNPCManager().getAllNPCs()) {
            if (title.contains(n.getNombre())) {
                npc = n;
                break;
            }
        }
        if (npc == null) return;
        String modo = getModoFromSlot(slot);
        if (modo == null) return;
        final String modoFinal = modo;
        final NPCEntity npcFinal = npc;
        plugin.getConfirmCallbacks().register(player, confirmed -> {
            if (confirmed) {
                npcFinal.setModo(modoFinal);
                npcFinal.saveToConfig();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7Modo cambiado a &e" + modoFinal);
                player.playSound(player.getLocation(),
                    org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
            new ConfigGUI(plugin, npcFinal).open(player);
        });
        new ConfirmGUI(plugin, "Cambiar Modo",
            "Cambiar modo del NPC a " + modoFinal,
            confirmed -> {}).open(player);
    }

    private void handleConfirmClick(Player player, int slot, ItemStack item) {
        if (slot == 2 && item.getType() == Material.LIME_STAINED_GLASS_PANE) {
            plugin.getConfirmCallbacks().execute(player, true);
        } else if (slot == 6 && item.getType() == Material.RED_STAINED_GLASS_PANE) {
            plugin.getConfirmCallbacks().execute(player, false);
        }
    }

    private void handleTiendaClick(Player player, int slot, ItemStack item, String title) {
        if (item.getType() == Material.BARRIER ||
            item.getType() == Material.NETHER_STAR ||
            item.getItemMeta() == null) return;
        String npcNombre = title.replace("Tienda de", "").trim();
        NPCEntity npc = plugin.getNPCManager().getNPCByName(npcNombre);
        if (npc == null) return;
        double precio = extractPrice(item);
        if (precio <= 0) return;
        if (!plugin.getEconomy().has(player, precio)) {
            plugin.getMessageManager().send(player, "sin_dinero");
            return;
        }
        plugin.getEconomy().withdrawPlayer(player, precio);
        player.getInventory().addItem(new ItemStack(item.getType()));
        plugin.getMessageManager().sendWithPrefix(player,
            "&a✔ &7Compra exitosa. &e-$" + precio);
        player.playSound(player.getLocation(),
            org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        plugin.getLogManager().log(npc.getId(),
            player.getName() + " compro " + item.getType().name() + " por $" + precio);
    }

    private void handleInteraccionClick(Player player, int slot, ItemStack item) {
        String title = ChatColor.stripColor(player.getOpenInventory().getTitle());
        NPCEntity npc = null;
        for (NPCEntity n : plugin.getNPCManager().getAllNPCs()) {
            if (title.contains(n.getNombre())) { npc = n; break; }
        }
        if (npc == null) return;
        final NPCEntity npcFinal = npc;
        String key = getInteraccionKey(slot);
        if (key == null) {
            if (slot == 28) {
                player.closeInventory();
                plugin.getListeners().getChatListener().awaitInput(player,
                    "Nombre de la pareja:", nombre -> {
                        plugin.getListeners().getChatListener().awaitInput(player,
                            "Skin de la pareja (nombre de jugador):", skin -> {
                                plugin.getFamiliaManager().crearEsposa(npcFinal, nombre, skin);
                                plugin.getMessageManager().sendWithPrefix(player,
                                    "&d✦ &7Pareja creada: &d" + nombre);
                                new InteraccionFamiliaGUI(plugin, npcFinal).open(player);
                            });
                    });
            }
            if (slot == 30 && npcFinal.getFamiliaEsposaId() != -1) {
                player.closeInventory();
                plugin.getListeners().getChatListener().awaitInput(player,
                    "Nombre del hijo:", nombre -> {
                        plugin.getListeners().getChatListener().awaitInput(player,
                            "Skin del hijo:", skin -> {
                                NPCEntity esposa = plugin.getNPCManager()
                                    .getNPC(npcFinal.getFamiliaEsposaId());
                                plugin.getFamiliaManager().crearHijo(npcFinal, esposa, nombre, skin);
                                plugin.getMessageManager().sendWithPrefix(player,
                                    "&e✦ &7Hijo creado: &e" + nombre);
                                new InteraccionFamiliaGUI(plugin, npcFinal).open(player);
                            });
                    });
            }
            return;
        }
        org.bukkit.configuration.file.FileConfiguration config =
            plugin.getDataManager().getNPCConfig(npc.getId());
        boolean activo = config.getBoolean("interacciones." + key, false);
        config.set("interacciones." + key, !activo);
        plugin.getDataManager().saveNPCConfig(npc.getId(), config);
        plugin.getMessageManager().sendWithPrefix(player,
            "&7Interaccion &e" + key + " &7: " + (!activo ? "&aACTIVADA" : "&cDESACTIVADA"));
        new InteraccionFamiliaGUI(plugin, npc).open(player);
    }

    private void handleConstruirClick(Player player, int slot, ItemStack item, String title) {
        NPCEntity npc = null;
        for (NPCEntity n : plugin.getNPCManager().getAllNPCs()) {
            if (title.contains(n.getNombre())) { npc = n; break; }
        }
        if (npc == null) return;
        final NPCEntity npcFinal = npc;
        switch (slot) {
            case 20 -> {
                npcFinal.setLocation(player.getLocation());
                npcFinal.saveToConfig();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7Punto de inicio definido.");
                new ConstruirGUI(plugin, npcFinal).open(player);
            }
            case 31 -> {
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7Construccion iniciada.");
                plugin.getLogManager().log(npcFinal.getId(),
                    npcFinal.getNombre() + " inicio construccion.");
                new ConstruirGUI(plugin, npcFinal).open(player);
            }
            case 32 -> {
                plugin.getMessageManager().sendWithPrefix(player,
                    "&e⚠ &7Construccion pausada.");
                new ConstruirGUI(plugin, npcFinal).open(player);
            }
            case 33 -> {
                plugin.getMessageManager().sendWithPrefix(player,
                    "&c✖ &7Construccion cancelada.");
                new ConstruirGUI(plugin, npcFinal).open(player);
            }
        }
    }

    private String getProfesionFromSlot(int slot) {
        return switch (slot) {
            case 10 -> "POLICIA"; case 11 -> "GUARDIA";
            case 12 -> "CUIDADOR_OVEJAS"; case 13 -> "CUIDADOR_ANIMALES";
            case 14 -> "FARMEADOR"; case 15 -> "COSECHADOR";
            case 16 -> "MINERO"; case 19 -> "DEFENSOR";
            case 20 -> "TALADOR"; case 21 -> "CONSTRUCTOR";
            case 22 -> "PESCADOR"; case 23 -> "GUARDABOSQUES";
            case 24 -> "HERRERO"; case 25 -> "MEDICO";
            case 28 -> "TRANSPORTISTA"; case 29 -> "MAGO";
            case 30 -> "GUARDADOR_INVENTARIO"; case 31 -> "AMIGO";
            case 32 -> "CONGELADOR"; case 33 -> "CURANDERO";
            case 34 -> "ENVENENADOR"; case 35 -> "INVOCADOR";
            case 36 -> "COMERCIANTE_AMBULANTE"; case 37 -> "ESPIA";
            case 38 -> "CARCELERO"; case 39 -> "ENTRENADOR";
            case 40 -> "SEGUIDOR"; case 41 -> "COMBATIENTE";
            case 42 -> "PAREJA"; case 43 -> "HIJO";
            case 44 -> "ESCLAVO";
            default -> null;
        };
    }

    private String getModoFromSlot(int slot) {
        return switch (slot) {
            case 10 -> "ESTATICO"; case 12 -> "VIDA_PROPIA";
            case 14 -> "COMBATE"; case 16 -> "COMERCIANTE";
            case 19 -> "GUARDAESPALDAS"; case 21 -> "DECORATIVO";
            case 23 -> "JEFE"; case 25 -> "EVENTO";
            default -> null;
        };
    }

    private String getInteraccionKey(int slot) {
        return switch (slot) {
            case 10 -> "abrazo"; case 11 -> "beso";
            case 12 -> "jugar_hijo"; case 13 -> "comer_juntos";
            case 14 -> "ensenar"; case 15 -> "hijo_corre";
            case 16 -> "paseo"; case 19 -> "proteger_hijo";
            default -> null;
        };
    }

    private int extractNPCId(String title) {
        try {
            int start = title.indexOf("#") + 1;
            int end = title.indexOf(" ", start);
            if (end == -1) end = title.length();
            return Integer.parseInt(title.substring(start, end).trim());
        } catch (Exception e) { return -1; }
    }

    private int getCurrentPage(Player player) {
        return 0;
    }

    private double extractPrice(ItemStack item) {
        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) return 0;
        for (String lore : item.getItemMeta().getLore()) {
            String stripped = ChatColor.stripColor(lore);
            if (stripped.startsWith("Precio: $")) {
                try {
                    return Double.parseDouble(stripped.replace("Precio: $", "").trim());
                } catch (Exception ignored) {}
            }
        }
        return 0;
    }
}
