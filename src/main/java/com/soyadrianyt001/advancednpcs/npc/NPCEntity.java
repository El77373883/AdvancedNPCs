package com.soyadrianyt001.advancednpcs.npc;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCEntity {

    private final AdvancedNPCS plugin;
    private final int id;
    private String nombre;
    private String skin;
    private String modo;
    private String tipo;
    private String profesion;
    private String estado;
    private String emocion;
    private String faccion;
    private String mundo;
    private double x, y, z;
    private float yaw, pitch;
    private double vidaMaxima;
    private double vidaActual;
    private double escala;
    private boolean tiendaActiva;
    private boolean dormirActivo;
    private boolean comerActivo;
    private boolean caminarActivo;
    private boolean seguirActivo;
    private boolean combateActivo;
    private boolean inventarioActivo;
    private List<String> particulas;
    private List<String> efectos;
    private List<String> comandosAlClick;
    private int familiaEsposaId;
    private int familiaHijoId;
    private int familiaPadreId;
    private boolean esEsposa;
    private boolean esHijo;
    private UUID skinUUID;

    public NPCEntity(AdvancedNPCS plugin, int id) {
        this.plugin = plugin;
        this.id = id;
        this.particulas = new ArrayList<>();
        this.efectos = new ArrayList<>();
        this.comandosAlClick = new ArrayList<>();
        this.familiaEsposaId = -1;
        this.familiaHijoId = -1;
        this.familiaPadreId = -1;
        this.profesion = "NINGUNA";
        this.faccion = "NINGUNA";
        this.escala = 1.0;
        this.vidaMaxima = 20;
        this.vidaActual = 20;
    }

    public void spawn() {
        if (mundo == null) return;
        plugin.getPacketManager().spawnNPC(this);
        plugin.getPacketManager().updateNameTag(this);
        if (!particulas.isEmpty()) {
            plugin.getParticulasManager().startParticles(this);
        }
    }

    public void despawn() {
        plugin.getPacketManager().despawnNPC(this);
        plugin.getParticulasManager().stopParticles(this);
    }

    public void respawn() {
        despawn();
        spawn();
    }

    public void saveToConfig() {
        FileConfiguration config = new YamlConfiguration();
        config.set("id", id);
        config.set("nombre", nombre);
        config.set("skin", skin);
        config.set("modo", modo);
        config.set("tipo", tipo);
        config.set("profesion", profesion);
        config.set("estado", estado);
        config.set("emocion", emocion);
        config.set("faccion", faccion);
        config.set("mundo", mundo);
        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("yaw", yaw);
        config.set("pitch", pitch);
        config.set("vida_maxima", vidaMaxima);
        config.set("vida_actual", vidaActual);
        config.set("escala", escala);
        config.set("tienda_activa", tiendaActiva);
        config.set("dormir_activo", dormirActivo);
        config.set("comer_activo", comerActivo);
        config.set("caminar_activo", caminarActivo);
        config.set("seguir_activo", seguirActivo);
        config.set("combate_activo", combateActivo);
        config.set("inventario_activo", inventarioActivo);
        config.set("particulas", particulas);
        config.set("efectos", efectos);
        config.set("comandos_click", comandosAlClick);
        config.set("familia.esposa_id", familiaEsposaId);
        config.set("familia.hijo_id", familiaHijoId);
        config.set("familia.padre_id", familiaPadreId);
        config.set("familia.es_esposa", esEsposa);
        config.set("familia.es_hijo", esHijo);
        plugin.getDataManager().saveNPCConfig(id, config);
    }

    public void loadFromConfig(FileConfiguration config) {
        this.nombre = config.getString("nombre", "NPC");
        this.skin = config.getString("skin", "Steve");
        this.modo = config.getString("modo", "ESTATICO");
        this.tipo = config.getString("tipo", "PLAYER");
        this.profesion = config.getString("profesion", "NINGUNA");
        this.estado = config.getString("estado", "ACTIVO");
        this.emocion = config.getString("emocion", "NEUTRAL");
        this.faccion = config.getString("faccion", "NINGUNA");
        this.mundo = config.getString("mundo");
        this.x = config.getDouble("x");
        this.y = config.getDouble("y");
        this.z = config.getDouble("z");
        this.yaw = (float) config.getDouble("yaw");
        this.pitch = (float) config.getDouble("pitch");
        this.vidaMaxima = config.getDouble("vida_maxima", 20);
        this.vidaActual = config.getDouble("vida_actual", 20);
        this.escala = config.getDouble("escala", 1.0);
        this.tiendaActiva = config.getBoolean("tienda_activa");
        this.dormirActivo = config.getBoolean("dormir_activo");
        this.comerActivo = config.getBoolean("comer_activo");
        this.caminarActivo = config.getBoolean("caminar_activo");
        this.seguirActivo = config.getBoolean("seguir_activo");
        this.combateActivo = config.getBoolean("combate_activo");
        this.inventarioActivo = config.getBoolean("inventario_activo");
        this.particulas = config.getStringList("particulas");
        this.efectos = config.getStringList("efectos");
        this.comandosAlClick = config.getStringList("comandos_click");
        this.familiaEsposaId = config.getInt("familia.esposa_id", -1);
        this.familiaHijoId = config.getInt("familia.hijo_id", -1);
        this.familiaPadreId = config.getInt("familia.padre_id", -1);
        this.esEsposa = config.getBoolean("familia.es_esposa");
        this.esHijo = config.getBoolean("familia.es_hijo");
    }

    public Location getLocation() {
        if (mundo == null) return null;
        return new Location(
            plugin.getServer().getWorld(mundo),
            x, y, z, yaw, pitch
        );
    }

    public void setLocation(Location loc) {
        this.mundo = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public boolean isNearby(Player player, double range) {
        Location loc = getLocation();
        if (loc == null) return false;
        if (!player.getWorld().getName().equals(mundo)) return false;
        return player.getLocation().distance(loc) <= range;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSkin() { return skin; }
    public void setSkin(String skin) { this.skin = skin; }
    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getProfesion() { return profesion; }
    public void setProfesion(String profesion) { this.profesion = profesion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getEmocion() { return emocion; }
    public void setEmocion(String emocion) { this.emocion = emocion; }
    public String getFaccion() { return faccion; }
    public void setFaccion(String faccion) { this.faccion = faccion; }
    public String getMundo() { return mundo; }
    public double getVidaMaxima() { return vidaMaxima; }
    public void setVidaMaxima(double vidaMaxima) { this.vidaMaxima = vidaMaxima; }
    public double getVidaActual() { return vidaActual; }
    public void setVidaActual(double vidaActual) { this.vidaActual = vidaActual; }
    public double getEscala() { return escala; }
    public void setEscala(double escala) { this.escala = escala; }
    public boolean isTiendaActiva() { return tiendaActiva; }
    public void setTiendaActiva(boolean tiendaActiva) { this.tiendaActiva = tiendaActiva; }
    public boolean isDormirActivo() { return dormirActivo; }
    public void setDormirActivo(boolean dormirActivo) { this.dormirActivo = dormirActivo; }
    public boolean isComerActivo() { return comerActivo; }
    public void setComerActivo(boolean comerActivo) { this.comerActivo = comerActivo; }
    public boolean isCaminarActivo() { return caminarActivo; }
    public void setCaminarActivo(boolean caminarActivo) { this.caminarActivo = caminarActivo; }
    public boolean isSeguirActivo() { return seguirActivo; }
    public void setSeguirActivo(boolean seguirActivo) { this.seguirActivo = seguirActivo; }
    public boolean isCombateActivo() { return combateActivo; }
    public void setCombateActivo(boolean combateActivo) { this.combateActivo = combateActivo; }
    public boolean isInventarioActivo() { return inventarioActivo; }
    public void setInventarioActivo(boolean inventarioActivo) { this.inventarioActivo = inventarioActivo; }
    public List<String> getParticulas() { return particulas; }
    public void setParticulas(List<String> particulas) { this.particulas = particulas; }
    public List<String> getEfectos() { return efectos; }
    public void setEfectos(List<String> efectos) { this.efectos = efectos; }
    public List<String> getComandosAlClick() { return comandosAlClick; }
    public int getFamiliaEsposaId() { return familiaEsposaId; }
    public void setFamiliaEsposaId(int id) { this.familiaEsposaId = id; }
    public int getFamiliaHijoId() { return familiaHijoId; }
    public void setFamiliaHijoId(int id) { this.familiaHijoId = id; }
    public int getFamiliaPadreId() { return familiaPadreId; }
    public void setFamiliaPadreId(int id) { this.familiaPadreId = id; }
    public boolean isEsEsposa() { return esEsposa; }
    public void setEsEsposa(boolean esEsposa) { this.esEsposa = esEsposa; }
    public boolean isEsHijo() { return esHijo; }
    public void setEsHijo(boolean esHijo) { this.esHijo = esHijo; }
}
