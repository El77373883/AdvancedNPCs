package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;

public class DialogoManager {

    private final AdvancedNPCS plugin;
    private final Map<UUID, Integer> conversacionActiva;
    private final Map<UUID, NPCEntity> npcEnConversacion;
    private final Map<UUID, BukkitTask> timeoutTasks;
    private final Map<UUID, Long> ultimoSaludo;
    private final Random random;

    private static final Map<String, List<String>> RESPUESTAS = new HashMap<>();
    private static final List<String> SALUDOS_NPC = Arrays.asList(
        "Hola! Como estas?",
        "Buenos dias! Que tal?",
        "Hey! Un placer verte por aqui!",
        "Hola viajero! Que te trae por aqui?",
        "Saludos! En que puedo ayudarte?"
    );
    private static final List<String> DESPEDIDAS_NPC = Arrays.asList(
        "Hasta luego! Fue un placer hablar contigo.",
        "Cuídate mucho! Vuelve pronto.",
        "Adios! Que tengas un buen dia.",
        "Hasta la proxima! Buen viaje.",
        "Que estes bien! Nos vemos."
    );

    static {
        RESPUESTAS.put("hola", Arrays.asList(
            "Hola! Como estas hoy?",
            "Hey! Que gusto verte!",
            "Hola! Bienvenido!",
            "Buenos dias! Que tal el dia?"
        ));
        RESPUESTAS.put("bien", Arrays.asList(
            "Me alegra mucho escuchar eso!",
            "Genial! Yo tambien estoy bien, gracias.",
            "Que bueno! Sigue asi.",
            "Perfecto! El dia esta hermoso verdad?"
        ));
        RESPUESTAS.put("mal", Arrays.asList(
            "Vaya... lo siento mucho. Espero que mejores.",
            "No te preocupes, los dias malos pasan.",
            "Animo! Mañana sera mejor.",
            "Lo siento. Puedo ayudarte en algo?"
        ));
        RESPUESTAS.put("como estas", Arrays.asList(
            "Muy bien gracias por preguntar! Y tu?",
            "Excelente! Trabajando duro como siempre.",
            "Bien bien, no me puedo quejar.",
            "De maravilla! Hoy es un gran dia."
        ));
        RESPUESTAS.put("nombre", Arrays.asList(
            "Mi nombre ya lo sabes, lo ves sobre mi cabeza!",
            "Ese soy yo! Para servirte.",
            "Ese es mi nombre, si."
        ));
        RESPUESTAS.put("trabajo", Arrays.asList(
            "Trabajo duro todos los dias para este servidor!",
            "Mi trabajo es lo mas importante para mi.",
            "Sin trabajo no hay progreso!"
        ));
        RESPUESTAS.put("ayuda", Arrays.asList(
            "Claro! En que puedo ayudarte?",
            "Siempre listo para ayudar!",
            "Dime que necesitas."
        ));
        RESPUESTAS.put("adios", Arrays.asList(
            "Hasta luego! Fue un placer.",
            "Adios! Vuelve pronto.",
            "Cuídate! Hasta la proxima."
        ));
        RESPUESTAS.put("gracias", Arrays.asList(
            "De nada! Para eso estoy.",
            "No hay de que! Es mi placer.",
            "Con gusto! Cuando quieras."
        ));
        RESPUESTAS.put("clima", Arrays.asList(
            "Si, el clima esta raro ultimamente.",
            "El tiempo cambia mucho por aqui.",
            "Esperemos que no llueva hoy!"
        ));
        RESPUESTAS.put("dia", Arrays.asList(
            "Si, es un dia precioso!",
            "Cada dia es una nueva oportunidad.",
            "El dia esta hermoso, verdad?"
        ));
    }

    public DialogoManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.conversacionActiva = new HashMap<>();
        this.npcEnConversacion = new HashMap<>();
        this.timeoutTasks = new HashMap<>();
        this.ultimoSaludo = new HashMap<>();
        this.random = new Random();
        startProximityDetector();
    }

    private void startProximityDetector() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
                if (npc.getLocation() == null) continue;
                if (npc.getMundo() == null) continue;
                org.bukkit.World world = plugin.getServer().getWorld(npc.getMundo());
                if (world == null) continue;

                for (Player player : world.getPlayers()) {
                    if (!npc.isNearby(player, 5)) continue;
                    if (conversacionActiva.containsKey(player.getUniqueId())) continue;

                    long ahora = System.currentTimeMillis();
                    long ultimaVez = ultimoSaludo.getOrDefault(
                        player.getUniqueId(), 0L);

                    if (ahora - ultimaVez < 60000) continue;

                    ultimoSaludo.put(player.getUniqueId(), ahora);
                    iniciarConversacion(npc, player);
                }
            }
        }, 20L, 40L);
    }

    public void iniciarConversacion(NPCEntity npc, Player player) {
        conversacionActiva.put(player.getUniqueId(), 0);
        npcEnConversacion.put(player.getUniqueId(), npc);

        String saludo = SALUDOS_NPC.get(random.nextInt(SALUDOS_NPC.size()));
        enviarMensajeNPC(npc, player, saludo);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (conversacionActiva.containsKey(player.getUniqueId())) {
                enviarMensajeNPC(npc, player,
                    "Escríbeme algo! Puedo entender: hola, adios, bien, mal, gracias, ayuda...");
            }
        }, 40L);

        iniciarTimeout(player);
    }

    public boolean procesarMensaje(Player player, String mensaje) {
        if (!conversacionActiva.containsKey(player.getUniqueId())) return false;
        NPCEntity npc = npcEnConversacion.get(player.getUniqueId());
        if (npc == null) return false;

        cancelarTimeout(player);

        String mensajeLower = mensaje.toLowerCase().trim();
        String respuesta = buscarRespuesta(npc, player, mensajeLower);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            enviarMensajeNPC(npc, player, respuesta);

            if (esDespartida(mensajeLower)) {
                terminarConversacion(player);
            } else {
                iniciarTimeout(player);
            }
        }, 20L);

        return true;
    }

    private String buscarRespuesta(NPCEntity npc, Player player, String mensaje) {
        for (Map.Entry<String, List<String>> entry : RESPUESTAS.entrySet()) {
            if (mensaje.contains(entry.getKey())) {
                List<String> respuestas = entry.getValue();
                String resp = respuestas.get(random.nextInt(respuestas.size()));
                resp = resp.replace("%jugador%", player.getName());
                resp = resp.replace("%npc%", npc.getNombre());
                return resp;
            }
        }

        if (mensaje.contains(npc.getNombre().toLowerCase())) {
            return "Si, ese soy yo! " + npc.getNombre() + " para servirte!";
        }

        List<String> defaultResp = Arrays.asList(
            "Interesante lo que dices...",
            "Hmm, no entiendo muy bien pero sigo aqui!",
            "Disculpa, no comprendi. Intentalo de otra forma.",
            "Puedo hablar de: hola, adios, bien, mal, gracias, ayuda, clima, trabajo.",
            "No estoy seguro de entender. Prueba con palabras simples."
        );
        return defaultResp.get(random.nextInt(defaultResp.size()));
    }

    private boolean esDespartida(String mensaje) {
        return mensaje.contains("adios") || mensaje.contains("bye") ||
               mensaje.contains("chao") || mensaje.contains("hasta") ||
               mensaje.contains("nos vemos");
    }

    private void terminarConversacion(Player player) {
        NPCEntity npc = npcEnConversacion.get(player.getUniqueId());
        if (npc != null) {
            String despedida = DESPEDIDAS_NPC.get(random.nextInt(DESPEDIDAS_NPC.size()));
            enviarMensajeNPC(npc, player, despedida);
        }
        cancelarTimeout(player);
        conversacionActiva.remove(player.getUniqueId());
        npcEnConversacion.remove(player.getUniqueId());
        ultimoSaludo.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void iniciarTimeout(Player player) {
        cancelarTimeout(player);
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (conversacionActiva.containsKey(player.getUniqueId())) {
                NPCEntity npc = npcEnConversacion.get(player.getUniqueId());
                if (npc != null) {
                    enviarMensajeNPC(npc, player,
                        "Parece que estas ocupado. Hablamos luego!");
                }
                conversacionActiva.remove(player.getUniqueId());
                npcEnConversacion.remove(player.getUniqueId());
            }
        }, 200L);
        timeoutTasks.put(player.getUniqueId(), task);
    }

    private void cancelarTimeout(Player player) {
        BukkitTask task = timeoutTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }

    private void enviarMensajeNPC(NPCEntity npc, Player player, String mensaje) {
        player.sendMessage(plugin.getMessageManager().color(
            "&8[&b" + npc.getNombre() + "&8] &f" + mensaje));
        plugin.getParticulasManager().spawnHearts(npc.getLocation());
    }

    public boolean estaEnConversacion(Player player) {
        return conversacionActiva.containsKey(player.getUniqueId());
    }

    public void limpiarConversacion(Player player) {
        cancelarTimeout(player);
        conversacionActiva.remove(player.getUniqueId());
        npcEnConversacion.remove(player.getUniqueId());
    }
}
