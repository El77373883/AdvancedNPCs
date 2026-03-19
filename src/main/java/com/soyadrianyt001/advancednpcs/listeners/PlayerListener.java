package com.soyadrianyt001.advancednpcs.listeners;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

public class PlayerListener implements Listener {

    private final AdvancedNPCS plugin;

    public PlayerListener(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
                if (npc.isNearby(player, 50)) {
                    plugin.getPacketManager().spawnNPCForPlayer(npc, player);
                }
            }
            plugin.getVersionChecker().notifyPlayer(player);
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getDataManager().savePlayerData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
            if (!npc.getMundo().equals(player.getWorld().getName())) continue;
            if (npc.isSeguirActivo()) {
                plugin.getFamiliaManager().handleFollow(npc, player);
            }
            if (npc.isNearby(player, 5)) {
                plugin.getPacketManager().updateNameTagForPlayer(npc, player);
            }
        }
    }
}
