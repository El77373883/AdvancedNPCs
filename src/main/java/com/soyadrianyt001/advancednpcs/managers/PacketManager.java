package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

public class PacketManager {

    private final AdvancedNPCS plugin;

    public PacketManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    public void spawnNPC(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        for (Player player : loc.getWorld().getPlayers()) {
            spawnNPCForPlayer(npc, player);
        }
    }

    public void spawnNPCForPlayer(NPCEntity npc, Player player) {
        try {
            Location loc = npc.getLocation();
            if (loc == null) return;
            UUID uuid = UUID.randomUUID();
            GameProfile profile = new GameProfile(uuid, npc.getNombre().length() > 16
                ? npc.getNombre().substring(0, 16) : npc.getNombre());
            ServerLevel level = ((CraftWorld) loc.getWorld()).getHandle();
            net.minecraft.server.MinecraftServer server =
                ((CraftServer) Bukkit.getServer()).getServer();
            ServerPlayer serverPlayer = new ServerPlayer(server, level, profile,
                net.minecraft.world.entity.player.PlayerModelPart.values()[0]);
            serverPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());
            ServerGamePacketListenerImpl conn =
                ((org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer) player).getHandle().connection;
            conn.send(new ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                serverPlayer));
            conn.send(new ClientboundAddEntityPacket(serverPlayer));
            conn.send(new ClientboundRotateHeadPacket(serverPlayer,
                (byte) (loc.getYaw() * 256.0F / 360.0F)));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                conn.send(new ClientboundPlayerInfoRemovePacket(
                    List.of(serverPlayer.getUUID())));
            }, 20L);
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
        plugin.getLogger().info("Despawning NPC " + npc.getId() + " for " + player.getName());
    }

    public void updateNameTag(NPCEntity npc) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        for (Player player : loc.getWorld().getPlayers()) {
            updateNameTagForPlayer(npc, player);
        }
    }

    public void updateNameTagForPlayer(NPCEntity npc, Player player) {
        plugin.getLogger().info("Updating nametag for NPC " + npc.getId());
    }

    public void moveNPC(NPCEntity npc, Location newLoc) {
        npc.setLocation(newLoc);
        despawnNPC(npc);
        spawnNPC(npc);
    }
}
