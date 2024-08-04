package org.achymake.harvester.listeners;

import org.achymake.harvester.Harvester;
import org.achymake.harvester.net.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record PlayerJoin(Harvester plugin) implements Listener {
    private UpdateChecker getUpdateChecker() {
        return plugin.getUpdateChecker();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("harvester.event.join.update"))return;
        getUpdateChecker().getUpdate(player);
    }
}
