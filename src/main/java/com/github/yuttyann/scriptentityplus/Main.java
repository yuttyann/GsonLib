package com.github.yuttyann.scriptentityplus;

import com.github.yuttyann.scriptblockplus.PluginInstance;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.ScriptBlockAPI;
import com.github.yuttyann.scriptblockplus.Updater;
import com.github.yuttyann.scriptblockplus.enums.OptionPriority;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.file.SEFiles;
import com.github.yuttyann.scriptentityplus.item.ScriptConnection;
import com.github.yuttyann.scriptentityplus.enums.ToolMode;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;
import com.github.yuttyann.scriptentityplus.listener.PlayerListener;
import com.github.yuttyann.scriptentityplus.script.option.EntityCooldown;
import com.github.yuttyann.scriptentityplus.script.option.EntityDelay;
import com.github.yuttyann.scriptentityplus.script.option.EntityOldCooldown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Main extends JavaPlugin {

    private Updater updater;

    static {
        StreamUtils.forEach(ToolMode.values(), t -> new ScriptConnection(t).put());
    }

    @Override
    public void onEnable() {
        PluginManager manager = getServer().getPluginManager();
        if (!manager.isPluginEnabled("ScriptBlockPlus")) {
            manager.disablePlugin(this);
        } else {
            if (Utils.isUpperVersion(ScriptBlock.getInstance().getDescription().getVersion(), "1.9.3")) {
                new PluginInstance(Main.class, this).put();

                SEFiles.reload();
                manager.registerEvents(new PlayerListener(), this);
                manager.registerEvents(new EntityListener(), this);

                ScriptBlockAPI api = ScriptBlock.getInstance().getAPI();
                api.registerOption(OptionPriority.HIGHEST, EntityOldCooldown.class);
                api.registerOption(OptionPriority.HIGHEST, EntityCooldown.class);
                api.registerOption(OptionPriority.VERY_HIGH, EntityDelay.class);

                checkUpdate(Bukkit.getConsoleSender(), false);
            } else {
                manager.disablePlugin(this);
            }
        }
    }

    @Override
    public void onDisable() {
        EntityListener.TEMP_ENTITIES.forEach(Entity::remove);
        EntityListener.TEMP_ENTITIES.clear();
    }

    public void checkUpdate(@NotNull CommandSender sender, boolean latestMessage) {
        if (updater == null) {
            updater = new Updater(this);
        }
        Thread thread = new Thread(() -> {
            try {
                updater.init();
                updater.load();
                if (!updater.execute(sender) && latestMessage) {
                    SBConfig.NOT_LATEST_PLUGIN.send(sender);
                }
            } catch (Exception e) {
                e.printStackTrace();
                SBConfig.ERROR_UPDATE.send(sender);
            }
        });
        try {
            thread.setName("Update Thread : " + Utils.getPluginName(this));
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public ArmorStand createArmorStand(@NotNull Location location) {
        World world = Objects.requireNonNull(location.getWorld());
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setSilent(true);
        armorStand.setCustomName("DeathScriptDummy");
        armorStand.setCustomNameVisible(false);
        return armorStand;
    }

    @NotNull
    public static Main getInstance() {
        return PluginInstance.get(Main.class);
    }

    public static void dispatchCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}