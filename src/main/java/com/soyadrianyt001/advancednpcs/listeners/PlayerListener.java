package com.soyadrianyt001.advancednpcs.listeners;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {

    private final AdvancedNPCS plugin;

    public PlayerListener(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            spawnNPCsForPlayer(player);
            plugin.getVersionChecker().notifyPlayer(player);
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getDataManager().savePlayerData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String worldAnterior = event.getFrom().getName();
        String worldNuevo = player.getWorld().getName();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
                if (npc.getMundo() == null) continue;
                if (npc.getMundo().equals(worldAnterior)) {
                    plugin.getPacketManager().despawnNPCForPlayer(npc, player);
                }
                if (npc.getMundo().equals(worldNuevo)) {
                    plugin.getPacketManager().spawnNPCForPlayer(npc, player);
                }
            }
        }, 20L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
        for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
            if (npc.getMundo() == null) continue;
            if (!npc.getMundo().equals(player.getWorld().getName())) continue;
            if (npc.isSeguirActivo()) {
                plugin.getFamiliaManager().handleFollow(npc, player);
            }
        }
    }

    private void spawnNPCsForPlayer(Player player) {
        String worldName = player.getWorld().getName();
        int count = 0;
        for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
            if (npc.getMundo() == null) continue;
            if (npc.getMundo().equals(worldName)) {
                plugin.getPacketManager().spawnNPCForPlayer(npc, player);
                count++;
            }
        }
        if (count > 0) {
            plugin.getLogger().info("Spawneados " + count +
                " NPCs para " + player.getName() +
                " en mundo " + worldName);
        }
    }
}
