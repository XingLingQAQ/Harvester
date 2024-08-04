package org.achymake.harvester.commands;

import org.achymake.harvester.Harvester;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HarvesterCommand implements CommandExecutor, TabCompleter {
    private final Harvester plugin;
    public HarvesterCommand(Harvester plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                plugin.send(player, "&6" + plugin.getDescription().getName() + " " + plugin.getDescription().getVersion());
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reload();
                    plugin.send(player, "&6Harvester:&f reloaded");
                    return true;
                }
            }
        } else if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 0) {
                plugin.send(consoleCommandSender, plugin.getDescription().getName() + " " + plugin.getDescription().getVersion());
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reload();
                    plugin.send(consoleCommandSender, "Harvester: reloaded");
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                commands.add("reload");
            }
        }
        return commands;
    }
}