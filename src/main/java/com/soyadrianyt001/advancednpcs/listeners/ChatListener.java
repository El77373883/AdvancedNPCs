package com.soyadrianyt001.advancednpcs.listeners;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatListener implements Listener {

    private final AdvancedNPCS plugin;
    private final Map<UUID, Consumer<String>> pendingInputs;

    public ChatListener(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.pendingInputs = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!pendingInputs.containsKey(uuid)) return;
        event.setCancelled(true);
        String message = event.getMessage();
        Consumer<String> callback = pendingInputs.remove(uuid);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (message.equalsIgnoreCase("cancel")) {
                plugin.getMessageManager().send(player, "cancelado");
                return;
            }
            callback.accept(message);
        });
    }

    public void awaitInput(Player player, String prompt, Consumer<String> callback) {
        player.sendMessage(plugin.getMessageManager().color(
            "&8╔══════════════════════════════════════════╗"));
        player.sendMessage(plugin.getMessageManager().color(
            "&8║   &b&l⚡ AdvancedNPCS &5✦ &d&lPremium ⚡ &r&8        ║"));
        player.sendMessage(plugin.getMessageManager().color(
            "&8╠══════════════════════════════════════════╣"));
        player.sendMessage(plugin.getMessageManager().color(
            "&8║   &7" + prompt + "        ║"));
        player.sendMessage(plugin.getMessageManager().color(
            "&8║   &cEscribe &4cancel &cpara cancelar.         ║"));
        player.sendMessage(plugin.getMessageManager().color(
            "&8╚══════════════════════════════════════════╝"));
        player.playSound(player.getLocation(),
            org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        pendingInputs.put(player.getUniqueId(), callback);
    }

    public boolean hasPendingInput(Player player) {
        return pendingInputs.containsKey(player.getUniqueId());
    }

    public void cancelInput(Player player) {
        pendingInputs.remove(player.getUniqueId());
    }
}
