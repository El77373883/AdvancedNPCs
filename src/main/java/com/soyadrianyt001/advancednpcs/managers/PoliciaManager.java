package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PoliciaManager {

    private final AdvancedNPCS plugin;
    private final Map<UUID, Integer> nivelBusqueda;
    private final Map<UUID, BukkitTask> reduccionTasks;

    public PoliciaManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.nivelBusqueda = new HashMap<>();
        this.reduccionTasks = new HashMap<>();
    }

    public int getNivelBusqueda(Player player) {
        return nivelBusqueda.getOrDefault(player.getUniqueId(), 0);
    }

    public void aumentarNivel(Player player, int cantidad) {
        int nivel = getNivelBusqueda(player) + cantidad;
        nivel = Math.min(nivel, 5);
        nivelBusqueda.put(player.getUniqueId(), nivel);
        plugin.getMessageManager().send(player, "buscado",
            "%estrellas%", "⭐".repeat(nivel));
        startReduccion(player);
    }

    public void reducirNivel(Player player, int cantidad) {
        int nivel = getNivelBusqueda(player) - cantidad;
        nivel = Math.max(nivel, 0);
        nivelBusqueda.put(player.getUniqueId(), nivel);
    }

    public void resetNivel(Player player) {
        nivelBusqueda.put(player.getUniqueId(), 0);
    }

    private void startReduccion(Player player) {
        BukkitTask existing = reduccionTasks.get(player.getUniqueId());
        if (existing != null) existing.cancel();
        long intervalo = plugin.getConfig().getLong("policia.tiempo_reduccion_segundos", 300) * 20L;
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (getNivelBusqueda(player) > 0) {
                    reducirNivel(player, 1);
                }
            });
        }, intervalo, intervalo);
        reduccionTasks.put(player.getUniqueId(), task);
    }

    public void handleNPCAttacked(NPCEntity npc, Player attacker) {
        aumentarNivel(attacker, 2);
        int nivel = getNivelBusqueda(attacker);
        switch (nivel) {
            case 1 -> advertirJugador(npc, attacker);
            case 2 -> multarJugador(npc, attacker);
            case 3 -> intentarArrestar(npc, attacker);
            case 4, 5 -> atacarJugador(npc, attacker);
        }
    }

    private void advertirJugador(NPCEntity npc, Player player) {
        player.sendMessage(plugin.getMessageManager().color(
            "&c[NPC] &7" + npc.getNombre() + "&f: ¡Oye! ¡Detente o llamare a refuerzos!"));
    }

    private void multarJugador(NPCEntity npc, Player player) {
        double multa = 250;
        if (plugin.getEconomy().has(player, multa)) {
            plugin.getEconomy().withdrawPlayer(player, multa);
            plugin.getMessageManager().send(player, "multado", "%cantidad%", String.valueOf(multa));
        } else {
            aumentarNivel(player, 1);
        }
        plugin.getLogManager().log(npc.getId(),
            npc.getNombre() + " multo a " + player.getName() + " por $" + multa);
    }

    private void intentarArrestar(NPCEntity npc, Player player) {
        player.sendMessage(plugin.getMessageManager().color(
            "&c[NPC] &7" + npc.getNombre() + "&f: ¡Estas arrestado! Tienes 10 segundos para rendirte."));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (getNivelBusqueda(player) >= 3) {
                arrestarJugador(npc, player);
            }
        }, 200L);
    }

    private void arrestarJugador(NPCEntity npc, Player player) {
        FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
        double px = config.getDouble("arresto.prision.x", 0);
        double py = config.getDouble("arresto.prision.y", 64);
        double pz = config.getDouble("arresto.prision.z", 0);
        String mundo = config.getString("arresto.prision.mundo", "world");
        org.bukkit.World w = plugin.getServer().getWorld(mundo);
        if (w != null) {
            player.teleport(new Location(w, px, py, pz));
            plugin.getMessageManager().send(player, "arrestado", "%tiempo%", "300");
            resetNivel(player);
            plugin.getLogManager().log(npc.getId(),
                npc.getNombre() + " arresto a " + player.getName());
        }
    }

    private void atacarJugador(NPCEntity npc, Player player) {
        player.sendMessage(plugin.getMessageManager().color(
            "&c[NPC] &7" + npc.getNombre() + "&f: ¡Atacando!"));
        player.damage(8.0);
        plugin.getParticulasManager().spawnBurstEffect(
            npc.getLocation(), org.bukkit.Particle.CRIT, 10);
    }
}
