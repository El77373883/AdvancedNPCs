package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import java.util.*;

public class PacketManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, Entity> spawnedEntities;
    private final Map<Integer, ArmorStand> holoEntities;

    public PacketManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.spawnedEntities = new HashMap<>();
        this.holoEntities = new HashMap<>();
    }

    public void spawnNPC(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        despawnNPC(npc);
        Entity entity = spawnEntityByType(npc, loc);
        if (entity == null) return;
        entity.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
        entity.setCustomNameVisible(true);
        entity.setMetadata("advancednpc", new FixedMetadataValue(plugin, npc.getId()));
        if (entity instanceof LivingEntity living) {
            living.setRemoveWhenFarAway(false);
            living.setAI(false);
            living.setInvulnerable(true);
        }
        spawnedEntities.put(npc.getId(), entity);
        spawnHologram(npc, loc);
    }

    private Entity spawnEntityByType(NPCEntity npc, Location loc) {
        String tipo = npc.getTipo().toUpperCase();
        try {
            return switch (tipo) {
                case "ZOMBIE" -> loc.getWorld().spawn(loc, Zombie.class, e -> {
                    e.setBaby(false);
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "SKELETON" -> loc.getWorld().spawn(loc, Skeleton.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "VILLAGER" -> loc.getWorld().spawn(loc, Villager.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "CREEPER" -> loc.getWorld().spawn(loc, Creeper.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "ENDERMAN" -> loc.getWorld().spawn(loc, Enderman.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "BLAZE" -> loc.getWorld().spawn(loc, Blaze.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "WITCH" -> loc.getWorld().spawn(loc, Witch.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "PIGLIN" -> loc.getWorld().spawn(loc, Piglin.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "IRON_GOLEM" -> loc.getWorld().spawn(loc, IronGolem.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                    e.setPlayerCreated(true);
                });
                case "WARDEN" -> loc.getWorld().spawn(loc, Warden.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                default -> loc.getWorld().spawn(loc, ArmorStand.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                    e.setVisible(true);
                    e.setGravity(false);
                    e.setInvulnerable(true);
                    e.setArms(true);
                    e.setHelmet(new ItemStack(Material.PLAYER_HEAD));
                });
            };
        } catch (Exception e) {
            plugin.getLogger().warning("Error spawneando entidad tipo " + tipo + ": " + e.getMessage());
            return loc.getWorld().spawn(loc, ArmorStand.class, stand -> {
                stand.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                stand.setCustomNameVisible(true);
                stand.setVisible(true);
                stand.setGravity(false);
                stand.setInvulnerable(true);
                stand.setArms(true);
            });
        }
    }

    private void spawnHologram(NPCEntity npc, Location loc) {
        if (holoEntities.containsKey(npc.getId())) {
            ArmorStand old = holoEntities.get(npc.getId());
            if (old != null && !old.isDead()) old.remove();
        }
        double vidaPorcentaje = (npc.getVidaActual() / npc.getVidaMaxima()) * 100;
        String colorVida = vidaPorcentaje >= 75 ? "&a" : vidaPorcentaje >= 25 ? "&e" : "&c";
        int barraLlena = (int)(vidaPorcentaje / 10);
        StringBuilder barra = new StringBuilder(colorVida + "[");
        for (int i = 0; i < 10; i++) {
            barra.append(i < barraLlena ? "■" : "&7■");
        }
        barra.append(colorVida + "]");
        String holoText = plugin.getMessageManager().color(
            colorVida + "❤ " + (int)npc.getVidaActual() + "/" + (int)npc.getVidaMaxima() +
            " " + barra +
            " &8| &5✦ &7by &bsoyadrianyt001");
        Location holoLoc = loc.clone().add(0, 2.3, 0);
        ArmorStand holo = holoLoc.getWorld().spawn(holoLoc, ArmorStand.class, e -> {
            e.setCustomName(holoText);
            e.setCustomNameVisible(true);
            e.setVisible(false);
            e.setGravity(false);
            e.setInvulnerable(true);
            e.setSmall(true);
            e.setMarker(true);
        });
        holo.setMetadata("advancednpc_holo",
            new FixedMetadataValue(plugin, npc.getId()));
        holoEntities.put(npc.getId(), holo);
    }

    public void despawnNPC(NPCEntity npc) {
        Entity entity = spawnedEntities.remove(npc.getId());
        if (entity != null && !entity.isDead()) entity.remove();
        ArmorStand holo = holoEntities.remove(npc.getId());
        if (holo != null && !holo.isDead()) holo.remove();
    }

    public void despawnNPCForPlayer(NPCEntity npc, Player player) {
        despawnNPC(npc);
    }

    public void updateNameTag(NPCEntity npc) {
        Entity entity = spawnedEntities.get(npc.getId());
        if (entity != null) {
            entity.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
        }
        spawnHologram(npc, npc.getLocation());
    }

    public void updateNameTagForPlayer(NPCEntity npc, Player player) {
        updateNameTag(npc);
    }

    public void spawnNPCForPlayer(NPCEntity npc, Player player) {
        if (!spawnedEntities.containsKey(npc.getId())) {
            spawnNPC(npc);
        }
    }

    public void moveNPC(NPCEntity npc, Location newLoc) {
        Entity entity = spawnedEntities.get(npc.getId());
        if (entity != null && !entity.isDead()) {
            entity.teleport(newLoc);
            ArmorStand holo = holoEntities.get(npc.getId());
            if (holo != null && !holo.isDead()) {
                holo.teleport(newLoc.clone().add(0, 2.3, 0));
            }
        }
        npc.setLocation(newLoc);
    }

    public Entity getEntity(int npcId) {
        return spawnedEntities.get(npcId);
    }
}

