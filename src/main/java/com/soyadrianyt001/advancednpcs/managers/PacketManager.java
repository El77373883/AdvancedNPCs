package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PacketManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, Entity> spawnedEntities;
    private final Map<Integer, ArmorStand> holoEntities;
    private final File entityUUIDFile;
    private FileConfiguration entityUUIDConfig;

    public PacketManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.spawnedEntities = new HashMap<>();
        this.holoEntities = new HashMap<>();
        this.entityUUIDFile = new File(plugin.getDataFolder(), "entity_uuids.yml");
        loadEntityUUIDs();
        cleanupOldEntities();
    }

    private void loadEntityUUIDs() {
        if (!entityUUIDFile.exists()) {
            try {
                entityUUIDFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Error creando entity_uuids.yml");
            }
        }
        entityUUIDConfig = YamlConfiguration.loadConfiguration(entityUUIDFile);
    }

    private void saveEntityUUID(int npcId, UUID entityUUID, UUID holoUUID) {
        if (entityUUID != null)
            entityUUIDConfig.set("entities." + npcId + ".entity", entityUUID.toString());
        if (holoUUID != null)
            entityUUIDConfig.set("entities." + npcId + ".holo", holoUUID.toString());
        try {
            entityUUIDConfig.save(entityUUIDFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Error guardando UUIDs de entidades.");
        }
    }

    private void removeEntityUUID(int npcId) {
        entityUUIDConfig.set("entities." + npcId, null);
        try {
            entityUUIDConfig.save(entityUUIDFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Error removiendo UUID de entidad.");
        }
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
        if (entityUUIDConfig.contains("entities")) {
            for (String key : entityUUIDConfig.getConfigurationSection("entities").getKeys(false)) {
                String entityUUIDStr = entityUUIDConfig.getString("entities." + key + ".entity");
                String holoUUIDStr = entityUUIDConfig.getString("entities." + key + ".holo");
                for (org.bukkit.World world : plugin.getServer().getWorlds()) {
                    if (entityUUIDStr != null) {
                        try {
                            UUID uuid = UUID.fromString(entityUUIDStr);
                            Entity e = world.getEntity(uuid);
                            if (e != null) { e.remove(); count++; }
                        } catch (Exception ignored) {}
                    }
                    if (holoUUIDStr != null) {
                        try {
                            UUID uuid = UUID.fromString(holoUUIDStr);
                            Entity e = world.getEntity(uuid);
                            if (e != null) { e.remove(); count++; }
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
        entityUUIDConfig.set("entities", null);
        try {
            entityUUIDConfig.save(entityUUIDFile);
        } catch (IOException ignored) {}
        plugin.getLogger().info("Eliminadas " + count + " entidades antiguas de AdvancedNPCS.");
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
        ArmorStand holo = spawnHologram(npc, loc);
        saveEntityUUID(npc.getId(), entity.getUniqueId(),
            holo != null ? holo.getUniqueId() : null);
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
        return holo;
    }

    public void despawnNPC(NPCEntity npc) {
        Entity entity = spawnedEntities.remove(npc.getId());
        if (entity != null && !entity.isDead()) entity.remove();
        ArmorStand holo = holoEntities.remove(npc.getId());
        if (holo != null && !holo.isDead()) holo.remove();
        removeEntityUUID(npc.getId());
    }

    public void despawnNPCForPlayer(NPCEntity npc, Player player) {
        despawnNPC(npc);
    }

    public void despawnAll() {
        plugin.getLogger().info("Eliminando todas las entidades de AdvancedNPCS...");
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
        entityUUIDConfig.set("entities", null);
        try {
            entityUUIDConfig.save(entityUUIDFile);
        } catch (IOException ignored) {}
        plugin.getLogger().info("Todas las entidades eliminadas correctamente.");
    }

    public void updateNameTag(NPCEntity npc) {
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
