package com.soyadrianyt001.advancednpcs.listeners;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class NPCListener implements Listener {

    private final AdvancedNPCS plugin;

    public NPCListener(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
            if (!npc.isNearby(player, 3)) continue;
            event.setCancelled(true);
            if (player.isSneaking() && player.isOp()) {
                new com.soyadrianyt001.advancednpcs.gui.ConfigGUI(plugin, npc).open(player);
                return;
            }
            handleNPCClick(player, npc);
            return;
        }
    }

    private void handleNPCClick(Player player, NPCEntity npc) {
        plugin.getLogManager().log(npc.getId(), player.getName() + " interactuo con NPC #" + npc.getId());
        switch (npc.getModo().toUpperCase()) {
            case "ESTATICO" -> {
                for (String cmd : npc.getComandosAlClick()) {
                    plugin.getServer().dispatchCommand(
                        plugin.getServer().getConsoleSender(),
                        cmd.replace("%player_name%", player.getName()));
                }
                if (npc.isTiendaActiva()) {
                    new com.soyadrianyt001.advancednpcs.gui.TiendaGUI(plugin, npc).open(player);
                }
            }
            case "COMERCIANTE" -> {
                new com.soyadrianyt001.advancednpcs.gui.TiendaGUI(plugin, npc).open(player);
            }
            case "VIDA_PROPIA" -> {
                if (npc.isInventarioActivo() && player.isSneaking()) {
                    new com.soyadrianyt001.advancednpcs.gui.InventarioGUI(plugin, npc).open(player);
                    return;
                }
                plugin.getFamiliaManager().handleDialogo(npc, player);
            }
            default -> {
                if (npc.isInventarioActivo() && player.isSneaking()) {
                    new com.soyadrianyt001.advancednpcs.gui.InventarioGUI(plugin, npc).open(player);
                }
            }
        }
    }
}
