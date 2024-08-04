package org.achymake.harvester.listeners;

import org.achymake.harvester.Harvester;
import org.achymake.harvester.listeners.custom.HarvestEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public record PlayerInteract(Harvester plugin) implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() == null)return;
            if (event.getHand() != EquipmentSlot.HAND)return;
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (!plugin.isHoe(player.getInventory().getItemInMainHand()))return;
            if (!plugin.isEnable(block))return;
            if (!plugin.isRightAge(block))return;
            if (!plugin.isAllowHarvest(block))return;
            plugin.getManager().callEvent(new HarvestEvent(player, block));
        }
    }
}