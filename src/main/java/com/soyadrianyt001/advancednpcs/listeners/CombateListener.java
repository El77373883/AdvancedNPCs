package com.soyadrianyt001.advancednpcs.listeners;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombateListener implements Listener {

    private final AdvancedNPCS plugin;

    public CombateListener(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
            if (!npc.isNearby(player, 3)) continue;
            event.setCancelled(true);
            handleNPCDamage(npc, player, event.getDamage());
            return;
        }
    }

    private void handleNPCDamage(NPCEntity npc, Player attacker, double damage) {
        double newVida = npc.getVidaActual() - damage;
        if (newVida <= 0) {
            npc.setVidaActual(0);
            handleNPCDeath(npc, attacker);
        } else {
            npc.setVidaActual(newVida);
            npc.saveToConfig();
            plugin.getPacketManager().updateNameTag(npc);
            plugin.getLogManager().log(npc.getId(),
                npc.getNombre() + " recibio " + damage + " de dano de " + attacker.getName());
            if (npc.isCombateActivo()) {
                plugin.getPoliciaManager().handleNPCAttacked(npc, attacker);
            }
            if (npc.getFamiliaEsposaId() != -1 || npc.getFamiliaPadreId() != -1) {
                plugin.getFamiliaManager().handleFamiliaAttacked(npc, attacker);
            }
        }
    }

    private void handleNPCDeath(NPCEntity npc, Player killer) {
        plugin.getLogManager().log(npc.getId(),
            npc.getNombre() + " murio. Matado por: " + killer.getName());
        plugin.getParticulasManager().spawnBurstEffect(
            npc.getLocation(), Particle.EXPLOSION, 5);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            npc.setVidaActual(npc.getVidaMaxima());
            npc.saveToConfig();
            npc.respawn();
        }, 600L);
    }
}
