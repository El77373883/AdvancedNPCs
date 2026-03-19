package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FamiliaManager {

    private final AdvancedNPCS plugin;
    private final Map<Integer, BukkitTask> familiaTasks;
    private final Random random;

    public FamiliaManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.familiaTasks = new HashMap<>();
        this.random = new Random();
        startFamiliaRoutines();
    }

    private void startFamiliaRoutines() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
                if (npc.getFamiliaEsposaId() != -1) {
                    handleParejaInteraccion(npc);
                }
                if (npc.isCaminarActivo()) {
                    handleCaminarNatural(npc);
                }
                if (npc.isDormirActivo()) {
                    handleSueno(npc);
                }
                if (npc.isComerActivo()) {
                    handleComida(npc);
                }
            }
        }, 0L, 100L);
    }

    private void handleParejaInteraccion(NPCEntity npc) {
        NPCEntity esposa = plugin.getNPCManager().getNPC(npc.getFamiliaEsposaId());
        if (esposa == null) return;
        Location locNpc = npc.getLocation();
        Location locEsposa = esposa.getLocation();
        if (locNpc == null || locEsposa == null) return;
        if (!locNpc.getWorld().equals(locEsposa.getWorld())) return;
        double distancia = locNpc.distance(locEsposa);
        FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
        if (distancia <= 3) {
            if (config.getBoolean("interacciones.beso", false)) {
                if (random.nextInt(100) < 5) {
                    spawnCorazones(locNpc);
                    spawnCorazones(locEsposa);
                    sendConversacion(npc, esposa, config.getStringList("conversaciones_pareja"));
                }
            }
            if (config.getBoolean("interacciones.abrazo", false)) {
                if (random.nextInt(100) < 3) {
                    plugin.getParticulasManager().spawnHearts(locNpc);
                    sendFrase(npc, "Te quiero mucho.");
                    sendFrase(esposa, "Yo tambien a ti.");
                }
            }
        }
        if (npc.getFamiliaHijoId() != -1) {
            NPCEntity hijo = plugin.getNPCManager().getNPC(npc.getFamiliaHijoId());
            if (hijo != null) {
                handleHijoInteraccion(npc, esposa, hijo, config);
            }
        }
    }

    private void handleHijoInteraccion(NPCEntity padre, NPCEntity madre, NPCEntity hijo, FileConfiguration config) {
        Location locHijo = hijo.getLocation();
        Location locPadre = padre.getLocation();
        if (locHijo == null || locPadre == null) return;
        double distancia = locHijo.distance(locPadre);
        if (config.getBoolean("interacciones.hijo_corre", false)) {
            if (distancia > 5 && random.nextInt(100) < 10) {
                plugin.getPacketManager().moveNPC(hijo, locPadre.clone().add(2, 0, 0));
                sendFrase(hijo, "¡PAPA! ¡MAMA!");
            }
        }
        if (config.getBoolean("interacciones.jugar_hijo", false)) {
            if (distancia <= 5 && random.nextInt(100) < 5) {
                sendFrase(padre, "¡Ven hijo, vamos a jugar!");
                sendFrase(hijo, "¡Si! ¡Vamos!");
                plugin.getParticulasManager().spawnBurstEffect(
                    locHijo, Particle.NOTE, 10);
            }
        }
        if (config.getBoolean("interacciones.ensenar", false)) {
            if (random.nextInt(1000) < 2) {
                sendFrase(padre, "Ven, te enseno a trabajar.");
                sendFrase(hijo, "¡Quiero aprender papa!");
                plugin.getLogManager().log(padre.getId(),
                    padre.getNombre() + " ensenando a " + hijo.getNombre());
            }
        }
        if (config.getBoolean("interacciones.paseo", false)) {
            if (random.nextInt(500) < 1) {
                sendFrase(padre, "¿Vamos a pasear?");
                sendFrase(madre, "¡Claro que si!");
                sendFrase(hijo, "¡Yo tambien quiero ir!");
                plugin.getParticulasManager().spawnHearts(locPadre);
            }
        }
    }

    private void handleCaminarNatural(NPCEntity npc) {
        if (random.nextInt(100) < 20) {
            Location loc = npc.getLocation();
            if (loc == null) return;
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * 5;
            Location newLoc = loc.clone().add(
                distance * Math.cos(angle), 0,
                distance * Math.sin(angle));
            if (newLoc.getBlock().getType().isSolid()) return;
            plugin.getPacketManager().moveNPC(npc, newLoc);
        }
    }

    private void handleSueno(NPCEntity npc) {
        long time = 0;
        if (npc.getMundo() != null) {
            org.bukkit.World world = plugin.getServer().getWorld(npc.getMundo());
            if (world != null) time = world.getTime();
        }
        long horaDormir = plugin.getConfig().getLong("sueno.hora_dormir", 13000);
        long horaDespertar = plugin.getConfig().getLong("sueno.hora_despertar", 23000);
        if (time >= horaDormir && time <= horaDespertar) {
            FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
            List<String> frases = config.getStringList("sueno.frases_dormir");
            if (!frases.isEmpty() && random.nextInt(500) < 1) {
                sendFrase(npc, frases.get(random.nextInt(frases.size())));
            }
            plugin.getParticulasManager().spawnBurstEffect(
                npc.getLocation(), Particle.CLOUD, 2);
        }
    }

    private void handleComida(NPCEntity npc) {
        if (random.nextInt(1000) < 1) {
            FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
            List<String> frases = config.getStringList("comida.frases_hambre");
            if (!frases.isEmpty()) {
                sendFrase(npc, frases.get(random.nextInt(frases.size())));
            }
        }
    }

    public void handleFollow(NPCEntity npc, Player player) {
        if (!npc.isSeguirActivo()) return;
        Location npcLoc = npc.getLocation();
        Location playerLoc = player.getLocation();
        if (npcLoc == null) return;
        if (!npcLoc.getWorld().getName().equals(playerLoc.getWorld().getName())) return;
        double distancia = npcLoc.distance(playerLoc);
        if (distancia > 3 && distancia < 50) {
            Location target = playerLoc.clone().add(
                -Math.sin(Math.toRadians(playerLoc.getYaw())) * 2,
                0,
                Math.cos(Math.toRadians(playerLoc.getYaw())) * 2);
            plugin.getPacketManager().moveNPC(npc, target);
        } else if (distancia >= 50) {
            plugin.getPacketManager().moveNPC(npc, playerLoc.clone().add(2, 0, 0));
        }
    }

    public void handleFamiliaAttacked(NPCEntity npc, Player attacker) {
        if (npc.getFamiliaEsposaId() != -1) {
            NPCEntity esposa = plugin.getNPCManager().getNPC(npc.getFamiliaEsposaId());
            if (esposa != null) {
                sendFrase(esposa, "¡Nadie toca a mi familia!");
                plugin.getParticulasManager().spawnBurstEffect(
                    esposa.getLocation(), Particle.CRIT, 10);
                attacker.damage(6.0);
            }
        }
        if (npc.getFamiliaPadreId() != -1) {
            NPCEntity padre = plugin.getNPCManager().getNPC(npc.getFamiliaPadreId());
            if (padre != null) {
                sendFrase(padre, "¡Nadie toca a mi hijo!");
                plugin.getParticulasManager().spawnBurstEffect(
                    padre.getLocation(), Particle.CRIT, 10);
                attacker.damage(8.0);
            }
        }
    }

    public void handleDialogo(NPCEntity npc, Player player) {
        FileConfiguration config = plugin.getDataManager().getNPCConfig(npc.getId());
        String saludo = config.getString("dialogo.inicio",
            "Hola " + player.getName() + ", ¿en que te puedo ayudar?");
        player.sendMessage(plugin.getMessageManager().color(
            "&7[NPC] &b" + npc.getNombre() + "&f: " + saludo));
        List<String> opciones = config.getStringList("dialogo.opciones_texto");
        if (!opciones.isEmpty()) {
            player.sendMessage(plugin.getMessageManager().color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            for (int i = 0; i < opciones.size(); i++) {
                player.sendMessage(plugin.getMessageManager().color(
                    "&e[" + (i + 1) + "] &7" + opciones.get(i)));
            }
            player.sendMessage(plugin.getMessageManager().color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        }
        plugin.getLogManager().log(npc.getId(),
            player.getName() + " inicio dialogo con " + npc.getNombre());
    }

    public NPCEntity crearEsposa(NPCEntity padre, String nombre, String skin) {
        Location loc = padre.getLocation();
        if (loc == null) return null;
        Location esposaLoc = loc.clone().add(2, 0, 0);
        NPCEntity esposa = new NPCEntity(plugin, plugin.getNPCManager().getAllNPCs().size());
        esposa.setNombre(nombre);
        esposa.setSkin(skin);
        esposa.setModo("VIDA_PROPIA");
        esposa.setTipo("PLAYER");
        esposa.setLocation(esposaLoc);
        esposa.setVidaMaxima(20);
        esposa.setVidaActual(20);
        esposa.setEscala(1.0);
        esposa.setEstado("ACTIVO");
        esposa.setEmocion("FELIZ");
        esposa.setEsEsposa(true);
        esposa.setFamiliaEsposaId(padre.getId());
        padre.setFamiliaEsposaId(esposa.getId());
        esposa.spawn();
        esposa.saveToConfig();
        padre.saveToConfig();
        announceEvento("&d&l✦ &f¡" + padre.getNombre() + " y " + nombre + " son pareja! &d&l✦");
        spawnCorazones(esposaLoc);
        spawnCorazones(loc);
        return esposa;
    }

    public NPCEntity crearHijo(NPCEntity padre, NPCEntity madre, String nombre, String skin) {
        Location loc = padre.getLocation();
        if (loc == null) return null;
        Location hijoLoc = loc.clone().add(1, 0, 1);
        NPCEntity hijo = new NPCEntity(plugin, plugin.getNPCManager().getAllNPCs().size());
        hijo.setNombre(nombre);
        hijo.setSkin(skin);
        hijo.setModo("VIDA_PROPIA");
        hijo.setTipo("PLAYER");
        hijo.setLocation(hijoLoc);
        hijo.setVidaMaxima(10);
        hijo.setVidaActual(10);
        hijo.setEscala(0.4);
        hijo.setEstado("ACTIVO");
        hijo.setEmocion("FELIZ");
        hijo.setEsHijo(true);
        hijo.setFamiliaPadreId(padre.getId());
        padre.setFamiliaHijoId(hijo.getId());
        if (madre != null) madre.setFamiliaHijoId(hijo.getId());
        hijo.spawn();
        hijo.saveToConfig();
        padre.saveToConfig();
        if (madre != null) madre.saveToConfig();
        announceNacimiento(padre, madre, hijo);
        spawnCorazones(hijoLoc);
        return hijo;
    }

    private void spawnCorazones(Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        loc.getWorld().spawnParticle(Particle.HEART, loc.clone().add(0, 2, 0),
            10, 0.5, 0.5, 0.5, 0.1);
    }

    private void sendFrase(NPCEntity npc, String frase) {
        Location loc = npc.getLocation();
        if (loc == null || loc.getWorld() == null) return;
        for (Player player : loc.getWorld().getPlayers()) {
            if (npc.isNearby(player, 20)) {
                player.sendMessage(plugin.getMessageManager().color(
                    "&7[NPC] &b" + npc.getNombre() + "&f: " + frase));
            }
        }
    }

    private void sendConversacion(NPCEntity npc1, NPCEntity npc2, List<String> conversaciones) {
        if (conversaciones.isEmpty()) return;
        int index = random.nextInt(conversaciones.size() / 2) * 2;
        if (index + 1 >= conversaciones.size()) return;
        final String frase1 = conversaciones.get(index);
        final String frase2 = conversaciones.get(index + 1);
        sendFrase(npc1, frase1);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            sendFrase(npc2, frase2);
        }, 40L);
    }

    private void announceEvento(String mensaje) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(plugin.getMessageManager().color(
                "&8╔══════════════════════════════════════════╗"));
            player.sendMessage(plugin.getMessageManager().color(
                "&8║   " + mensaje + "   ║"));
            player.sendMessage(plugin.getMessageManager().color(
                "&8║   &5✦ &7Creado por &bsoyadrianyt001 &5✦         ║"));
            player.sendMessage(plugin.getMessageManager().color(
                "&8╚══════════════════════════════════════════╝"));
        }
    }

    private void announceNacimiento(NPCEntity padre, NPCEntity madre, NPCEntity hijo) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(plugin.getMessageManager().color(
                "&8╔══════════════════════════════════════════╗"));
            player.sendMessage(plugin.getMessageManager().color(
                "&8║   &e&l✦ &f¡Ha nacido un nuevo NPC! &e&l✦        ║"));
            player.sendMessage(plugin.getMessageManager().color(
                "&8║   &7Nombre&8: &e" + hijo.getNombre() + "                    ║"));
            player.sendMessage(plugin.getMessageManager().color(
                "&8║   &7Padre&8: &b" + padre.getNombre() +
                (madre != null ? " &7Madre&8: &d" + madre.getNombre() : "") + "  ║"));
            player.sendMessage(plugin.getMessageManager().color(
                "&8║   &5✦ &7Creado por &bsoyadrianyt001 &5✦         ║"));
            player.sendMessage(plugin.getMessageManager().color(
                "&8╚══════════════════════════════════════════╝"));
        }
    }
}
