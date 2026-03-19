package com.soyadrianyt001.advancednpcs.managers;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;

public class BancoManager {

    private final AdvancedNPCS plugin;
    private BukkitTask interesTask;

    public BancoManager(AdvancedNPCS plugin) {
        this.plugin = plugin;
        startInteres();
    }

    public double getSaldo(Player player) {
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        return data.getDouble("banco.saldo", 0);
    }

    public boolean depositar(Player player, double cantidad) {
        if (!plugin.getEconomy().has(player, cantidad)) return false;
        double limite = plugin.getConfig().getDouble("banco.limite_deposito", 1000000);
        double saldoActual = getSaldo(player);
        if (saldoActual + cantidad > limite) return false;
        plugin.getEconomy().withdrawPlayer(player, cantidad);
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        data.set("banco.saldo", saldoActual + cantidad);
        plugin.getDataManager().savePlayerData(player.getUniqueId());
        plugin.getMessageManager().sendWithPrefix(player,
            "&a✔ &7Depositaste &e$" + cantidad + " &7en el banco.");
        return true;
    }

    public boolean retirar(Player player, double cantidad) {
        double saldo = getSaldo(player);
        if (saldo < cantidad) return false;
        plugin.getEconomy().depositPlayer(player, cantidad);
        FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
        data.set("banco.saldo", saldo - cantidad);
        plugin.getDataManager().savePlayerData(player.getUniqueId());
        plugin.getMessageManager().sendWithPrefix(player,
            "&a✔ &7Retiraste &e$" + cantidad + " &7del banco.");
        return true;
    }

    private void startInteres() {
        if (!plugin.getConfig().getBoolean("banco.interes.activado", true)) return;
        double porcentaje = plugin.getConfig().getDouble("banco.interes.porcentaje", 2);
        long horas = plugin.getConfig().getLong("banco.interes.intervalo_horas", 24);
        long ticks = horas * 72000L;
        interesTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
                double saldo = getSaldo(player);
                if (saldo <= 0) continue;
                double interes = saldo * (porcentaje / 100.0);
                FileConfiguration data = plugin.getDataManager().getPlayerData(player.getUniqueId());
                data.set("banco.saldo", saldo + interes);
                plugin.getDataManager().savePlayerData(player.getUniqueId());
                plugin.getMessageManager().sendWithPrefix(player,
                    "&e💰 &7Has recibido &a$" + String.format("%.2f", interes) + " &7de interes bancario.");
            }
        }, ticks, ticks);
    }
}
