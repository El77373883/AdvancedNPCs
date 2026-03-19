package com.soyadrianyt001.advancednpcs.commands;

import com.soyadrianyt001.advancednpcs.AdvancedNPCS;
import com.soyadrianyt001.advancednpcs.gui.PanelGUI;
import com.soyadrianyt001.advancednpcs.npc.NPCEntity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnpcCommand implements CommandExecutor {

    private final AdvancedNPCS plugin;

    public AnpcCommand(AdvancedNPCS plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }
        if (!player.isOp() && !player.hasPermission("advancednpcs.use")) {
            plugin.getMessageManager().send(player, "sin_permiso");
            return true;
        }
        if (args.length == 0) {
            plugin.getMessageManager().send(player, "comando_invalido");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "panel" -> {
                if (!player.isOp() && !player.hasPermission("advancednpcs.panel")) {
                    plugin.getMessageManager().send(player, "sin_permiso");
                    return true;
                }
                new PanelGUI(plugin).open(player);
            }
            case "create" -> {
                if (!player.isOp() && !player.hasPermission("advancednpcs.create")) {
                    plugin.getMessageManager().send(player, "sin_permiso");
                    return true;
                }
                if (args.length < 2) {
                    plugin.getMessageManager().sendWithPrefix(player, "&7Uso: &e/anpc create &8<nombre>");
                    return true;
                }
                String nombre = args[1];
                NPCEntity npc = plugin.getNPCManager().createNPC(nombre, player.getLocation(), player);
                plugin.getMessageManager().send(player, "npc_creado",
                    "%npc_id%", String.valueOf(npc.getId()));
            }
            case "delete" -> {
                if (!player.isOp() && !player.hasPermission("advancednpcs.delete")) {
                    plugin.getMessageManager().send(player, "sin_permiso");
                    return true;
                }
                if (args.length < 2) {
                    plugin.getMessageManager().sendWithPrefix(player, "&7Uso: &e/anpc delete &8<id>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    if (!plugin.getNPCManager().exists(id)) {
                        plugin.getMessageManager().send(player, "npc_no_existe", "%npc_id%", args[1]);
                        return true;
                    }
                    plugin.getNPCManager().deleteNPC(id);
                    plugin.getMessageManager().send(player, "npc_eliminado", "%npc_id%", args[1]);
                } catch (NumberFormatException e) {
                    plugin.getMessageManager().sendWithPrefix(player, "&cID invalido.");
                }
            }
            case "edit" -> {
                if (!player.isOp() && !player.hasPermission("advancednpcs.edit")) {
                    plugin.getMessageManager().send(player, "sin_permiso");
                    return true;
                }
                if (args.length < 2) {
                    plugin.getMessageManager().sendWithPrefix(player, "&7Uso: &e/anpc edit &8<id/nombre>");
                    return true;
                }
                NPCEntity npc = null;
                try {
                    int id = Integer.parseInt(args[1]);
                    npc = plugin.getNPCManager().getNPC(id);
                } catch (NumberFormatException e) {
                    npc = plugin.getNPCManager().getNPCByName(args[1]);
                }
                if (npc == null) {
                    plugin.getMessageManager().send(player, "npc_no_existe", "%npc_id%", args[1]);
                    return true;
                }
                new com.soyadrianyt001.advancednpcs.gui.ConfigGUI(plugin, npc).open(player);
            }
            case "move" -> {
                if (!player.isOp() && !player.hasPermission("advancednpcs.edit")) {
                    plugin.getMessageManager().send(player, "sin_permiso");
                    return true;
                }
                if (args.length < 2) {
                    plugin.getMessageManager().sendWithPrefix(player, "&7Uso: &e/anpc move &8<id>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    NPCEntity npc = plugin.getNPCManager().getNPC(id);
                    if (npc == null) {
                        plugin.getMessageManager().send(player, "npc_no_existe", "%npc_id%", args[1]);
                        return true;
                    }
                    plugin.getPacketManager().moveNPC(npc, player.getLocation());
                    npc.saveToConfig();
                    plugin.getMessageManager().sendWithPrefix(player, "&a✔ &7NPC movido a tu posicion.");
                } catch (NumberFormatException e) {
                    plugin.getMessageManager().sendWithPrefix(player, "&cID invalido.");
                }
            }
            case "list" -> {
                player.sendMessage(plugin.getMessageManager().color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
                player.sendMessage(plugin.getMessageManager().color("&b&l✦ &7Lista de NPCs &b&l✦"));
                for (NPCEntity npc : plugin.getNPCManager().getAllNPCs()) {
                    player.sendMessage(plugin.getMessageManager().color(
                        "&8#&e" + npc.getId() + " &7- &b" + npc.getNombre() +
                        " &8| &7Modo&8: &e" + npc.getModo() +
                        " &8| &7Profesion&8: &e" + npc.getProfesion()));
                }
                player.sendMessage(plugin.getMessageManager().color(
                    "&7Total&8: &e" + plugin.getNPCManager().getTotalNPCs() + " &7NPCs"));
                player.sendMessage(plugin.getMessageManager().color("&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
            }
            case "reload" -> {
                if (!player.isOp() && !player.hasPermission("advancednpcs.reload")) {
                    plugin.getMessageManager().send(player, "sin_permiso");
                    return true;
                }
                plugin.reloadConfig();
                plugin.getMessageManager().reload();
                plugin.getNPCManager().reloadAll();
                plugin.getMessageManager().send(player, "plugin_recargado");
            }
            case "follow" -> {
                if (args.length < 2) return true;
                try {
                    int id = Integer.parseInt(args[1]);
                    NPCEntity npc = plugin.getNPCManager().getNPC(id);
                    if (npc == null) return true;
                    npc.setSeguirActivo(!npc.isSeguirActivo());
                    npc.saveToConfig();
                    if (npc.isSeguirActivo()) {
                        plugin.getMessageManager().send(player, "follow_activado",
                            "%nombre_npc%", npc.getNombre());
                    } else {
                        plugin.getMessageManager().send(player, "follow_desactivado",
                            "%nombre_npc%", npc.getNombre());
                    }
                } catch (NumberFormatException e) {
                    plugin.getMessageManager().sendWithPrefix(player, "&cID invalido.");
                }
            }
            case "combat" -> {
                if (args.length < 2) return true;
                try {
                    int id = Integer.parseInt(args[1]);
                    NPCEntity npc = plugin.getNPCManager().getNPC(id);
                    if (npc == null) return true;
                    npc.setCombateActivo(!npc.isCombateActivo());
                    npc.saveToConfig();
                    plugin.getMessageManager().sendWithPrefix(player,
                        "&7Combate del NPC &e" + npc.getNombre() + " &7: " +
                        (npc.isCombateActivo() ? "&aACTIVADO" : "&cDESACTIVADO"));
                } catch (NumberFormatException e) {
                    plugin.getMessageManager().sendWithPrefix(player, "&cID invalido.");
                }
            }
            case "version" -> {
                player.sendMessage(plugin.getMessageManager().color("&8╔══════════════════════════════════════════╗"));
                player.sendMessage(plugin.getMessageManager().color("&8║   &b&l⚡ AdvancedNPCS &5✦ &d&lPremium &8- &7Version   ║"));
                player.sendMessage(plugin.getMessageManager().color("&8╠══════════════════════════════════════════╣"));
                player.sendMessage(plugin.getMessageManager().color("&8║   &7Version actual&8:  &e" + plugin.getVersionChecker().getCurrentVersion() + "                ║"));
                if (plugin.getVersionChecker().isUpdated()) {
                    player.sendMessage(plugin.getMessageManager().color("&8║   &7Estado&8:         &a&l✔ Estas actualizado!    ║"));
                } else {
                    player.sendMessage(plugin.getMessageManager().color("&8║   &7Nueva version&8:  &a" + plugin.getVersionChecker().getLatestVersion() + "               ║"));
                    player.sendMessage(plugin.getMessageManager().color("&8║   &6&l⚠ &e¡Hay una nueva version disponible!    ║"));
                }
                player.sendMessage(plugin.getMessageManager().color("&8╠══════════════════════════════════════════╣"));
                player.sendMessage(plugin.getMessageManager().color("&8║   &5✦ &dAdvancedNPCS Premium &8| &bsoyadrianyt001 &5✦  ║"));
                player.sendMessage(plugin.getMessageManager().color("&8╚══════════════════════════════════════════╝"));
            }
            case "creator" -> {
                player.sendMessage(plugin.getMessageManager().color("&8╔════════════════════════════════════════════╗"));
                player.sendMessage(plugin.getMessageManager().color("&8║                                            ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║   &b&l  ⚡ AdvancedNPCS &5✦ &d&lPremium ⚡            ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║                                            ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║   &7Este plugin fue creado con &d&l♥ &7por&8:        ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║                                            ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║        &b&l★ &e&lsoyadrianyt001 &b&l★               ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║                                            ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║   &5✦ &dDesarrollador Principal &5✦               ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║   &7GitHub&8: &bgithub.com/soyadrianyt001          ║"));
                player.sendMessage(plugin.getMessageManager().color("&8║                                            ║"));
                player.sendMessage(plugin.getMessageManager().color("&8╚════════════════════════════════════════════╝"));
                player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
            case "logs" -> {
                if (!player.isOp()) {
                    plugin.getMessageManager().send(player, "sin_permiso");
                    return true;
                }
                if (args.length < 2) return true;
                if (args[1].equalsIgnoreCase("all")) {
                    plugin.getMessageManager().sendWithPrefix(player, "&7Usa &e/anpc logs <id> &7para ver logs especificos.");
                    return true;
                }
                if (args[1].equalsIgnoreCase("clear") && args.length >= 3) {
                    if (args[2].equalsIgnoreCase("all")) {
                        plugin.getLogManager().clearAllLogs();
                        plugin.getMessageManager().sendWithPrefix(player, "&a✔ &7Todos los logs eliminados.");
                    } else {
                        try {
                            int id = Integer.parseInt(args[2]);
                            plugin.getLogManager().clearLogs(id);
                            plugin.getMessageManager().sendWithPrefix(player, "&a✔ &7Logs del NPC &e" + id + " &7eliminados.");
                        } catch (NumberFormatException e) {
                            plugin.getMessageManager().sendWithPrefix(player, "&cID invalido.");
                        }
                    }
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    java.util.List<String> logs = plugin.getLogManager().getLogs(id);
                    player.sendMessage(plugin.getMessageManager().color("&b&l✦ &7Logs del NPC &e#" + id + " &b&l✦"));
                    if (logs.isEmpty()) {
                        player.sendMessage(plugin.getMessageManager().color("&7Sin logs disponibles."));
                    } else {
                        for (String log : logs) {
                            player.sendMessage(plugin.getMessageManager().color("&7" + log));
                        }
                    }
                } catch (NumberFormatException e) {
                    plugin.getMessageManager().sendWithPrefix(player, "&cID invalido.");
                }
            }
            case "help" -> sendHelp(player);
            default -> plugin.getMessageManager().send(player, "comando_invalido");
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(plugin.getMessageManager().color("&8╔══════════════════════════════════════════════╗"));
        player.sendMessage(plugin.getMessageManager().color("&8║      &b&l⚡ AdvancedNPCS &5✦ &d&lPremium &b⚡ &r&8            ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║      &7Creado por &bsoyadrianyt001                 ║"));
        player.sendMessage(plugin.getMessageManager().color("&8╠══════════════════════════════════════════════╣"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc panel              &7→ &aAbrir panel        ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc create &8<nombre>   &7→ &aCrear NPC         ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc delete &8<id>       &7→ &cEliminar NPC      ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc edit &8<id/nombre>  &7→ &bEditar NPC        ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc move &8<id>         &7→ &6Mover NPC         ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc list               &7→ &7Ver todos          ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc follow &8<id>       &7→ &aSeguidor toggle    ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc combat &8<id>       &7→ &cCombate toggle     ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc logs &8<id>         &7→ &7Ver logs           ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc reload             &7→ &aRecargar config    ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc version            &7→ &7Ver version        ║"));
        player.sendMessage(plugin.getMessageManager().color("&8║  &e/anpc creator            &7→ &dVer autor          ║"));
        player.sendMessage(plugin.getMessageManager().color("&8╠══════════════════════════════════════════════╣"));
        player.sendMessage(plugin.getMessageManager().color("&8║   &5✦ &dAdvancedNPCS Premium &8| &7v1.0.0 &8| &bPaper 1.21.1 ║"));
        player.sendMessage(plugin.getMessageManager().color("&8╚══════════════════════════════════════════════╝"));
    }
}
