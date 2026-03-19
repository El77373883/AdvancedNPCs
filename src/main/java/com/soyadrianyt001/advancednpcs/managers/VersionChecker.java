package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class VersionChecker {

    private final AdvancedNPCS plugin;
    private String latestVersion;
    private final String currentVersion;

    public VersionChecker(AdvancedNPCS plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void check() {
        if (!plugin.getConfig().getBoolean("version_checker.activado", true)) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String url = plugin.getConfig().getString("version_checker.url",
                    "https://raw.githubusercontent.com/soyadrianyt001/AdvancedNPCS/main/version.txt");
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new URL(url).openStream()));
                latestVersion = reader.readLine().trim();
                reader.close();
                if (!latestVersion.equals(currentVersion)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getConsoleSender().sendMessage(
                            plugin.getMessageManager().color(
                                "&8[&b&lAdvancedNPCS &5✦ &d&lPremium&8] &6⚠ &7Nueva version disponible: &av" + latestVersion));
                    });
                }
            } catch (Exception e) {
                plugin.getLogger().warning("No se pudo verificar la version.");
            }
        });
        long interval = plugin.getConfig().getLong("version_checker.intervalo_minutos", 30) * 60 * 20;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::check, interval, interval);
    }

    public void notifyPlayer(Player player) {
        if (!plugin.getConfig().getBoolean("version_checker.avisar_al_admin_al_entrar", true)) return;
        if (!player.isOp() && !player.hasPermission("advancednpcs.versioncheck")) return;
        if (latestVersion == null || latestVersion.equals(currentVersion)) return;
        player.sendMessage(plugin.getMessageManager().get("version_desactualizada",
            "%nueva_version%", latestVersion));
    }

    public String getCurrentVersion() { return currentVersion; }
    public String getLatestVersion() { return latestVersion; }
    public boolean isUpdated() {
        if (latestVersion == null) return true;
        return latestVersion.equals(currentVersion);
    }
}
