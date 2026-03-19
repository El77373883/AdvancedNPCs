package com.soyadrianyt001.advancednpcs.gui;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

        String title = event.getView().getTitle();
        String titleStripped = ChatColor.stripColor(title);

        if (titleStripped.contains("AdvancedNPCS") && titleStripped.contains("Premium")) {
            event.setCancelled(true);
            handlePanelClick(player, event.getSlot(), event.getCurrentItem(), titleStripped);
            return;
        }
        if (titleStripped.contains("NPC #") && titleStripped.contains("Config")) {
            event.setCancelled(true);
            handleConfigClick(player, event.getSlot(), event.getCurrentItem(), titleStripped);
            return;
        }
        if (titleStripped.contains("Profesion del NPC")) {
            event.setCancelled(true);
            handleProfesionClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (titleStripped.contains("Modo del NPC")) {
            event.setCancelled(true);
            handleModoClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (title.contains("⚠") || titleStripped.contains("Confirmar") ||
            titleStripped.contains("seguro") || titleStripped.contains("Eliminar NPC")) {
            event.setCancelled(true);
            handleConfirmClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (titleStripped.contains("Tienda de")) {
            event.setCancelled(true);
            handleTiendaClick(player, event.getSlot(), event.getCurrentItem(), titleStripped);
            return;
        }
        if (titleStripped.contains("Interacciones Familiares")) {
            event.setCancelled(true);
            handleInteraccionClick(player, event.getSlot(), event.getCurrentItem());
            return;
        }
        if (titleStripped.contains("Constructor")) {
            event.setCancelled(true);
            handleConstruirClick(player, event.getSlot(), event.getCurrentItem(), titleStripped);
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

    private void handlePanelClick(Player player, int slot, ItemStack item, String title) {
        if (slot == 10 && item.getType() == Material.EMERALD) {
            player.closeInventory();
            plugin.getListeners().getChatListener().awaitInput(player,
                "&7Como quieres llamar a tu NPC&8:", nombre -> {
                    NPCEntity npc = plugin.getNPCManager().createNPC(
                        nombre, player.getLocation(), player);
                    plugin.getNPCManager().spawnNPCEntity(npc);
                    plugin.getMessageManager().send(player, "npc_creado",
                        "%npc_id%", String.valueOf(npc.getId()));
                    player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    new ConfigGUI(plugin, npc).open(player);
                });
            return;
        }
        if (slot == 45 && item.getType() == Material.ARROW) {
            int page = extractPage(title);
            if (page > 0) new PanelGUI(plugin, page - 1).open(player);
            return;
        }
        if (slot == 53 && item.getType() == Material.ARROW) {
            int page = extractPage(title);
            int total = plugin.getNPCManager().getTotalNPCs();
            int totalPages = Math.max(1, (int) Math.ceil(total / 28.0));
            if (page < totalPages - 1) new PanelGUI(plugin, page + 1).open(player);
            return;
        }
        if (item.getType() == Material.PLAYER_HEAD && item.getItemMeta() != null) {
            String displayName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            NPCEntity npc = plugin.getNPCManager().getNPCByName(displayName);
            if (npc == null) {
                for (NPCEntity n : plugin.getNPCManager().getAllNPCs()) {
                    if (n.getNombre().equalsIgnoreCase(displayName.trim())) {
                        npc = n;
                        break;
                    }
                }
            }
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
                plugin.getMessageManager().sendWithPrefix(player,
                    "&7Escribe el nuevo &enombre &7del NPC en el chat&8:");
                plugin.getListeners().getChatListener().awaitInput(player,
                    "&7Nombre:", nombre -> {
                        npc.setNombre(nombre);
                        npc.saveToConfig();
                        plugin.getPacketManager().updateNameTag(npc);
                        plugin.getMessageManager().sendWithPrefix(player,
                            "&a✔ &7Nombre cambiado a &e" + nombre);
                        player.playSound(player.getLocation(),
                            org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        new ConfigGUI(plugin, npc).open(player);
                    });
            }
            case 12 -> {
                player.closeInventory();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&7Escribe el nombre del jugador para la &dskin&8:");
                plugin.getListeners().getChatListener().awaitInput(player,
                    "&7Skin:", skinName -> {
                        npc.setSkin(skinName);
                        npc.saveToConfig();
                        npc.respawn();
                        plugin.getMessageManager().sendWithPrefix(player,
                            "&a✔ &7Skin cambiada a &d" + skinName);
                        player.playSound(player.getLocation(),
                            org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        new ConfigGUI(plugin, npc).open(player);
                    });
            }
            case 13 -> new ModoGUI(plugin, npc).open(player);
            case 14 -> new ProfesionGUI(plugin, npc).open(player);
            case 15 -> {
                player.closeInventory();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&7Escribe el dialogo del NPC&8:");
                plugin.getListeners().getChatListener().awaitInput(player,
                    "&7Dialogo:", dialogo -> {
                        org.bukkit.configuration.file.FileConfiguration config =
                            plugin.getDataManager().getNPCConfig(npc.getId());
                        config.set("dialogo", dialogo);
                        plugin.getDataManager().saveNPCConfig(npc.getId(), config);
                        plugin.getMessageManager().sendWithPrefix(player,
                            "&a✔ &7Dialogo guardado.");
                        new ConfigGUI(plugin, npc).open(player);
                    });
            }
            case 16 -> {
                player.closeInventory();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&7Tipos&8: &eHEART&7, &eCRIT&7, &eSNOWFLAKE&7, &eWITCH&7, &eFLAME");
                plugin.getMessageManager().sendWithPrefix(player,
                    "&7Modo&8: &eNORMAL&7, &eAURA&7, &eCORONA&7, &eRASTRO &8- &7Ej&8: &eHEART:AURA");
                plugin.getListeners().getChatListener().awaitInput(player,
                    "&7Particula:", particulaStr -> {
                        try {
                            String[] parts = particulaStr.split(":");
                            org.bukkit.Particle.valueOf(parts[0].toUpperCase());
                            npc.getParticulas().clear();
                            npc.getParticulas().add(particulaStr.toUpperCase());
                            npc.saveToConfig();
                            plugin.getParticulasManager().stopParticles(npc);
                            plugin.getParticulasManager().startParticles(npc);
                            plugin.getMessageManager().sendWithPrefix(player,
                                "&a✔ &7Particula activada&8: &e" +
                                particulaStr.toUpperCase());
                            player.playSound(player.getLocation(),
                                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        } catch (Exception e) {
                            plugin.getMessageManager().sendWithPrefix(player,
                                "&cParticula invalida. Ej: HEART, FLAME, CRIT");
                        }
                        new ConfigGUI(plugin, npc).open(player);
                    });
            }
            case 20 -> {
                player.closeInventory();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&7Escribe la escala &8(0.1 - 5.0)&8:");
                plugin.getListeners().getChatListener().awaitInput(player,
                    "&7Escala:", escalaStr -> {
                        try {
                            double escala = Double.parseDouble(escalaStr);
                            escala = Math.max(0.1, Math.min(5.0, escala));
                            npc.setEscala(escala);
                            npc.saveToConfig();
                            npc.respawn();
                            plugin.getMessageManager().sendWithPrefix(player,
                                "&a✔ &7Escala cambiada a &e" + escala);
                            player.playSound(player.getLocation(),
                                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        } catch (NumberFormatException e) {
                            plugin.getMessageManager().sendWithPrefix(player,
                                "&cEscala invalida. Ej: 1.0 o 2.5");
                        }
                        new ConfigGUI(plugin, npc).open(player);
                    });
            }
            case 22 -> {
                npc.setEmocion(nextEmocion(npc.getEmocion()));
                npc.saveToConfig();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7Emocion cambiada a &d" + npc.getEmocion());
                new ConfigGUI(plugin, npc).open(player);
            }
            case 23 -> {
                npc.setTiendaActiva(!npc.isTiendaActiva());
                npc.saveToConfig();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7Tienda " +
                    (npc.isTiendaActiva() ? "&aACTIVADA" : "&cDESACTIVADA"));
                new ConfigGUI(plugin, npc).open(player);
            }
            case 31 -> new ConstruirGUI(plugin, npc).open(player);
            case 32 -> new InteraccionFamiliaGUI(plugin, npc).open(player);
            case 37 -> {
                plugin.getPacketManager().moveNPC(npc, player.getLocation());
                npc.saveToConfig();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7NPC movido a tu posicion.");
                player.playSound(player.getLocation(),
                    org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                new ConfigGUI(plugin, npc).open(player);
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
                new ConfirmGUI(plugin, "Eliminar NPC #" + npcId,
                    "Se eliminara el NPC permanentemente.",
                    confirmed -> {}).open(player);
            }
            case 44 -> new PanelGUI(plugin).open(player);
        }
    }

    private void handleProfesionClick(Player player, int slot, ItemStack item) {
        NPCEntity npc = getNPCFromOpenInventory(player);
        if (npc == null) return;

        if (slot == 49) {
            new ConfigGUI(plugin, npc).open(player);
            return;
        }
        if (slot == 46) {
            npc.setDormirActivo(!npc.isDormirActivo());
            npc.saveToConfig();
            plugin.getMessageManager().sendWithPrefix(player,
                "&7Dormir&8: " +
                (npc.isDormirActivo() ? "&aACTIVADO" : "&cDESACTIVADO"));
            new ProfesionGUI(plugin, npc).open(player);
            return;
        }
        if (slot == 47) {
            npc.setComerActivo(!npc.isComerActivo());
            npc.saveToConfig();
            plugin.getMessageManager().sendWithPrefix(player,
                "&7Comer&8: " +
                (npc.isComerActivo() ? "&aACTIVADO" : "&cDESACTIVADO"));
            new ProfesionGUI(plugin, npc).open(player);
            return;
        }
        if (slot == 48) {
            npc.setCaminarActivo(!npc.isCaminarActivo());
            npc.saveToConfig();
            plugin.getMessageManager().sendWithPrefix(player,
                "&7Caminar&8: " +
                (npc.isCaminarActivo() ? "&aACTIVADO" : "&cDESACTIVADO"));
            if (npc.isCaminarActivo()) {
                plugin.getTrabajoManager().startCaminarNatural(npc);
            } else {
                plugin.getTrabajoManager().stopTrabajo(npc);
            }
            new ProfesionGUI(plugin, npc).open(player);
            return;
        }
        if (slot == 50) {
            npc.setProfesion("NINGUNA");
            npc.saveToConfig();
            plugin.getTrabajoManager().stopTrabajo(npc);
            plugin.getMessageManager().sendWithPrefix(player,
                "&a✔ &7Profesion removida.");
            new ProfesionGUI(plugin, npc).open(player);
            return;
        }

        String profesion = getProfesionFromSlot(slot);
        if (profesion == null) return;

        npc.setProfesion(profesion);
        npc.saveToConfig();
        plugin.getMessageManager().sendWithPrefix(player,
            "&a✔ &7Profesion cambiada a &6" + profesion);
        player.playSound(player.getLocation(),
            org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        plugin.getTrabajoManager().startTrabajo(npc);
        new ProfesionGUI(plugin, npc).open(player);
    }

    private void handleModoClick(Player player, int slot, ItemStack item) {
        NPCEntity npc = getNPCFromOpenInventory(player);
        if (npc == null) return;

        if (slot == 49) {
            new ConfigGUI(plugin, npc).open(player);
            return;
        }

        String modo = getModoFromSlot(slot);
        if (modo == null) return;

        npc.setModo(modo);
        npc.saveToConfig();
        plugin.getMessageManager().sendWithPrefix(player,
            "&a✔ &7Modo cambiado a &b" + modo);
        player.playSound(player.getLocation(),
            org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        plugin.getTrabajoManager().stopTrabajo(npc);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            switch (modo) {
                case "VIDA_PROPIA" ->
                    plugin.getTrabajoManager().startCaminarNatural(npc);
                case "GUARDAESPALDAS", "COMBATE" ->
                    plugin.getTrabajoManager().startTrabajo(npc);
                case "ESTATICO", "DECORATIVO",
                     "COMERCIANTE", "JEFE", "EVENTO" -> {}
            }
        }, 5L);

        new ConfigGUI(plugin, npc).open(player);
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
        NPCEntity npc = getNPCFromOpenInventory(player);
        if (npc == null) return;

        String key = getInteraccionKey(slot);
        if (key != null) {
            org.bukkit.configuration.file.FileConfiguration config =
                plugin.getDataManager().getNPCConfig(npc.getId());
            boolean activo = config.getBoolean("interacciones." + key, false);
            config.set("interacciones." + key, !activo);
            plugin.getDataManager().saveNPCConfig(npc.getId(), config);
            plugin.getMessageManager().sendWithPrefix(player,
                "&7Interaccion &e" + key + "&7: " +
                (!activo ? "&aACTIVADA" : "&cDESACTIVADA"));
            new InteraccionFamiliaGUI(plugin, npc).open(player);
            return;
        }

        final NPCEntity npcFinal = npc;
        if (slot == 28) {
            player.closeInventory();
            plugin.getListeners().getChatListener().awaitInput(player,
                "&7Nombre de la pareja&8:", nombre -> {
                    plugin.getListeners().getChatListener().awaitInput(player,
                        "&7Skin de la pareja&8:", skin -> {
                            plugin.getFamiliaManager().crearEsposa(npcFinal, nombre, skin);
                            plugin.getMessageManager().sendWithPrefix(player,
                                "&d✦ &7Pareja creada&8: &d" + nombre);
                            new InteraccionFamiliaGUI(plugin, npcFinal).open(player);
                        });
                });
        } else if (slot == 30 && npcFinal.getFamiliaEsposaId() != -1) {
            player.closeInventory();
            plugin.getListeners().getChatListener().awaitInput(player,
                "&7Nombre del hijo&8:", nombre -> {
                    plugin.getListeners().getChatListener().awaitInput(player,
                        "&7Skin del hijo&8:", skin -> {
                            NPCEntity esposa = plugin.getNPCManager()
                                .getNPC(npcFinal.getFamiliaEsposaId());
                            plugin.getFamiliaManager().crearHijo(
                                npcFinal, esposa, nombre, skin);
                            plugin.getMessageManager().sendWithPrefix(player,
                                "&e✦ &7Hijo creado&8: &e" + nombre);
                            new InteraccionFamiliaGUI(plugin, npcFinal).open(player);
                        });
                });
        } else if (slot == 49) {
            new ConfigGUI(plugin, npc).open(player);
        }
    }

    private void handleConstruirClick(Player player, int slot, ItemStack item, String title) {
        NPCEntity npc = getNPCFromOpenInventory(player);
        if (npc == null) return;

        switch (slot) {
            case 20 -> {
                npc.setLocation(player.getLocation());
                npc.saveToConfig();
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7Punto de inicio definido.");
                new ConstruirGUI(plugin, npc).open(player);
            }
            case 31 -> {
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7Construccion iniciada.");
                new ConstruirGUI(plugin, npc).open(player);
            }
            case 32 -> {
                plugin.getMessageManager().sendWithPrefix(player,
                    "&e⚠ &7Construccion pausada.");
                new ConstruirGUI(plugin, npc).open(player);
            }
            case 33 -> {
                plugin.getMessageManager().sendWithPrefix(player,
                    "&c✖ &7Construccion cancelada.");
                new ConstruirGUI(plugin, npc).open(player);
            }
            case 49 -> new ConfigGUI(plugin, npc).open(player);
        }
    }

    private NPCEntity getNPCFromOpenInventory(Player player) {
        String title = ChatColor.stripColor(player.getOpenInventory().getTitle());
        int id = extractNPCId(title);
        if (id != -1) {
            NPCEntity npc = plugin.getNPCManager().getNPC(id);
            if (npc != null) return npc;
        }
        for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
            if (title.contains(npc.getNombre())) return npc;
        }
        return null;
    }

    private String nextEmocion(String actual) {
        return switch (actual) {
            case "NEUTRAL" -> "FELIZ";
            case "FELIZ" -> "TRISTE";
            case "TRISTE" -> "ENOJADO";
            case "ENOJADO" -> "ASUSTADO";
            case "ASUSTADO" -> "NEUTRAL";
            default -> "NEUTRAL";
        };
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
            if (start == 0) return -1;
            int end = title.indexOf(" ", start);
            if (end == -1) end = title.length();
            return Integer.parseInt(title.substring(start, end).trim());
        } catch (Exception e) { return -1; }
    }

    private int extractPage(String title) {
        try {
            if (title.contains("Pagina")) {
                int idx = title.indexOf("Pagina") + 6;
                String num = title.substring(idx).trim().split(" ")[0];
                return Integer.parseInt(num) - 1;
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private double extractPrice(ItemStack item) {
        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) return 0;
        for (String lore : item.getItemMeta().getLore()) {
            String stripped = ChatColor.stripColor(lore);
            if (stripped.startsWith("Precio: $")) {
                try {
                    return Double.parseDouble(
                        stripped.replace("Precio: $", "").trim());
                } catch (Exception ignored) {}
            }
        }
        return 0;
    }
}
