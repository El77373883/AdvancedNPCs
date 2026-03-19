package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;

public class TrabajoManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, BukkitTask> trabajoTasks;
    private final Map<Integer, BukkitTask> moveTasks;
    private final Random random;

    public TrabajoManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.trabajoTasks = new HashMap<>();
        this.moveTasks = new HashMap<>();
        this.random = new Random();
    }

    public void startTrabajo(NPCEntity npc) {
        stopTrabajo(npc);
        switch (npc.getProfesion()) {
            case "MINERO" -> startMinero(npc);
            case "TALADOR" -> startTalador(npc);
            case "FARMEADOR", "COSECHADOR" -> startAgricultor(npc);
            case "PESCADOR" -> startPescador(npc);
            case "CUIDADOR_ANIMALES", "CUIDADOR_OVEJAS" -> startCuidador(npc);
            case "SEGUIDOR", "AMIGO" -> startSeguidor(npc);
            case "GUARDAESPALDAS" -> startGuardaespaldas(npc);
            case "POLICIA", "GUARDIA", "DEFENSOR" -> startPatrulla(npc);
            case "COMERCIANTE_AMBULANTE" -> startCaminarNatural(npc);
            default -> {
                if (npc.isCaminarActivo()) startCaminarNatural(npc);
            }
        }
    }

    public void stopTrabajo(NPCEntity npc) {
        BukkitTask task = trabajoTasks.remove(npc.getId());
        if (task != null) task.cancel();
        BukkitTask moveTask = moveTasks.remove(npc.getId());
        if (moveTask != null) moveTask.cancel();
    }

    private void moverSuave(NPCEntity npc, Location destino) {
        BukkitTask oldMove = moveTasks.remove(npc.getId());
        if (oldMove != null) oldMove.cancel();
        Location inicio = npc.getLocation();
        if (inicio == null || destino == null) return;
        if (inicio.getWorld() == null || destino.getWorld() == null) return;
        if (!inicio.getWorld().equals(destino.getWorld())) return;
        double distancia = inicio.distance(destino);
        if (distancia < 0.5) return;
        int pasos = Math.max(1, (int)(distancia * 4));
        double dx = (destino.getX() - inicio.getX()) / pasos;
        double dy = (destino.getY() - inicio.getY()) / pasos;
        double dz = (destino.getZ() - inicio.getZ()) / pasos;
        final int[] step = {0};
        BukkitTask moveTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (step[0] >= pasos) {
                moveTasks.remove(npc.getId());
                plugin.getPacketManager().moveNPC(npc, destino);
                return;
            }
            double nx = inicio.getX() + dx * step[0];
            double ny = inicio.getY() + dy * step[0];
            double nz = inicio.getZ() + dz * step[0];
            double yaw = Math.toDegrees(Math.atan2(
                destino.getZ() - inicio.getZ(),
                destino.getX() - inicio.getX())) - 90;
            Location pasoLoc = new Location(inicio.getWorld(), nx, ny, nz,
                (float) yaw, 0);
            plugin.getPacketManager().moveNPC(npc, pasoLoc);
            step[0]++;
        }, 0L, 2L);
        moveTasks.put(npc.getId(), moveTask);
    }

    private void startMinero(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
            int radio = config.getInt("trabajo.radio", 5);
            for (int x = -radio; x <= radio; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -radio; z <= radio; z++) {
                        Block block = loc.clone().add(x, y, z).getBlock();
                        if (isMineral(block.getType())) {
                            Location dest = block.getLocation().clone().add(0.5, 0, 0.5);
                            moverSuave(npc, dest);
                            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                                Material tipo = block.getType();
                                block.setType(Material.AIR);
                                spawnParticulas(npc, block.getLocation());
                                plugin.getLogManager().log(npc.getId(),
                                    npc.getNombre() + " mino " + tipo.name());
                                sendFrase(npc, Arrays.asList(
                                    "Encontre un mineral!",
                                    "Minando...",
                                    "Esto vale mucho!"));
                            }, 20L);
                            return;
                        }
                    }
                }
            }
            wanderAround(npc, 3);
            sendFrase(npc, Arrays.asList(
                "No hay minerales cerca...",
                "Buscando veta..."));
        }, 0L, 100L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startTalador(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            int radio = 8;
            for (int x = -radio; x <= radio; x++) {
                for (int y = 0; y <= 5; y++) {
                    for (int z = -radio; z <= radio; z++) {
                        Block block = loc.clone().add(x, y, z).getBlock();
                        if (isTronco(block.getType())) {
                            Location dest = block.getLocation().clone().add(0.5, 0, 0.5);
                            moverSuave(npc, dest);
                            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                                Material tipo = block.getType();
                                block.setType(Material.AIR);
                                spawnParticulas(npc, block.getLocation());
                                plugin.getLogManager().log(npc.getId(),
                                    npc.getNombre() + " corto " + tipo.name());
                                sendFrase(npc, Arrays.asList(
                                    "Cortando arbol...",
                                    "Arbol cortado!",
                                    "Madera lista."));
                            }, 20L);
                            return;
                        }
                    }
                }
            }
            wanderAround(npc, 5);
        }, 0L, 80L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startAgricultor(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            int radio = 5;
            for (int x = -radio; x <= radio; x++) {
                for (int z = -radio; z <= radio; z++) {
                    Block block = loc.clone().add(x, 0, z).getBlock();
                    if (block.getType() == Material.WHEAT) {
                        try {
                            org.bukkit.block.data.Ageable ageable =
                                (org.bukkit.block.data.Ageable) block.getBlockData();
                            if (ageable.getAge() == ageable.getMaximumAge()) {
                                Location dest = block.getLocation().clone().add(0.5, 0, 0.5);
                                moverSuave(npc, dest);
                                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                                    block.setType(Material.AIR);
                                    Block soil = block.getRelative(0, -1, 0);
                                    if (soil.getType() == Material.FARMLAND) {
                                        block.setType(Material.WHEAT);
                                    }
                                    spawnParticulas(npc, block.getLocation());
                                    sendFrase(npc, Arrays.asList(
                                        "Cosechando...",
                                        "Buena cosecha!",
                                        "Replantando..."));
                                }, 15L);
                                return;
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
            wanderAround(npc, 3);
        }, 0L, 60L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startPescador(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            boolean aguaCerca = false;
            for (int x = -3; x <= 3 && !aguaCerca; x++) {
                for (int z = -3; z <= 3 && !aguaCerca; z++) {
                    if (loc.clone().add(x, -1, z).getBlock().getType() == Material.WATER) {
                        aguaCerca = true;
                    }
                }
            }
            if (aguaCerca) {
                spawnParticulas(npc, loc);
                sendFrase(npc, Arrays.asList(
                    "Pescando...",
                    "Esperando que piquen...",
                    "Pique!"));
            } else {
                wanderAround(npc, 5);
                sendFrase(npc, Arrays.asList(
                    "Buscando agua...",
                    "Necesito un lago..."));
            }
        }, 0L, 200L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startCuidador(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            loc.getWorld().getEntitiesByClass(org.bukkit.entity.Animals.class).stream()
                .filter(a -> a.getLocation().distance(loc) < 15)
                .findFirst()
                .ifPresent(animal -> {
                    moverSuave(npc, animal.getLocation());
                    sendFrase(npc, Arrays.asList(
                        "Cuidando animales...",
                        "Alimentando el rebano...",
                        "Ven aqui, bichito!"));
                });
            if (random.nextInt(5) == 0) wanderAround(npc, 5);
        }, 0L, 150L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startSeguidor(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            Player closest = null;
            double minDist = 30;
            for (Player p : loc.getWorld().getPlayers()) {
                double dist = p.getLocation().distance(loc);
                if (dist < minDist) {
                    minDist = dist;
                    closest = p;
                }
            }
            if (closest != null && minDist > 3) {
                moverSuave(npc, closest.getLocation().clone());
            }
        }, 0L, 10L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startGuardaespaldas(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            Player closest = null;
            double minDist = 50;
            for (Player p : loc.getWorld().getPlayers()) {
                double dist = p.getLocation().distance(loc);
                if (dist < minDist) {
                    minDist = dist;
                    closest = p;
                }
            }
            if (closest != null) {
                if (minDist > 5) {
                    moverSuave(npc, closest.getLocation().clone());
                }
                for (org.bukkit.entity.Entity entity :
                    loc.getWorld().getNearbyEntities(loc, 10, 10, 10)) {
                    if (entity instanceof org.bukkit.entity.Monster) {
                        moverSuave(npc, entity.getLocation().clone());
                        sendFrase(npc, Arrays.asList(
                            "Protegiendo al jugador!",
                            "Atras, monstruo!",
                            "No te acerques!"));
                        break;
                    }
                }
            }
        }, 0L, 15L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startPatrulla(NPCEntity npc) {
        Location baseLocation = npc.getLocation() != null ?
            npc.getLocation().clone() : null;
        if (baseLocation == null) return;
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null || loc.getWorld() == null) return;
            double angle = (System.currentTimeMillis() / 3000.0) % (2 * Math.PI);
            double radio = 8;
            double nx = baseLocation.getX() + radio * Math.cos(angle);
            double nz = baseLocation.getZ() + radio * Math.sin(angle);
            Location patrullaLoc = new Location(loc.getWorld(), nx,
                baseLocation.getY(), nz);
            Block floor = patrullaLoc.getBlock();
            Block above = floor.getRelative(0, 1, 0);
            if (floor.getType() != Material.AIR &&
                above.getType() == Material.AIR) {
                moverSuave(npc, patrullaLoc);
            }
            for (Player p : loc.getWorld().getPlayers()) {
                if (p.getLocation().distance(loc) < 5) {
                    sendFrase(npc, Arrays.asList(
                        "Zona bajo vigilancia.",
                        "Muevete, ciudadano.",
                        "Todo en orden aqui."));
                    break;
                }
            }
        }, 0L, 40L);
        trabajoTasks.put(npc.getId(), task);
    }

    public void startCaminarNatural(NPCEntity npc) {
        BukkitTask old = trabajoTasks.remove(npc.getId());
        if (old != null) old.cancel();
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            wanderAround(npc, 8);
        }, 0L, 80L);
        trabajoTasks.put(npc.getId(), task);
    }

    public void handleFollow(NPCEntity npc, Player player) {
        if (!npc.isSeguirActivo()) return;
        Location npcLoc = npc.getLocation();
        if (npcLoc == null) return;
        double dist = player.getLocation().distance(npcLoc);
        if (dist > 3 && dist < 30) {
            moverSuave(npc, player.getLocation().clone());
        }
    }

    private void wanderAround(NPCEntity npc, double radio) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = 2 + random.nextDouble() * radio;
        double nx = loc.getX() + distance * Math.cos(angle);
        double nz = loc.getZ() + distance * Math.sin(angle);
        Location newLoc = new Location(loc.getWorld(), nx, loc.getY(), nz);
        Block floor = newLoc.getBlock();
        Block above = floor.getRelative(0, 1, 0);
        if (floor.getType() != Material.AIR &&
            above.getType() == Material.AIR) {
            moverSuave(npc, newLoc.add(0.5, 0, 0.5));
        }
    }

    private void spawnParticulas(NPCEntity npc, Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(
            org.bukkit.Particle.CRIT,
            loc.clone().add(0, 1, 0),
            5, 0.3, 0.3, 0.3, 0.1);
    }

    private boolean isMineral(Material mat) {
        return mat == Material.COAL_ORE || mat == Material.IRON_ORE ||
               mat == Material.GOLD_ORE || mat == Material.DIAMOND_ORE ||
               mat == Material.EMERALD_ORE || mat == Material.REDSTONE_ORE ||
               mat == Material.LAPIS_ORE || mat == Material.COPPER_ORE ||
               mat == Material.DEEPSLATE_COAL_ORE ||
               mat == Material.DEEPSLATE_IRON_ORE ||
               mat == Material.DEEPSLATE_GOLD_ORE ||
               mat == Material.DEEPSLATE_DIAMOND_ORE;
    }

    private boolean isTronco(Material mat) {
        return mat == Material.OAK_LOG || mat == Material.BIRCH_LOG ||
               mat == Material.SPRUCE_LOG || mat == Material.JUNGLE_LOG ||
               mat == Material.ACACIA_LOG || mat == Material.DARK_OAK_LOG ||
               mat == Material.MANGROVE_LOG || mat == Material.CHERRY_LOG;
    }

    private void sendFrase(NPCEntity npc, List<String> frases) {
        if (frases.isEmpty()) return;
        if (random.nextInt(4) != 0) return;
        String frase = frases.get(random.nextInt(frases.size()));
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        for (Player player : loc.getWorld().getPlayers()) {
            if (npc.isNearby(player, 15)) {
                player.sendMessage(plugin.getMessageManager().color(
                    "&7[NPC] &b" + npc.getNombre() + "&f: " + frase));
            }
        }
    }
}
