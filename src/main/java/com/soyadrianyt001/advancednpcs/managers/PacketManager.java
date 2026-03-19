package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class PacketManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, Location> npcLocations;

    public PacketManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.npcLocations = new HashMap<>();
    }

    public void spawnNPC(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        npcLocations.put(npc.getId(), loc);
        for (Player player : loc.getWorld().getPlayers()) {
            spawnNPCForPlayer(npc, player);
        }
    }

    public void spawnNPCForPlayer(NPCEntity npc, Player player) {
        try {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            if (!player.getWorld().equals(loc.getWorld())) return;
            org.bukkit.entity.ArmorStand stand = loc.getWorld().spawn(loc, org.bukkit.entity.ArmorStand.class, entity -> {
                entity.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                entity.setCustomNameVisible(true);
                entity.setVisible(false);
                entity.setGravity(false);
                entity.setInvulnerable(true);
                entity.setSmall(npc.getEscala() < 0.8);
                entity.setPersistent(false);
            });
            npcLocations.put(npc.getId(), loc);
        } catch (Exception e) {
            plugin.getLogger().warning("Error spawneando NPC " + npc.getId() + ": " + e.getMessage());
        }
    }

    public void despawnNPC(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        loc.getWorld().getEntities().stream()
            .filter(e -> e instanceof org.bukkit.entity.ArmorStand)
            .filter(e -> e.getCustomName() != null &&
                e.getCustomName().contains(npc.getNombre()))
            .forEach(org.bukkit.entity.Entity::remove);
        npcLocations.remove(npc.getId());
    }

    public void despawnNPCForPlayer(NPCEntity npc, Player player) {
        despawnNPC(npc);
    }

    public void updateNameTag(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        double vidaPorcentaje = (npc.getVidaActual() / npc.getVidaMaxima()) * 100;
        String colorVida = vidaPorcentaje >= 75 ? "&a" : vidaPorcentaje >= 25 ? "&e" : "&c";
        int barraLlena = (int)(vidaPorcentaje / 10);
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            barra.append(i < barraLlena ? colorVida + "■" : "&7■");
        }
        barra.append("&7]");
        String nameTag = plugin.getMessageManager().color(
            "&b" + npc.getNombre() + "\n" +
            "&c❤ &f" + (int)npc.getVidaActual() + "&8/&f" + (int)npc.getVidaMaxima() + "\n" +
            barra + "\n" +
            "&5✦ &7Creado por &bsoyadrianyt001");
        loc.getWorld().getEntities().stream()
            .filter(e -> e instanceof org.bukkit.entity.ArmorStand)
            .filter(e -> e.getCustomName() != null &&
                e.getCustomName().contains(npc.getNombre()))
            .forEach(e -> e.setCustomName(nameTag));
    }

    public void updateNameTagForPlayer(NPCEntity npc, Player player) {
        updateNameTag(npc);
    }

    public void moveNPC(NPCEntity npc, Location newLoc) {
        despawnNPC(npc);
        npc.setLocation(newLoc);
        spawnNPC(npc);
    }

    public Location getNPCLocation(int id) {
        return npcLocations.get(id);
    }
}
