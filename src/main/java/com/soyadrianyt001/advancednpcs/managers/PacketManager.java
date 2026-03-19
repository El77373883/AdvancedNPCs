package com.soyadrianyt001.advancednpcs.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PacketManager {

    private final AdvancedNPCS plugin;
    private final ProtocolManager protocolManager;
    private final Map<Integer, UUID> npcUUIDs;
    private final Map<Integer, Integer> npcEntityIds;
    private int entityIdCounter = 2000;

    public PacketManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.npcUUIDs = new HashMap<>();
        this.npcEntityIds = new HashMap<>();
    }

    public void spawnNPC(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        if (!npcUUIDs.containsKey(npc.getId())) {
            npcUUIDs.put(npc.getId(), UUID.randomUUID());
            npcEntityIds.put(npc.getId(), entityIdCounter++);
        }
        for (Player player : loc.getWorld().getPlayers()) {
            spawnNPCForPlayer(npc, player);
        }
    }

    public void spawnNPCForPlayer(NPCEntity npc, Player player) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        if (!player.getWorld().equals(loc.getWorld())) return;

        UUID uuid = npcUUIDs.computeIfAbsent(npc.getId(), k -> UUID.randomUUID());
        int entityId = npcEntityIds.computeIfAbsent(npc.getId(), k -> entityIdCounter++);

        try {
            WrappedGameProfile profile = new WrappedGameProfile(uuid, npc.getNombre().length() > 16
                ? npc.getNombre().substring(0, 16) : npc.getNombre());

            PacketContainer addPlayer = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            addPlayer.getPlayerInfoActions().write(0,
                EnumSet.of(PlayerInfoAction.ADD_PLAYER, PlayerInfoAction.UPDATE_LISTED));
            List<PlayerInfoData> playerInfoData = new ArrayList<>();
            playerInfoData.add(new PlayerInfoData(
                uuid, 0, false,
                EnumWrappers.NativeGameMode.SURVIVAL,
                profile, null, null));
            addPlayer.getPlayerInfoDataLists().write(1, playerInfoData);
            protocolManager.sendServerPacket(player, addPlayer);

            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
            spawnPacket.getIntegers().write(0, entityId);
            spawnPacket.getUUIDs().write(0, uuid);
            spawnPacket.getDoubles().write(0, loc.getX());
            spawnPacket.getDoubles().write(1, loc.getY());
            spawnPacket.getDoubles().write(2, loc.getZ());
            spawnPacket.getBytes().write(0, (byte)(loc.getYaw() * 256.0F / 360.0F));
            spawnPacket.getBytes().write(1, (byte)(loc.getPitch() * 256.0F / 360.0F));
            protocolManager.sendServerPacket(player, spawnPacket);

            PacketContainer rotHead = protocolManager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            rotHead.getIntegers().write(0, entityId);
            rotHead.getBytes().write(0, (byte)(loc.getYaw() * 256.0F / 360.0F));
            protocolManager.sendServerPacket(player, rotHead);

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        PacketContainer removeInfo = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
                        removeInfo.getUUIDLists().write(0, Collections.singletonList(uuid));
                        protocolManager.sendServerPacket(player, removeInfo);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error removiendo info del NPC: " + e.getMessage());
                    }
                }
            }.runTaskLater(plugin, 20L);

            updateNameTagForPlayer(npc, player);

        } catch (Exception e) {
            plugin.getLogger().warning("Error spawneando NPC " + npc.getId() + ": " + e.getMessage());
        }
    }

    public void despawnNPC(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        for (Player player : loc.getWorld().getPlayers()) {
            despawnNPCForPlayer(npc, player);
        }
    }

    public void despawnNPCForPlayer(NPCEntity npc, Player player) {
        Integer entityId = npcEntityIds.get(npc.getId());
        if (entityId == null) return;
        try {
            PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyPacket.getIntLists().write(0, Collections.singletonList(entityId));
            protocolManager.sendServerPacket(player, destroyPacket);
        } catch (Exception e) {
            plugin.getLogger().warning("Error despawneando NPC " + npc.getId() + ": " + e.getMessage());
        }
    }

    public void updateNameTag(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        for (Player player : loc.getWorld().getPlayers()) {
            updateNameTagForPlayer(npc, player);
        }
    }

    public void updateNameTagForPlayer(NPCEntity npc, Player player) {
        double vidaPorcentaje = (npc.getVidaActual() / npc.getVidaMaxima()) * 100;
        String colorVida = vidaPorcentaje >= 75 ? "&a" : vidaPorcentaje >= 25 ? "&e" : "&c";
        int barraLlena = (int)(vidaPorcentaje / 10);
        StringBuilder barra = new StringBuilder(colorVida + "[");
        for (int i = 0; i < 10; i++) {
            barra.append(i < barraLlena ? "■" : "&7■");
        }
        barra.append(colorVida + "]");

        String relacionInfo = "";
        if (npc.getFamiliaEsposaId() != -1) {
            NPCEntity esposa = plugin.getNPCManager().getNPC(npc.getFamiliaEsposaId());
            if (esposa != null) relacionInfo = "\n&d❤ &7Pareja de &d" + esposa.getNombre();
        }
        if (npc.isEsHijo()) {
            relacionInfo = "\n&e👶 &7Hijo de familia";
        }

        String nameTag = plugin.getMessageManager().color(
            "&b" + npc.getNombre() + "\n" +
            colorVida + "❤ &f" + (int)npc.getVidaActual() + "&8/&f" + (int)npc.getVidaMaxima() + "\n" +
            barra + relacionInfo + "\n" +
            "&5✦ &7Creado por &bsoyadrianyt001");

        Integer entityId = npcEntityIds.get(npc.getId());
        if (entityId == null) return;

        try {
            PacketContainer metadata = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            metadata.getIntegers().write(0, entityId);
            player.sendMessage(plugin.getMessageManager().color(nameTag));
        } catch (Exception e) {
            plugin.getLogger().warning("Error actualizando nametag NPC " + npc.getId());
        }
    }

    public void moveNPC(NPCEntity npc, Location newLoc) {
        despawnNPC(npc);
        npc.setLocation(newLoc);
        spawnNPC(npc);
    }

    public int getEntityId(int npcId) {
        return npcEntityIds.getOrDefault(npcId, -1);
    }

    public UUID getNPCUUID(int npcId) {
        return npcUUIDs.get(npcId);
    }
}
