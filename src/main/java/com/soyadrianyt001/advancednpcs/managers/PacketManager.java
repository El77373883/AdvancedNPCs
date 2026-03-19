package com.soyadrianyt001.advancednpcs.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PacketManager {

    private final AdvancedNPCS plugin;
    private final ProtocolManager protocolManager;
    private final Map<Integer, UUID> npcUUIDs;
    private final Map<Integer, Integer> npcEntityIds;
    private final Map<Integer, Entity> spawnedEntities;
    private final Map<Integer, ArmorStand> holoEntities;
    private final File entityUUIDFile;
    private FileConfiguration entityUUIDConfig;
    private int entityIdCounter = 9000;

    public PacketManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.npcUUIDs = new HashMap<>();
        this.npcEntityIds = new HashMap<>();
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

    private void saveEntityUUID(int npcId, UUID entityUUID, UUID holoUUID) {
        if (entityUUID != null)
            entityUUIDConfig.set("entities." + npcId + ".entity", entityUUID.toString());
        if (holoUUID != null)
            entityUUIDConfig.set("entities." + npcId + ".holo", holoUUID.toString());
        try { entityUUIDConfig.save(entityUUIDFile); }
        catch (IOException e) { plugin.getLogger().warning("Error guardando UUIDs."); }
    }

    private void removeEntityUUID(int npcId) {
        entityUUIDConfig.set("entities." + npcId, null);
        try { entityUUIDConfig.save(entityUUIDFile); }
        catch (IOException e) { plugin.getLogger().warning("Error removiendo UUID."); }
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
        entityUUIDConfig.set("entities", null);
        try { entityUUIDConfig.save(entityUUIDFile); }
        catch (IOException ignored) {}
        plugin.getLogger().info("Eliminadas " + count + " entidades antiguas.");
    }

    public void spawnNPC(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        despawnNPC(npc);
        String tipo = npc.getTipo().toUpperCase();
        if (tipo.equals("PLAYER")) {
            spawnPlayerNPC(npc, loc);
        } else {
            spawnMobNPC(npc, loc);
        }
    }

    private void spawnPlayerNPC(NPCEntity npc, Location loc) {
        plugin.getSkinManager().getSkinProfile(npc.getSkin(), profile -> {
            UUID uuid = UUID.randomUUID();
            int entityId = entityIdCounter++;
            npcUUIDs.put(npc.getId(), uuid);
            npcEntityIds.put(npc.getId(), entityId);
            String displayName = npc.getNombre().length() > 16
                ? npc.getNombre().substring(0, 16) : npc.getNombre();
            WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, displayName);
            if (profile != null) {
                try {
                    org.bukkit.profile.PlayerTextures textures = profile.getTextures();
                    if (textures.getSkin() != null) {
                        String skinUrl = textures.getSkin().toString();
                        String base64 = Base64.getEncoder().encodeToString(
                            ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinUrl + "\"}}}").getBytes());
                        gameProfile.getProperties().put("textures",
                            new WrappedSignedProperty("textures", base64, ""));
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Error aplicando textura: " + e.getMessage());
                }
            }
            for (org.bukkit.entity.Player player : loc.getWorld().getPlayers()) {
                sendPlayerNPCPackets(npc, player, uuid, entityId, gameProfile, loc);
            }
            ArmorStand holo = spawnHologram(npc, loc);
            saveEntityUUID(npc.getId(), uuid,
                holo != null ? holo.getUniqueId() : null);
        });
    }

    private void sendPlayerNPCPackets(NPCEntity npc, org.bukkit.entity.Player player,
                                       UUID uuid, int entityId,
                                       WrappedGameProfile gameProfile, Location loc) {
        try {
            PacketContainer addInfo = protocolManager.createPacket(
                PacketType.Play.Server.PLAYER_INFO);
            addInfo.getPlayerInfoActions().write(0,
                EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER,
                           EnumWrappers.PlayerInfoAction.UPDATE_LISTED));
            PlayerInfoData infoData = new PlayerInfoData(
                uuid, 0, false,
                EnumWrappers.NativeGameMode.SURVIVAL,
                gameProfile,
                WrappedChatComponent.fromText(npc.getNombre()),
                (WrappedRemoteChatSessionData) null);
            addInfo.getPlayerInfoDataLists().write(1,
                Collections.singletonList(infoData));
            protocolManager.sendServerPacket(player, addInfo);

            PacketContainer spawnPacket = protocolManager.createPacket(
                PacketType.Play.Server.SPAWN_ENTITY);
            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getUUIDs().write(0, uuid);
            spawnPacket.getIntegers().write(1, 128);
            spawnPacket.getIntegers().write(2, 0);
            spawnPacket.getDoubles().write(0, loc.getX());
            spawnPacket.getDoubles().write(1, loc.getY());
            spawnPacket.getDoubles().write(2, loc.getZ());
            spawnPacket.getBytes().write(0, (byte)(loc.getYaw() * 256.0F / 360.0F));
            spawnPacket.getBytes().write(1, (byte)(loc.getPitch() * 256.0F / 360.0F));
            spawnPacket.getBytes().write(2, (byte)(loc.getYaw() * 256.0F / 360.0F));
            protocolManager.sendServerPacket(player, spawnPacket);

            PacketContainer rotHead = protocolManager.createPacket(
                PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            rotHead.getIntegers().write(0, entityId);
            rotHead.getBytes().write(0, (byte)(loc.getYaw() * 256.0F / 360.0F));
            protocolManager.sendServerPacket(player, rotHead);

            WrappedDataWatcher watcher = new WrappedDataWatcher();
            WrappedDataWatcher.WrappedDataWatcherObject skinLayersObj =
                new WrappedDataWatcher.WrappedDataWatcherObject(
                    17, WrappedDataWatcher.Registry.get(Byte.class));
            watcher.setObject(skinLayersObj, (byte) 127);
            PacketContainer metadata = protocolManager.createPacket(
                PacketType.Play.Server.ENTITY_METADATA);
            metadata.getIntegers().write(0, entityId);
            metadata.getWatchableCollectionModifier().write(0,
                watcher.getWatchableObjects());
            protocolManager.sendServerPacket(player, metadata);

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        PacketContainer removeInfo = protocolManager.createPacket(
                            PacketType.Play.Server.PLAYER_INFO_REMOVE);
                        removeInfo.getUUIDLists().write(0,
                            Collections.singletonList(uuid));
                        protocolManager.sendServerPacket(player, removeInfo);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error removiendo info: " + e.getMessage());
                    }
                }
            }.runTaskLater(plugin, 60L);

        } catch (Exception e) {
            plugin.getLogger().warning("Error enviando packets NPC "
                + npc.getId() + ": " + e.getMessage());
            e.printStackTrace();
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
        Integer entityId = npcEntityIds.remove(npc.getId());
        UUID uuid = npcUUIDs.remove(npc.getId());
        if (entityId != null && uuid != null) {
            for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
                try {
                    PacketContainer destroy = protocolManager.createPacket(
                        PacketType.Play.Server.ENTITY_DESTROY);
                    destroy.getIntLists().write(0, Collections.singletonList(entityId));
                    protocolManager.sendServerPacket(player, destroy);
                } catch (Exception e) {
                    plugin.getLogger().warning("Error destruyendo NPC packet: " + e.getMessage());
                }
            }
        }
        Entity entity = spawnedEntities.remove(npc.getId());
        if (entity != null && !entity.isDead()) entity.remove();
        ArmorStand holo = holoEntities.remove(npc.getId());
        if (holo != null && !holo.isDead()) holo.remove();
        removeEntityUUID(npc.getId());
    }

    public void despawnNPCForPlayer(NPCEntity npc, org.bukkit.entity.Player player) {
        Integer entityId = npcEntityIds.get(npc.getId());
        if (entityId != null) {
            try {
                PacketContainer destroy = protocolManager.createPacket(
                    PacketType.Play.Server.ENTITY_DESTROY);
                destroy.getIntLists().write(0, Collections.singletonList(entityId));
                protocolManager.sendServerPacket(player, destroy);
            } catch (Exception e) {
                plugin.getLogger().warning("Error destruyendo NPC: " + e.getMessage());
            }
        }
        Entity entity = spawnedEntities.get(npc.getId());
        if (entity != null && !entity.isDead()) {
            entity.setMetadata("temp_hidden", new FixedMetadataValue(plugin, true));
        }
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
        npcUUIDs.clear();
        npcEntityIds.clear();
        for (org.bukkit.World world : plugin.getServer().getWorlds()) {
            for (Entity entity : new ArrayList<>(world.getEntities())) {
                if (entity.hasMetadata("advancednpc") ||
                    entity.hasMetadata("advancednpc_holo")) {
                    entity.remove();
                }
            }
        }
        entityUUIDConfig.set("entities", null);
        try { entityUUIDConfig.save(entityUUIDFile); }
        catch (IOException ignored) {}
        plugin.getLogger().info("Todas las entidades eliminadas correctamente.");
    }

    public void spawnNPCForPlayer(NPCEntity npc, org.bukkit.entity.Player player) {
        Location loc = npc.getLocation();
        if (loc == null) return;
        if (!player.getWorld().getName().equals(npc.getMundo())) return;
        String tipo = npc.getTipo().toUpperCase();
        if (tipo.equals("PLAYER")) {
            UUID uuid = npcUUIDs.get(npc.getId());
            Integer entityId = npcEntityIds.get(npc.getId());
            if (uuid != null && entityId != null) {
                String displayName = npc.getNombre().length() > 16
                    ? npc.getNombre().substring(0, 16) : npc.getNombre();
                WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, displayName);
                sendPlayerNPCPackets(npc, player, uuid, entityId, gameProfile, loc);
            } else {
                spawnPlayerNPC(npc, loc);
            }
        } else {
            if (!spawnedEntities.containsKey(npc.getId())) {
                spawnMobNPC(npc, loc);
            }
        }
    }

    public void updateNameTag(NPCEntity npc) {
        Entity entity = spawnedEntities.get(npc.getId());
        if (entity != null) {
            entity.setCustomName(plugin.getMessageManager().color("&b" + npc.getNombre()));
        }
        Location loc = npc.getLocation();
        if (loc != null) spawnHologram(npc, loc);
    }

    public void updateNameTagForPlayer(NPCEntity npc, org.bukkit.entity.Player player) {
        updateNameTag(npc);
    }

    public void moveNPC(NPCEntity npc, Location newLoc) {
        String tipo = npc.getTipo().toUpperCase();
        if (tipo.equals("PLAYER")) {
            Integer entityId = npcEntityIds.get(npc.getId());
            if (entityId != null) {
                for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
                    if (!player.getWorld().getName().equals(npc.getMundo())) continue;
                    try {
                        PacketContainer teleport = protocolManager.createPacket(
                            PacketType.Play.Server.ENTITY_TELEPORT);
                        teleport.getIntegers().write(0, entityId);
                        teleport.getDoubles().write(0, newLoc.getX());
                        teleport.getDoubles().write(1, newLoc.getY());
                        teleport.getDoubles().write(2, newLoc.getZ());
                        teleport.getBytes().write(0,
                            (byte)(newLoc.getYaw() * 256.0F / 360.0F));
                        teleport.getBytes().write(1,
                            (byte)(newLoc.getPitch() * 256.0F / 360.0F));
                        teleport.getBooleans().write(0, false);
                        protocolManager.sendServerPacket(player, teleport);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error moviendo NPC: " + e.getMessage());
                    }
                }
            }
        } else {
            Entity entity = spawnedEntities.get(npc.getId());
            if (entity != null && !entity.isDead()) {
                entity.teleport(newLoc);
            }
        }
        ArmorStand holo = holoEntities.get(npc.getId());
        if (holo != null && !holo.isDead()) {
            double holoHeight = tipo.equals("PLAYER") ? 2.4 : 2.3;
            holo.teleport(newLoc.clone().add(0, holoHeight, 0));
        }
        npc.setLocation(newLoc);
    }

    public Entity getEntity(int npcId) {
        return spawnedEntities.get(npcId);
    }
}
