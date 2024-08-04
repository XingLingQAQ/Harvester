package org.achymake.harvester.listeners;

import org.achymake.harvester.Harvester;
import org.achymake.harvester.listeners.custom.HarvestEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Random;

public record Harvest(Harvester plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHarvest(HarvestEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (event.isCancelled())return;
        if (!getConfig().getBoolean("enable"))return;
        if (!player.hasPermission("harvester.event.harvest." + block.getType().toString().toLowerCase()))return;
        if (player.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_HOE)) {
            if (!player.hasPermission("harvester.event.harvest.item.wooden_hoe"))return;
            resetAge(block);
            player.swingMainHand();
            playHarvestSound(player, block);
            dropItems(player, block);
            dropExperience(player, block);
            addDamage(player.getInventory().getItemInMainHand(), getConfig().getInt("crops." + block.getType() + ".damage"));
            isDestroyed(player, player.getInventory().getItemInMainHand());
        } else if (player.getInventory().getItemInMainHand().getType().equals(Material.STONE_HOE)) {
            if (!player.hasPermission("harvester.event.harvest.item.stone_hoe"))return;
            resetAge(block);
            player.swingMainHand();
            playHarvestSound(player, block);
            dropItems(player, block);
            dropExperience(player, block);
            addDamage(player.getInventory().getItemInMainHand(), getConfig().getInt("crops." + block.getType() + ".damage"));
            isDestroyed(player, player.getInventory().getItemInMainHand());
        } else if (player.getInventory().getItemInMainHand().getType().equals(Material.IRON_HOE)) {
            if (!player.hasPermission("harvester.event.harvest.item.iron_hoe"))return;
            resetAge(block);
            player.swingMainHand();
            playHarvestSound(player, block);
            dropItems(player, block);
            dropExperience(player, block);
            addDamage(player.getInventory().getItemInMainHand(), getConfig().getInt("crops." + block.getType() + ".damage"));
            isDestroyed(player, player.getInventory().getItemInMainHand());
        } else if (player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_HOE)) {
            if (!player.hasPermission("harvester.event.harvest.item.golden_hoe"))return;
            resetAge(block);
            player.swingMainHand();
            playHarvestSound(player, block);
            dropItems(player, block);
            dropExperience(player, block);
            addDamage(player.getInventory().getItemInMainHand(), getConfig().getInt("crops." + block.getType() + ".damage"));
            isDestroyed(player, player.getInventory().getItemInMainHand());
        } else if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_HOE)) {
            if (!player.hasPermission("harvester.event.harvest.item.diamond_hoe"))return;
            resetAge(block);
            player.swingMainHand();
            playHarvestSound(player, block);
            dropItems(player, block);
            dropExperience(player, block);
            addDamage(player.getInventory().getItemInMainHand(), getConfig().getInt("crops." + block.getType() + ".damage"));
            isDestroyed(player, player.getInventory().getItemInMainHand());
        } else if (player.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_HOE)) {
            if (!player.hasPermission("harvester.event.harvest.item.netherite_hoe"))return;
            resetAge(block);
            player.swingMainHand();
            playHarvestSound(player, block);
            dropItems(player, block);
            dropExperience(player, block);
            addDamage(player.getInventory().getItemInMainHand(), getConfig().getInt("crops." + block.getType() + ".damage"));
            isDestroyed(player, player.getInventory().getItemInMainHand());
        }
    }
    private void resetAge(Block block) {
        BlockData blockData = block.getBlockData();
        ((Ageable) blockData).setAge(0);
        block.setBlockData(blockData);
    }
    private void playHarvestSound(Player player, Block block) {
        player.playSound(block.getLocation().add(0.5, 0.3, 0.5), Sound.ITEM_SHOVEL_FLATTEN, 1.0F, 1.0F);
    }
    private void addDamage(ItemStack itemStack, int damage) {
        if (itemStack.containsEnchantment(Enchantment.DURABILITY)) {
            int durability = itemStack.getItemMeta().getEnchantLevel(Enchantment.DURABILITY);
            Damageable toolHealthDamage = (Damageable) itemStack.getItemMeta();
            int result = toolHealthDamage.getDamage() + damage + getConfig().getInt("max-durability");
            toolHealthDamage.setDamage(result - durability);
            itemStack.setItemMeta(toolHealthDamage);
        } else {
            Damageable toolHealthDamage = (Damageable) itemStack.getItemMeta();
            int result = toolHealthDamage.getDamage() + getConfig().getInt("crops." + damage + ".damage") + getConfig().getInt("max-durability");
            toolHealthDamage.setDamage(result);
            itemStack.setItemMeta(toolHealthDamage);
        }
    }
    private void dropItems(Player player, Block block) {
        if (getConfig().getInt("crops." + block.getType() + ".drops.amount.max") == 1) {
            int amount = getConfig().getInt("crops." + block.getType() + ".drops.amount.max");
            ItemStack itemStack = new ItemStack(Material.valueOf(getConfig().getString("crops." + block.getType() + ".drops.item")), amount);
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                int extra = new Random().nextInt(0, player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
                if (new Random().nextInt(100) >= 70) {
                    itemStack.setAmount(itemStack.getAmount() + extra);
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
                } else {
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
                }
            } else {
                player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
            }
        } else {
            int amount = new Random().nextInt(getConfig().getInt("crops." + block.getType() + ".drops.amount.min"), getConfig().getInt("crops." + block.getType() + ".drops.amount.max"));
            ItemStack itemStack = new ItemStack(Material.valueOf(getConfig().getString("crops." + block.getType() + ".drops.item")), amount);
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                int extra = new Random().nextInt(0, player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
                if (new Random().nextInt(100) >= 70) {
                    itemStack.setAmount(itemStack.getAmount() + extra);
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
                } else {
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
                }
            } else {
                player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
            }
        }
    }
    private void dropExperience(Player player, Block block) {
        if (getConfig().getBoolean("crops." + block.getType() + ".experience.enable")) {
            if (new Random().nextInt(100) < getConfig().getInt("crops." + block.getType() + ".experience.chance")) {
                Location location = block.getLocation().add(0.5, 0.3, 0.5);
                ExperienceOrb experience = (ExperienceOrb) player.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
                experience.setExperience(getConfig().getInt("crops." + block.getType() + ".experience.amount"));
                if (getConfig().getBoolean("crops." + block.getType() + ".sound.enable")) {
                    String soundType = getConfig().getString("crops." + block.getType() + ".sound.type");
                    float volume = (float) getConfig().getDouble("crops." + block.getType() + ".sound.volume");
                    float pitch = (float) getConfig().getDouble("crops." + block.getType() + ".sound.pitch");
                    player.playSound(location, Sound.valueOf(soundType), volume, pitch);
                }
                if (getConfig().getBoolean("crops." + block.getType() + ".particle.enable")) {
                    String particleType = getConfig().getString("crops." + block.getType() + ".particle.type");
                    int count = getConfig().getInt("crops." + block.getType() + ".particle.count");
                    double offsetX = getConfig().getDouble("crops." + block.getType() + ".particle.offsetX");
                    double offsetY = getConfig().getDouble("crops." + block.getType() + ".particle.offsetY");
                    double offsetZ = getConfig().getDouble("crops." + block.getType() + ".particle.offsetZ");
                    player.spawnParticle(Particle.valueOf(particleType), location, count, offsetX, offsetY, offsetZ, 0.0);
                }
            }
        }
    }
    private void isDestroyed(Player player, ItemStack itemStack) {
        if (itemStack.getType().equals(Material.WOODEN_HOE)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 59) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (itemStack.getType().equals(Material.STONE_HOE)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 131) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (itemStack.getType().equals(Material.IRON_HOE)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 250) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (itemStack.getType().equals(Material.GOLDEN_HOE)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 32) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (itemStack.getType().equals(Material.DIAMOND_HOE)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 1561) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (itemStack.getType().equals(Material.NETHERITE_HOE)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 2031) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        }
    }
}