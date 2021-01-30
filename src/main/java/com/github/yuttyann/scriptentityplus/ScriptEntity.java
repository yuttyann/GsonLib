/**
 * ScriptEntityPlus - Allow you to add script to any entities.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptentityplus;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.Updater;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.script.option.OptionIndex;
import com.github.yuttyann.scriptblockplus.script.option.time.Cooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.file.SEFiles;
import com.github.yuttyann.scriptentityplus.item.ScriptConnection;
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

public class ScriptEntity extends JavaPlugin {

    public static final String SBP_VERSION = "2.0.7";

    private Updater updater;

    @Override
    public void onEnable() {
        PluginManager manager = getServer().getPluginManager();
        if (!manager.isPluginEnabled("ScriptBlockPlus")) {
            manager.disablePlugin(this);
        } else {
            if (Utils.isUpperVersion(ScriptBlock.getInstance().getDescription().getVersion(), SBP_VERSION)) {
                
                // 全ファイルの読み込み
                SEFiles.reload();

                // リスナーの登録
                manager.registerEvents(new PlayerListener(), this);
                manager.registerEvents(new EntityListener(), this);

                // アイテムアクションの登録
                ItemAction.register(new ScriptConnection());

                // オプションの登録（複数登録するため、APIではなくOptionManagerから直接登録する）
                OptionManager.register(OptionIndex.before(OldCooldown.class), EntityOldCooldown::new);
                OptionManager.register(OptionIndex.before(Cooldown.class), EntityCooldown::new);
                OptionManager.register(OptionIndex.before(Delay.class), EntityDelay::new);
                OptionManager.update();

                // アップデート処理
                checkUpdate(Bukkit.getConsoleSender(), false);
            } else {
                getLogger().info("Versions below " + SBP_VERSION + " are not supported.");
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
                if (!updater.run(sender) && latestMessage) {
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
    public static ScriptEntity getInstance() {
        return getPlugin(ScriptEntity.class);
    }

    public static void dispatchCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}