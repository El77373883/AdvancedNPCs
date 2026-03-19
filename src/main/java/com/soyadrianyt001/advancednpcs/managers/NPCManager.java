package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.*;

public class NPCManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, NPCEntity> npcs;
    private int nextId;

    public NPCManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.npcs = new HashMap<>();
        this.nextId = 0;
        loadAllNPCs();
    }

    private void loadAllNPCs() {
        File folder = plugin.getDataManager().getNpcFolder();
        File[] files = folder.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            int id = config.getInt("id");
            NPCEntity npc = new NPCEntity(plugin, id);
            npc.loadFromConfig(config);
            npcs.put(id, npc);
            if (id >= nextId) nextId = id + 1;
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (NPCEntity npc : npcs.values()) {
                spawnNPCEntity(npc);
            }
            plugin.getLogger().info("Cargados " + npcs.size() + " NPCs.");
        }, 20L);
    }

    public NPCEntity createNPC(String nombre, Location location, Player creator) {
        int id = nextId++;
        NPCEntity npc = new NPCEntity(plugin, id);
        npc.setNombre(nombre);
        npc.setLocation(location);
        npc.setModo("ESTATICO");
        npc.setTipo("PLAYER");
        npc.setSkin("Steve");
        npc.setVidaMaxima(20);
        npc.setVidaActual(20);
        npc.setEstado("ACTIVO");
        npc.setEmocion("NEUTRAL");
        npc.setEscala(1.0);
        npc.saveToConfig();
        npcs.put(id, npc);
        updateRegistro(id, nombre);
        plugin.getLogManager().log(id, "NPC #" + id + " creado por " + creator.getName());
        return npc;
    }

    public void spawnNPCEntity(NPCEntity npc) {
        plugin.getPacketManager().spawnNPC(npc);
    }

    public void deleteNPC(int id) {
        NPCEntity npc = npcs.get(id);
        if (npc == null) return;
        plugin.getPacketManager().despawnNPC(npc);
        npcs.remove(id);
        plugin.getDataManager().deleteNPCConfig(id);
        removeRegistro(id);
        plugin.getLogManager().log(id, "NPC #" + id + " eliminado.");
    }

    public NPCEntity getNPC(int id) {
        return npcs.get(id);
    }

    public NPCEntity getNPCByName(String nombre) {
        for (NPCEntity npc : npcs.values()) {
            if (npc.getNombre().equalsIgnoreCase(nombre)) return npc;
        }
        return null;
    }

    public List<NPCEntity> getAllNPCs() {
        return new ArrayList<>(npcs.values());
    }

    public int getTotalNPCs() {
        return npcs.size();
    }

    public boolean exists(int id) {
        return npcs.containsKey(id);
    }

    private void updateRegistro(int id, String nombre) {
        List<Map<?, ?>> lista = plugin.getConfig().getMapList("npcs_registrados");
        Map<String, Object> entry = new HashMap<>();
        entry.put("id", id);
        entry.put("nombre", nombre);
        entry.put("archivo", "npcs/npc_" + id + ".yml");
        lista.add(entry);
        plugin.getConfig().set("npcs_registrados", lista);
        plugin.saveConfig();
    }

    private void removeRegistro(int id) {
        List<Map<?, ?>> lista = plugin.getConfig().getMapList("npcs_registrados");
        lista.removeIf(m -> ((Number) m.get("id")).intValue() == id);
        plugin.getConfig().set("npcs_registrados", lista);
        plugin.saveConfig();
    }

    public void saveAll() {
        for (NPCEntity npc : npcs.values()) {
            npc.saveToConfig();
        }
    }

    public void reloadAll() {
        for (NPCEntity npc : npcs.values()) {
            plugin.getPacketManager().despawnNPC(npc);
        }
        npcs.clear();
        nextId = 0;
        loadAllNPCs();
    }
}
