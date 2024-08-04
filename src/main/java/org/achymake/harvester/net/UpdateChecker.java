package org.achymake.harvester.net;

import org.achymake.harvester.Harvester;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public record UpdateChecker(Harvester plugin) {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private BukkitScheduler getScheduler() {
        return plugin.getScheduler();
    }
    public void getUpdate(Player player) {
        if (notifyUpdate()) {
            getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    getLatest((latest) -> {
                        if (!plugin.version().equals(latest)) {
                            plugin.send(player, plugin.name() + "&6 has new update:");
                            plugin.send(player, "-&a https://www.spigotmc.org/resources/115479/");
                        }
                    });
                }
            }, 8);
        }
    }
    public void getUpdate() {
        if (notifyUpdate()) {
            getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    getLatest((latest) -> {
                        if (plugin.version().equals(latest)) {
                            plugin.sendLog(Level.INFO, "You are using the latest version");
                        } else {
                            plugin.sendLog(Level.INFO, plugin.name() + " has new update:");
                            plugin.sendLog(Level.INFO, "- https://www.spigotmc.org/resources/115479/");
                        }
                    });
                }
            });
        }
    }
    public void getLatest(Consumer<String> consumer) {
        try {
            InputStream inputStream = (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 115479)).openStream();
            Scanner scanner = new Scanner(inputStream);
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
                scanner.close();
            }
            inputStream.close();
        } catch (IOException e) {
            plugin.sendLog(Level.WARNING, e.getMessage());
        }
    }
    private boolean notifyUpdate() {
        return getConfig().getBoolean("notify-update");
    }
}
