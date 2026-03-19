package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ConfirmCallbacks {

    private final AdvancedNPCS plugin;
    private final Map<UUID, Consumer<Boolean>> callbacks;

    public ConfirmCallbacks(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.callbacks = new HashMap<>();
    }

    public void register(Player player, Consumer<Boolean> callback) {
        callbacks.put(player.getUniqueId(), callback);
    }

    public void execute(Player player, boolean confirmed) {
        Consumer<Boolean> callback = callbacks.remove(player.getUniqueId());
        if (callback != null) {
            callback.accept(confirmed);
        }
        if (confirmed) {
            player.playSound(player.getLocation(),
                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {
            player.playSound(player.getLocation(),
                org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        }
    }

    public boolean hasPending(Player player) {
        return callbacks.containsKey(player.getUniqueId());
    }

    public void cancel(Player player) {
        callbacks.remove(player.getUniqueId());
    }
}
