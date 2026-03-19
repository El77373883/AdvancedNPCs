package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClimatiManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, String> climaActual;
    private final Random random;
    private BukkitTask climaTask;

    public ClimatiManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.climaActual = new HashMap<>();
        this.random = new Random();
        startClimaTick();
    }

    private void startClimaTick() {
        climaTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
                if (npc.getMundo() == null) continue;
                World world = plugin.getServer().getWorld(npc.getMundo());
                if (world == null) continue;
                String climaPrevio = climaActual.getOrDefault(npc.getId(), "SOLEADO");
                String climaNuevo = detectarClima(world);
                if (!climaPrevio.equals(climaNuevo)) {
                    climaActual.put(npc.getId(), climaNuevo);
                    handleCambioClima(npc, climaNuevo);
                }
                handleClimaActivo(npc, climaNuevo);
            }
        }, 0L, 100L);
    }

    private String detectarClima(World world) {
        if (world.isThundering()) return "TORMENTA";
        if (world.hasStorm()) return "LLUVIA";
        return "SOLEADO";
    }

    private void handleCambioClima(NPCEntity npc, String clima) {
        FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
        switch (clima) {
            case "LLUVIA" -> {
                List<String> frases = config.getStringList("reaccion_clima.lluvia.frases");
                if (!frases.isEmpty()) sendFrase(npc, frases.get(random.nextInt(frases.size())));
                if (config.getBoolean("reaccion_clima.lluvia.buscar_refugio", true)) {
                    handleRefugio(npc, config);
                }
                if (config.getBoolean("reaccion_clima.lluvia.pausar_trabajo", true)) {
                    plugin.getTrabajoManager().stopTrabajo(npc);
                }
            }
            case "TORMENTA" -> {
                List<String> frases = config.getStringList("reaccion_clima.tormenta.frases");
                if (!frases.isEmpty()) sendFrase(npc, frases.get(random.nextInt(frases.size())));
                Location loc = npc.getLocation();
                if (loc != null) {
                    loc.getWorld().strikeLightningEffect(loc);
                }
            }
            case "SOLEADO" -> {
                if (npc.getProfesion() != null && !npc.getProfesion().equals("NINGUNA")) {
                    plugin.getTrabajoManager().startTrabajo(npc);
                }
            }
        }
    }

    private void handleClimaActivo(NPCEntity npc, String clima) {
        if (clima.equals("LLUVIA")) {
            Location loc = npc.getLocation();
            if (loc != null && loc.getWorld() != null) {
                loc.getWorld().spawnParticle(
                    Particle.DRIPPING_WATER,
                    loc.clone().add(0, 1, 0),
                    2, 0.3, 0.3, 0.3, 0);
            }
        }
    }

    private void handleRefugio(NPCEntity npc, FileConfiguration config) {
        double rx = config.getDouble("reaccion_clima.lluvia.refugio.x", npc.getLocation().getX());
        double ry = config.getDouble("reaccion_clima.lluvia.refugio.y", npc.getLocation().getY());
        double rz = config.getDouble("reaccion_clima.lluvia.refugio.z", npc.getLocation().getZ());
        String mundo = npc.getMundo();
        World w = plugin.getServer().getWorld(mundo);
        if (w != null) {
            plugin.getPacketManager().moveNPC(npc,
                new Location(w, rx, ry, rz));
        }
    }

    private void sendFrase(NPCEntity npc, String frase) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        for (org.bukkit.entity.Player player : loc.getWorld().getPlayers()) {
            if (npc.isNearby(player, 20)) {
                player.sendMessage(plugin.getMessageManager().color(
                    "&7[NPC] &b" + npc.getNombre() + "&f: " + frase));
            }
        }
    }

    public String getClimaActual(int npcId) {
        return climaActual.getOrDefault(npcId, "SOLEADO");
    }
}
