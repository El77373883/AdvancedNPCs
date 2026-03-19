package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.Map;

public class PartikulasManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, BukkitTask> tasks;

    public PartikulasManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.tasks = new HashMap<>();
    }

    public void startParticles(NPCEntity npc) {
        if (tasks.containsKey(npc.getId())) stopParticles(npc);
        if (npc.getParticulas().isEmpty()) return;
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            for (String particulaStr : npc.getParticulas()) {
                try {
                    String[] parts = particulaStr.split(":");
                    String tipo = parts[0];
                    String modo = parts.length > 1 ? parts[1] : "NORMAL";
                    Particle particle = Particle.valueOf(tipo);
                    switch (modo.toUpperCase()) {
                        case "AURA" -> spawnAura(loc, particle);
                        case "CORONA" -> spawnCorona(loc, particle);
                        case "RASTRO" -> spawnRastro(loc, particle);
                        default -> spawnNormal(loc, particle);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Particula invalida: " + particulaStr);
                }
            }
        }, 0L, 2L);
        tasks.put(npc.getId(), task);
    }

    public void stopParticles(NPCEntity npc) {
        BukkitTask task = tasks.remove(npc.getId());
        if (task != null) task.cancel();
    }

    private void spawnNormal(Location loc, Particle particle) {
        loc.getWorld().spawnParticle(particle,
            loc.clone().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.05);
    }

    private void spawnAura(Location loc, Particle particle) {
        double radius = 1.0;
        long time = System.currentTimeMillis();
        for (int i = 0; i < 8; i++) {
            double angle = (2 * Math.PI / 8) * i + (time / 500.0);
            double px = loc.getX() + radius * Math.cos(angle);
            double pz = loc.getZ() + radius * Math.sin(angle);
            Location pLoc = new Location(loc.getWorld(), px, loc.getY() + 1, pz);
            loc.getWorld().spawnParticle(particle, pLoc, 1, 0, 0, 0, 0);
        }
    }

    private void spawnCorona(Location loc, Particle particle) {
        double radius = 0.5;
        long time = System.currentTimeMillis();
        for (int i = 0; i < 6; i++) {
            double angle = (2 * Math.PI / 6) * i + (time / 300.0);
            double px = loc.getX() + radius * Math.cos(angle);
            double pz = loc.getZ() + radius * Math.sin(angle);
            Location pLoc = new Location(loc.getWorld(), px, loc.getY() + 2.2, pz);
            loc.getWorld().spawnParticle(particle, pLoc, 1, 0, 0, 0, 0);
        }
    }

    private void spawnRastro(Location loc, Particle particle) {
        loc.getWorld().spawnParticle(particle,
            loc.clone().add(0, 0.1, 0), 3, 0.2, 0, 0.2, 0);
    }

    public void spawnBurstEffect(Location loc, Particle particle, int count) {
        if (loc == null || loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(particle, loc.clone().add(0, 1, 0),
            count, 0.5, 0.5, 0.5, 0.1);
    }

    public void spawnHearts(Location loc) {
        spawnBurstEffect(loc, Particle.HEART, 10);
    }

    public void spawnFreezeEffect(Location loc) {
        spawnBurstEffect(loc, Particle.SNOWFLAKE, 20);
    }

    public void spawnPoisonEffect(Location loc) {
        spawnBurstEffect(loc, Particle.WITCH, 15);
    }
}
