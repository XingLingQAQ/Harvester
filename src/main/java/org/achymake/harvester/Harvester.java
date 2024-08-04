package org.achymake.harvester;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.achymake.harvester.commands.HarvesterCommand;
import org.achymake.harvester.listeners.Harvest;
import org.achymake.harvester.listeners.PlayerInteract;
import org.achymake.harvester.listeners.PlayerJoin;
import org.achymake.harvester.net.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class Harvester extends JavaPlugin {
    private static Harvester instance;
    private static UpdateChecker updateChecker;
    private static StateFlag FLAG_HARVEST;
    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flagHarvest = new StateFlag("harvester-harvest", false);
            registry.register(flagHarvest);
            FLAG_HARVEST = flagHarvest;
        } catch (FlagConflictException ignored) {
            Flag<?> existingHarvest = registry.get("harvester-harvest");
            if (existingHarvest instanceof StateFlag) {
                FLAG_HARVEST = (StateFlag) existingHarvest;
            }
        } catch (Exception e) {
            sendLog(Level.WARNING, e.getMessage());
        }
    }
    @Override
    public void onEnable() {
        instance = this;
        updateChecker = new UpdateChecker(this);
        commands();
        events();
        reload();
        sendLog(Level.INFO, "Enabled " + getDescription().getName() + " " + getDescription().getVersion());
        getUpdateChecker().getUpdate();
    }
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        sendLog(Level.INFO, "Disabled " + getDescription().getName() + " " + getDescription().getVersion());
    }
    private void commands() {
        getCommand("harvester").setExecutor(new HarvesterCommand(this));
    }
    private void events() {
        getManager().registerEvents(new Harvest(this), this);
        getManager().registerEvents(new PlayerInteract(this), this);
        getManager().registerEvents(new PlayerJoin(this), this);
    }
    public void reload() {
        File file = new File(getDataFolder(), "config.yml");
        if (file.exists()) {
            try {
                getConfig().load(file);
            } catch (IOException | InvalidConfigurationException e) {
                sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            getConfig().options().copyDefaults(true);
            try {
                getConfig().save(file);
            } catch (IOException e) {
                sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
    public StateFlag getFlagHarvest() {
        return FLAG_HARVEST;
    }
    public PluginManager getManager() {
        return getServer().getPluginManager();
    }
    public BukkitScheduler getScheduler() {
        return getServer().getScheduler();
    }
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
    public static Harvester getInstance() {
        return instance;
    }
    public String name() {
        return getDescription().getName();
    }
    public String version() {
        return getDescription().getVersion();
    }
    public boolean isAllowHarvest(Block block) {
        try {
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
            if (regionManager != null) {
                ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion("_", BlockVector3.at(block.getX(), block.getY(), block.getZ()), BlockVector3.at(block.getX(), block.getY(), block.getZ()));
                for (ProtectedRegion regionIn : regionManager.getApplicableRegions(protectedCuboidRegion)) {
                    StateFlag.State flag = regionIn.getFlag(getFlagHarvest());
                    if (flag == StateFlag.State.ALLOW) {
                        return true;
                    } else if (flag == StateFlag.State.DENY) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            sendLog(Level.WARNING, e.getMessage());
            return false;
        }
    }
    public boolean isHoe(ItemStack itemStack) {
        return isWoodenHoe(itemStack) || isStoneHoe(itemStack) || isIronHoe(itemStack) || isGoldenHoe(itemStack) || isDiamondHoe(itemStack) || isNetheriteHoe(itemStack);
    }
    public boolean isWoodenHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOODEN_HOE);
    }
    public boolean isStoneHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.STONE_HOE);
    }
    public boolean isIronHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.IRON_HOE);
    }
    public boolean isGoldenHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.GOLDEN_HOE);
    }
    public boolean isDiamondHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.DIAMOND_HOE);
    }
    public boolean isNetheriteHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.NETHERITE_HOE);
    }
    public boolean isEnable(Block block) {
        return getConfig().getBoolean("crops." + block.getType() + ".enable");
    }
    public boolean isRightAge(Block block) {
        return ((Ageable) block.getBlockData()).getAge() == getConfig().getInt("crops." + block.getType() + ".max-age");
    }
    public void send(Player player, String message) {
        player.sendMessage(addColor(message));
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(addColor(message)));
    }
    public String addColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public void send(ConsoleCommandSender sender, String message) {
        sender.sendMessage(message);
    }
    public void sendLog(Level level, String message) {
        getLogger().log(level, message);
    }
}