package com.soyadrianyt001.advancednpcs;

import com.soyadrianyt001.advancednpcs.commands.AnpcCommand;
import com.soyadrianyt001.advancednpcs.commands.QuestCommand;
import com.soyadrianyt001.advancednpcs.commands.RepCommand;
import com.soyadrianyt001.advancednpcs.managers.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedNPCS extends JavaPlugin {

    private static AdvancedNPCS instance;
    private Economy economy;
    private NPCManager npcManager;
    private DataManager dataManager;
    private MessageManager messageManager;
    private PacketManager packetManager;
    private TrabajoManager trabajoManager;
    private FamiliaManager familiaManager;
    private PoliciaManager policiaManager;
    private BancoManager bancoManager;
    private MisionManager misionManager;
    private LogManager logManager;
    private VersionChecker versionChecker;
    private PartikulasManager particulasManager;
    private ClimatiManager climatiManager;
    private ConfirmCallbacks confirmCallbacks;
    private ListenerManager listenerManager;
    private SkinManager skinManager;
    private DialogoManager dialogoManager;

    @Override
    public void onEnable() {
        instance = this;
        printStartupMessage();
        saveDefaultConfig();
        saveResource("messages.yml", false);
        if (!setupEconomy()) {
            getLogger().severe("Vault no encontrado. Desactivando AdvancedNPCS.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        initManagers();
        registerCommands();
        versionChecker.check();
        getLogger().info("AdvancedNPCS Premium cargado correctamente.");
    }

    @Override
    public void onDisable() {
        if (packetManager != null) packetManager.despawnAll();
        if (npcManager != null) npcManager.saveAll();
        if (dataManager != null) dataManager.saveAll();
        printShutdownMessage();
    }

    private void printStartupMessage() {
        Bukkit.getConsoleSender().sendMessage("§8╔══════════════════════════════════════════╗");
        Bukkit.getConsoleSender().sendMessage("§8║  §b§l  Advanced§3§lNPCS §8- §d§lPremium Edition        ║");
        Bukkit.getConsoleSender().sendMessage("§8║  §7Version §e1.0.0 §7| §7Paper §e1.21.1              ║");
        Bukkit.getConsoleSender().sendMessage("§8║  §7Autor§8: §bsoyadrianyt001                    ║");
        Bukkit.getConsoleSender().sendMessage("§8║  §7Estado§8: §a✔ Plugin cargado correctamente   ║");
        Bukkit.getConsoleSender().sendMessage("§8║  §5✦ §dGracias por usar AdvancedNPCS Premium §5✦  ║");
        Bukkit.getConsoleSender().sendMessage("§8╚══════════════════════════════════════════╝");
    }

    private void printShutdownMessage() {
        Bukkit.getConsoleSender().sendMessage("§8╔══════════════════════════════════════════╗");
        Bukkit.getConsoleSender().sendMessage("§8║   §b§lAdvancedNPCS §d§lPremium §7se esta apagando...  ║");
        Bukkit.getConsoleSender().sendMessage("§8║   §7Guardando datos de NPCs§8... §a✔ Completado  ║");
        Bukkit.getConsoleSender().sendMessage("§8║   §5✦ §dHasta pronto §8- §bsoyadrianyt001 §5✦        ║");
        Bukkit.getConsoleSender().sendMessage("§8╚══════════════════════════════════════════╝");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp =
            getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    private void initManagers() {
        dataManager = new DataManager(this);
        messageManager = new MessageManager(this);
        logManager = new LogManager(this);
        skinManager = new SkinManager(this);
        packetManager = new PacketManager(this);
        confirmCallbacks = new ConfirmCallbacks(this);
        trabajoManager = new TrabajoManager(this);
        familiaManager = new FamiliaManager(this);
        policiaManager = new PoliciaManager(this);
        bancoManager = new BancoManager(this);
        misionManager = new MisionManager(this);
        particulasManager = new PartikulasManager(this);
        climatiManager = new ClimatiManager(this);
        versionChecker = new VersionChecker(this);
        dialogoManager = new DialogoManager(this);
        npcManager = new NPCManager(this);
        listenerManager = new ListenerManager(this);
    }

    private void registerCommands() {
        getCommand("anpc").setExecutor(new AnpcCommand(this));
        getCommand("quest").setExecutor(new QuestCommand(this));
        getCommand("rep").setExecutor(new RepCommand(this));
    }

    public static AdvancedNPCS getInstance() { return instance; }
    public Economy getEconomy() { return economy; }
    public NPCManager getNPCManager() { return npcManager; }
    public DataManager getDataManager() { return dataManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public PacketManager getPacketManager() { return packetManager; }
    public TrabajoManager getTrabajoManager() { return trabajoManager; }
    public FamiliaManager getFamiliaManager() { return familiaManager; }
    public PoliciaManager getPoliciaManager() { return policiaManager; }
    public BancoManager getBancoManager() { return bancoManager; }
    public MisionManager getMisionManager() { return misionManager; }
    public LogManager getLogManager() { return logManager; }
    public VersionChecker getVersionChecker() { return versionChecker; }
    public PartikulasManager getParticulasManager() { return particulasManager; }
    public ClimatiManager getClimatiManager() { return climatiManager; }
    public ConfirmCallbacks getConfirmCallbacks() { return confirmCallbacks; }
    public ListenerManager getListeners() { return listenerManager; }
    public SkinManager getSkinManager() { return skinManager; }
    public DialogoManager getDialogoManager() { return dialogoManager; }
}
