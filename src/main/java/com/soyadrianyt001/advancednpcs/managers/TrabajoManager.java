package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TrabajoManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, BukkitTask> trabajoTasks;
    private final Random random;

    public TrabajoManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.trabajoTasks = new HashMap<>();
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
            case "CAMINAR_NATURAL" -> startCaminarNatural(npc);
        }
    }

    public void stopTrabajo(NPCEntity npc) {
        BukkitTask task = trabajoTasks.remove(npc.getId());
        if (task != null) task.cancel();
    }

    private void startMinero(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null) return;
            FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
            int radio = config.getInt("trabajo.radio", 5);
            for (int x = -radio; x <= radio; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -radio; z <= radio; z++) {
                        Block block = loc.clone().add(x, y, z).getBlock();
                        if (isMineral(block.getType())) {
                            plugin.getPacketManager().moveNPC(npc, block.getLocation());
                            block.setType(Material.AIR);
                            plugin.getLogManager().log(npc.getId(),
                                "Minero " + npc.getNombre() + " mino " + block.getType().name());
                            sendFrase(npc, config.getStringList("trabajo.frases_trabajando"));
                            return;
                        }
                    }
                }
            }
            sendFrase(npc, java.util.Arrays.asList("No hay minerales cerca..."));
        }, 0L, 100L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startTalador(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null) return;
            FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
            int radio = config.getInt("trabajo.radio", 8);
            for (int x = -radio; x <= radio; x++) {
                for (int y = 0; y <= 5; y++) {
                    for (int z = -radio; z <= radio; z++) {
                        Block block = loc.clone().add(x, y, z).getBlock();
                        if (isTronco(block.getType())) {
                            plugin.getPacketManager().moveNPC(npc, block.getLocation());
                            block.setType(Material.AIR);
                            plugin.getLogManager().log(npc.getId(),
                                "Talador " + npc.getNombre() + " corto " + block.getType().name());
                            return;
                        }
                    }
                }
            }
        }, 0L, 80L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startAgricultor(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null) return;
            int radio = 5;
            for (int x = -radio; x <= radio; x++) {
                for (int z = -radio; z <= radio; z++) {
                    Block block = loc.clone().add(x, 0, z).getBlock();
                    if (block.getType() == Material.WHEAT) {
                        org.bukkit.block.data.Ageable ageable =
                            (org.bukkit.block.data.Ageable) block.getBlockData();
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            plugin.getPacketManager().moveNPC(npc, block.getLocation());
                            block.setType(Material.AIR);
                            Block soil = block.getRelative(0, -1, 0);
                            if (soil.getType() == Material.FARMLAND) {
                                block.setType(Material.WHEAT);
                            }
                            plugin.getLogManager().log(npc.getId(),
                                npc.getNombre() + " cosecho WHEAT");
                            return;
                        }
                    }
                }
            }
        }, 0L, 60L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startPescador(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            sendFrase(npc, java.util.Arrays.asList("Pescando...", "Esperando que piquen..."));
            plugin.getLogManager().log(npc.getId(), npc.getNombre() + " esta pescando.");
        }, 0L, 200L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startCuidador(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            sendFrase(npc, java.util.Arrays.asList("Cuidando animales...", "Alimentando el rebano..."));
        }, 0L, 150L);
        trabajoTasks.put(npc.getId(), task);
    }

    private void startCaminarNatural(NPCEntity npc) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Location loc = npc.getLocation();
            if (loc == null) return;
            double radio = 10;
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * radio;
            Location newLoc = loc.clone().add(
                distance * Math.cos(angle), 0,
                distance * Math.sin(angle));
            if (newLoc.getBlock().getType() == Material.AIR) {
                plugin.getPacketManager().moveNPC(npc, newLoc);
            }
        }, 0L, 60L);
        trabajoTasks.put(npc.getId(), task);
    }

    private boolean isMineral(Material mat) {
        return mat == Material.COAL_ORE || mat == Material.IRON_ORE ||
               mat == Material.GOLD_ORE || mat == Material.DIAMOND_ORE ||
               mat == Material.EMERALD_ORE || mat == Material.REDSTONE_ORE ||
               mat == Material.LAPIS_ORE || mat == Material.COPPER_ORE ||
               mat == Material.DEEPSLATE_COAL_ORE || mat == Material.DEEPSLATE_IRON_ORE ||
               mat == Material.DEEPSLATE_GOLD_ORE || mat == Material.DEEPSLATE_DIAMOND_ORE;
    }

    private boolean isTronco(Material mat) {
        return mat == Material.OAK_LOG || mat == Material.BIRCH_LOG ||
               mat == Material.SPRUCE_LOG || mat == Material.JUNGLE_LOG ||
               mat == Material.ACACIA_LOG || mat == Material.DARK_OAK_LOG ||
               mat == Material.MANGROVE_LOG || mat == Material.CHERRY_LOG;
    }

    private void sendFrase(NPCEntity npc, java.util.List<String> frases) {
        if (frases.isEmpty()) return;
        String frase = frases.get(random.nextInt(frases.size()));
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        for (org.bukkit.entity.Player player : loc.getWorld().getPlayers()) {
            if (npc.isNearby(player, 20)) {
                player.sendMessage(plugin.getMessageManager().color(
                    "&7[NPC] &b" + npc.getNombre() + "&f: " + frase));
            }
        }
    }
}
