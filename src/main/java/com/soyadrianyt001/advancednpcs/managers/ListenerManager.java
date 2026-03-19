package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.listeners.ChatListener;
import com.soyadrianyt001.advancednpcs.listeners.CombateListener;
import com.soyadrianyt001.advancednpcs.listeners.GUIListener;
import com.soyadrianyt001.advancednpcs.listeners.NPCListener;
import com.soyadrianyt001.advancednpcs.listeners.PlayerListener;

public class ListenerManager {

    private final AdvancedNPCS plugin;
    private final ChatListener chatListener;
    private final NPCListener npcListener;
    private final GUIListener guiListener;
    private final PlayerListener playerListener;
    private final CombateListener combateListener;

    public ListenerManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.chatListener = new ChatListener(plugin);
        this.npcListener = new NPCListener(plugin);
        this.guiListener = new GUIListener(plugin);
        this.playerListener = new PlayerListener(plugin);
        this.combateListener = new CombateListener(plugin);
        registerAll();
    }

    private void registerAll() {
        plugin.getServer().getPluginManager().registerEvents(chatListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(npcListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(guiListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(playerListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(combateListener, plugin);
    }

    public ChatListener getChatListener() { return chatListener; }
    public NPCListener getNpcListener() { return npcListener; }
    public GUIListener getGuiListener() { return guiListener; }
    public PlayerListener getPlayerListener() { return playerListener; }
    public CombateListener getCombateListener() { return combateListener; }
}
