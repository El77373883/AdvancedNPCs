package com.soyadrianyt001.advancednpcs.commands;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestCommand implements CommandExecutor {

    private final AdvancedNPCS plugin;

    public QuestCommand(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }
        if (!player.hasPermission("advancednpcs.quest.use") && !player.isOp()) {
            plugin.getMessageManager().send(player, "sin_permiso");
            return true;
        }
        if (args.length == 0) {
            sendQuestHelp(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list" -> {
                plugin.getMisionManager().listQuests(player);
            }
            case "reset" -> {
                if (!player.isOp()) {
                    plugin.getMessageManager().send(player, "sin_permiso");
                    return true;
                }
                if (args.length < 3) {
                    plugin.getMessageManager().sendWithPrefix(player,
                        "&7Uso: &e/quest reset &8<jugador> &8<id>");
                    return true;
                }
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    plugin.getMessageManager().sendWithPrefix(player, "&cJugador no encontrado.");
                    return true;
                }
                plugin.getMisionManager().resetQuest(target, args[2]);
                plugin.getMessageManager().sendWithPrefix(player,
                    "&a✔ &7Mision &e" + args[2] + " &7reseteada para &e" + target.getName());
            }
            default -> sendQuestHelp(player);
        }
        return true;
    }

    private void sendQuestHelp(Player player) {
        player.sendMessage(plugin.getMessageManager().color("&b&l✦ &7Comandos de Misiones &b&l✦"));
        player.sendMessage(plugin.getMessageManager().color("&e/quest list &7- Ver misiones disponibles"));
        player.sendMessage(plugin.getMessageManager().color("&e/quest reset &8<jugador> &8<id> &7- Resetear mision"));
    }
}
