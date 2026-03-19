package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PacketManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, String> npcFancyIds;
    private final Map<Integer, Entity> spawnedEntities;
    private final Map<Integer, ArmorStand> holoEntities;
    private final File entityUUIDFile;
    private FileConfiguration entityUUIDConfig;

    public PacketManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.npcFancyIds = new HashMap<>();
        this.spawnedEntities = new HashMap<>();
        this.holoEntities = new HashMap<>();
        this.entityUUIDFile = new File(plugin.getDataFolder(), "entity_uuids.yml");
        loadEntityUUIDs();
        cleanupOldEntities();
    }

    private void loadEntityUUIDs() {
        if (!entityUUIDFile.exists()) {
            try { entityUUIDFile.createNewFile(); }
            catch (IOException e) { plugin.getLogger().warning("Error creando entity_uuids.yml"); }
        }
        entityUUIDConfig = YamlConfiguration.loadConfiguration(entityUUIDFile);
    }

    private void saveNPCId(int npcId, String fancyId) {
        entityUUIDConfig.set("fancy." + npcId, fancyId);
        try { entityUUIDConfig.save(entityUUIDFile); }
        catch (IOException e) { plugin.getLogger().warning("Error guardando IDs."); }
    }

    private void removeNPCId(int npcId) {
        entityUUIDConfig.set("fancy." + npcId, null);
        try { entityUUIDConfig.save(entityUUIDFile); }
        catch (IOException e) { plugin.getLogger().warning("Error removiendo ID."); }
    }

    public void cleanupOldEntities() {
        plugin.getLogger().info("Limpiando entidades antiguas de AdvancedNPCS...");
        int count = 0;
        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            for (Entity entity : new ArrayList<>(world.getEntities())) {
                if (entity.hasMetadata("advancednpc") ||
                    entity.hasMetadata("advancednpc_holo")) {
                    entity.remove();
                    count++;
                }
            }
        }
        plugin.getLogger().info("Eliminadas " + count + " entidades antiguas.");
    }

    public void spawnNPC(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        despawnNPC(npc);
        String tipo = npc.getTipo().toUpperCase();
        if (tipo.equals("PLAYER")) {
            spawnFancyNPC(npc, loc);
        } else {
            spawnMobNPC(npc, loc);
        }
    }

    private void spawnFancyNPC(NPCEntity npc, Location loc) {
        try {
            String fancyId = "advancednpc_" + npc.getId();
            NpcData data = new NpcData(fancyId, UUID.randomUUID(), loc);
            data.setSkin(npc.getSkin());
            data.setDisplayName(plugin.getMessageManager().color(
                "&b" + npc.getNombre()));
            data.setShowInTab(false);
            data.setTurnToPlayer(true);
            data.setGlowing(false);
            Npc fancyNpc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
            FancyNpcsPlugin.get().getNpcManager().registerNpc(fancyNpc);
            fancyNpc.create();
            fancyNpc.spawnForAll();
            npcFancyIds.put(npc.getId(), fancyId);
            saveNPCId(npc.getId(), fancyId);
            spawnHologram(npc, loc);
            plugin.getLogger().info("NPC creado con FancyNPCs: " + fancyId);
        } catch (Exception e) {
            plugin.getLogger().warning("Error creando FancyNPC: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Npc getFancyNpc(String fancyId) {
        try {
            return FancyNpcsPlugin.get().getNpcManager().getNpc(fancyId);
        } catch (Exception e) {
            return null;
        }
    }

    private void spawnMobNPC(NPCEntity npc, Location loc) {
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
                case "PHANTOM" -> loc.getWorld().spawn(loc, Phantom.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "SPIDER" -> loc.getWorld().spawn(loc, Spider.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "DROWNED" -> loc.getWorld().spawn(loc, Drowned.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "HUSK" -> loc.getWorld().spawn(loc, Husk.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "PILLAGER" -> loc.getWorld().spawn(loc, Pillager.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "VINDICATOR" -> loc.getWorld().spawn(loc, Vindicator.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                case "SNOW_GOLEM" -> loc.getWorld().spawn(loc, Snowman.class, e -> {
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
                default -> loc.getWorld().spawn(loc, Zombie.class, e -> {
                    e.setBaby(false);
                    e.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
                    e.setCustomNameVisible(true);
                });
            };
        } catch (Exception e) {
            plugin.getLogger().warning("Error spawneando tipo " + tipo + ": " + e.getMessage());
            return null;
        }
    }

    private ArmorStand spawnHologram(NPCEntity npc, Location loc) {
        if (holoEntities.containsKey(npc.getId())) {
            ArmorStand old = holoEntities.get(npc.getId());
            if (old != null && !old.isDead()) old.remove();
        }
        double vidaPorcentaje = (npc.getVidaActual() / npc.getVidaMaxima()) * 100;
        String colorVida = vidaPorcentaje >= 75 ? "&a" : vidaPorcentaje >= 25 ? "&e" : "&c";
        int barraLlena = (int)(vidaPorcentaje / 10);
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            barra.append(i < barraLlena ? colorVida + "■" : "&7■");
        }
        barra.append("&7]");
        String holoText = plugin.getMessageManager().color(
            colorVida + "❤ " + (int)npc.getVidaActual() + "/" + (int)npc.getVidaMaxima() +
            " " + barra);
        double holoHeight = npc.getTipo().equalsIgnoreCase("PLAYER") ? 2.4 : 2.3;
        Location holoLoc = loc.clone().add(0, holoHeight, 0);
        ArmorStand holo = holoLoc.getWorld().spawn(holoLoc, ArmorStand.class, e -> {
            e.setCustomName(holoText);
            e.setCustomNameVisible(true);
            e.setVisible(false);
            e.setGravity(false);
            e.setInvulnerable(true);
            e.setSmall(true);
            e.setMarker(true);
            e.setMetadata("advancednpc_holo",
                new FixedMetadataValue(plugin, npc.getId()));
        });
        holoEntities.put(npc.getId(), holo);
        return holo;
    }

    public void despawnNPC(NPCEntity npc) {
        String fancyId = npcFancyIds.remove(npc.getId());
        if (fancyId != null) {
            try {
                Npc fancyNpc = getFancyNpc(fancyId);
                if (fancyNpc != null) {
                    fancyNpc.removeForAll();
                    FancyNpcsPlugin.get().getNpcManager().removeNpc(fancyNpc);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error removiendo FancyNPC: " + e.getMessage());
            }
        }
        Entity entity = spawnedEntities.remove(npc.getId());
        if (entity != null && !entity.isDead()) entity.remove();
        ArmorStand holo = holoEntities.remove(npc.getId());
        if (holo != null && !holo.isDead()) holo.remove();
        removeNPCId(npc.getId());
    }

    public void despawnNPCForPlayer(NPCEntity npc, Player player) {
        String fancyId = npcFancyIds.get(npc.getId());
        if (fancyId != null) {
            try {
                Npc fancyNpc = getFancyNpc(fancyId);
                if (fancyNpc != null) fancyNpc.removeForAll();
            } catch (Exception e) {
                plugin.getLogger().warning("Error removiendo FancyNPC: " + e.getMessage());
            }
        }
    }

    public void despawnAll() {
        plugin.getLogger().info("Eliminando todas las entidades de AdvancedNPCS...");
        for (String fancyId : new ArrayList<>(npcFancyIds.values())) {
            try {
                Npc fancyNpc = getFancyNpc(fancyId);
                if (fancyNpc != null) {
                    fancyNpc.removeForAll();
                    FancyNpcsPlugin.get().getNpcManager().removeNpc(fancyNpc);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error removiendo FancyNPC: " + e.getMessage());
            }
        }
        npcFancyIds.clear();
        for (Entity entity : new ArrayList<>(spawnedEntities.values())) {
            if (entity != null && !entity.isDead()) entity.remove();
        }
        spawnedEntities.clear();
        for (ArmorStand holo : new ArrayList<>(holoEntities.values())) {
            if (holo != null && !holo.isDead()) holo.remove();
        }
        holoEntities.clear();
        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            for (Entity entity : new ArrayList<>(world.getEntities())) {
                if (entity.hasMetadata("advancednpc") ||
                    entity.hasMetadata("advancednpc_holo")) {
                    entity.remove();
                }
            }
        }
        plugin.getLogger().info("Todas las entidades eliminadas correctamente.");
    }

    public void spawnNPCForPlayer(NPCEntity npc, Player player) {
        Location loc = npc.getLocation();
        if (loc == null) return;
        if (!player.getWorld().getName().equals(npc.getMundo())) return;
        String tipo = npc.getTipo().toUpperCase();
        if (tipo.equals("PLAYER")) {
            String fancyId = npcFancyIds.get(npc.getId());
            if (fancyId != null) {
                try {
                    Npc fancyNpc = getFancyNpc(fancyId);
                    if (fancyNpc != null) fancyNpc.spawnForAll();
                } catch (Exception e) {
                    plugin.getLogger().warning("Error spawneando FancyNPC: " + e.getMessage());
                }
            } else {
                spawnFancyNPC(npc, loc);
            }
        } else {
            if (!spawnedEntities.containsKey(npc.getId())) {
                spawnMobNPC(npc, loc);
            }
        }
    }

    public void updateNameTag(NPCEntity npc) {
        String fancyId = npcFancyIds.get(npc.getId());
        if (fancyId != null) {
            try {
                Npc fancyNpc = getFancyNpc(fancyId);
                if (fancyNpc != null) {
                    fancyNpc.getData().setDisplayName(
                        plugin.getMessageManager().color("&b" + npc.getNombre()));
                    fancyNpc.updateForAll();
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error actualizando FancyNPC: " + e.getMessage());
            }
        }
        Entity entity = spawnedEntities.get(npc.getId());
        if (entity != null) {
            entity.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
        }
        Location loc = npc.getLocation();
        if (loc != null) spawnHologram(npc, loc);
    }

    public void updateNameTagForPlayer(NPCEntity npc, Player player) {
        updateNameTag(npc);
    }

    public void moveNPC(NPCEntity npc, Location newLoc) {
        String fancyId = npcFancyIds.get(npc.getId());
        if (fancyId != null) {
            try {
                Npc fancyNpc = getFancyNpc(fancyId);
                if (fancyNpc != null) {
                    fancyNpc.getData().setLocation(newLoc);
                    fancyNpc.updateForAll();
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error moviendo FancyNPC: " + e.getMessage());
            }
        }
        Entity entity = spawnedEntities.get(npc.getId());
        if (entity != null && !entity.isDead()) entity.teleport(newLoc);
        ArmorStand holo = holoEntities.get(npc.getId());
        if (holo != null && !holo.isDead()) {
            double holoHeight = npc.getTipo().equalsIgnoreCase("PLAYER") ? 2.4 : 2.3;
            holo.teleport(newLoc.clone().add(0, holoHeight, 0));
        }
        npc.setLocation(newLoc);
    }

    public Entity getEntity(int npcId) {
        return spawnedEntities.get(npcId);
    }
}
