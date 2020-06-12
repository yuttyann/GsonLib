package com.github.yuttyann.scriptentityplus.item;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.Main;
import com.github.yuttyann.scriptentityplus.Permission;
import com.github.yuttyann.scriptentityplus.enums.ChatFormat;
import com.github.yuttyann.scriptentityplus.enums.ClickEventType;
import com.github.yuttyann.scriptentityplus.enums.HoverEventType;
import com.github.yuttyann.scriptentityplus.enums.ToolMode;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import com.github.yuttyann.scriptentityplus.json.ScriptEntity;
import com.github.yuttyann.scriptentityplus.json.ScriptEntityInfo;
import com.github.yuttyann.scriptentityplus.json.tellraw.*;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;
import com.github.yuttyann.scriptentityplus.listener.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.StringJoiner;

public class ScriptConnection extends ItemAction {

    public ScriptConnection(@NotNull ToolMode toolMode) {
        super(toolMode.getItem());
    }

    @NotNull
    public Optional<Entity> getEntity() {
        ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
        return Optional.ofNullable(objectMap.get(EntityListener.KEY_CLICK_ENTITY));
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return Permission.TOOL_SCRIPT_CONNECTION.has(permissible);
    }

    @Override
    public boolean run() {
        switch (action) {
            case RIGHT_CLICK_BLOCK:
                break;
            case RIGHT_CLICK_AIR:
                if (SBPlayer.fromPlayer(player).getObjectMap().getBoolean(EntityListener.KEY_OFF)) {
                    off();
                } else {
                    main();
                }
                break;
            case LEFT_CLICK_BLOCK:
                left();
                break;
            case LEFT_CLICK_AIR:
                player.getInventory().setItemInMainHand(ToolMode.getNextItem(item));
                Utils.updateInventory(player);
                break;
            default:
        }
        return true;
    }

    private void left() {
        if (isSneaking) {
            String blockCoords = BlockCoords.getFullCoords(location);
            JsonBuilder builder = new JsonBuilder();
            builder.add(new JsonElement("ScriptTypes: ", ChatColor.WHITE));
            for (ScriptType scriptType : ScriptType.values()) {
                if (ScriptBlock.getInstance().getMapManager().containsCoords(location, scriptType)) {
                    String chat = scriptType.name() + "|" + blockCoords + "/" + PlayerListener.KEY_TOOL;
                    JsonElement element = new JsonElement(scriptType.name(), ChatColor.GREEN, ChatFormat.BOLD);
                    element.setClickEvent(ClickEventType.RUN_COMMAND, chat);
                    element.setHoverEvent(HoverEventType.SHOW_TEXT, getTexts(scriptType, location));
                    builder.add(element);
                } else {
                    builder.add(new JsonElement(scriptType.name(), ChatColor.RED));
                }
                if (scriptType.ordinal() != (ScriptType.size() - 1)) {
                    builder.add(new JsonElement(", ", ChatColor.WHITE));
                }
            }
            Main.dispatchCommand("tellraw " + player.getName() + " " + builder.toJson());
        } else {
            player.getInventory().setItemInMainHand(ToolMode.getNextItem(item));
            Utils.updateInventory(player);
        }
    }

    private void main() {
        Optional<Entity> entity = getEntity();
        if (!entity.isPresent()) {
            return;
        }
        if (isSneaking) {
            ScriptEntity scriptEntity = new ScriptEntity(entity.get().getUniqueId());
            if (!scriptEntity.has()) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
                return;
            }
            scriptEntity.delete();
            SEConfig.SCRIPT_REMOVE_ENTITY.replace(entity.get().getType().name()).send(player);
        } else {
            ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
            if (!objectMap.has(PlayerListener.KEY_SCRIPT)) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
                return;
            }
            ScriptEntity scriptEntity = new ScriptEntity(entity.get().getUniqueId());
            ScriptEntityInfo info = scriptEntity.getInfo();
            ToolMode toolMode = ToolMode.getType(item);
            for (String script : objectMap.get(PlayerListener.KEY_SCRIPT, new String[0])) {
                String[] array = StringUtils.split(script, "|");
                Location location = BlockCoords.fromString(array[1]);
                ScriptType scriptType = ScriptType.valueOf(array[0]);
                if (ScriptBlock.getInstance().getMapManager().containsCoords(location, scriptType)) {
                    info.getScripts(toolMode).add(script);
                }
            }
            info.setInvincible(true);
            try {
                scriptEntity.save();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                objectMap.remove(PlayerListener.KEY_SCRIPT);
            }
            SEConfig.SCRIPT_SETTING_ENTITY.replace(toolMode.getMode(), entity.get().getType().name()).send(player);
        }
    }

    private void off() {
        Optional<Entity> entity = getEntity();
        if (!entity.isPresent()) {
            return;
        }
        ScriptEntity scriptEntity = new ScriptEntity(entity.get().getUniqueId());
        ScriptEntityInfo info = scriptEntity.getInfo();
        if (isSneaking) {
            if (!scriptEntity.has()) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
                return;
            }
            String uuid = entity.get().getUniqueId().toString();
            JsonBuilder builder = new JsonBuilder();
            JsonElement element = new JsonElement("Invincible", ChatColor.AQUA, ChatFormat.BOLD);
            element.setHoverEvent(HoverEventType.SHOW_TEXT, StringUtils.setColor(SEConfig.INVINCIBLE_TEXT.getValue(), true));
            builder.add(element);
            setButton(builder, "Invincible", uuid);
            builder.add(new JsonElement("\n", ChatColor.WHITE));

            element = new JsonElement("Projectile", ChatColor.AQUA, ChatFormat.BOLD);
            element.setHoverEvent(HoverEventType.SHOW_TEXT, StringUtils.setColor(SEConfig.PROJECTILE_TEXT.toString(), true));
            builder.add(element);
            setButton(builder, "Projectile", uuid);

            player.sendMessage("--------- [ Entity Settings ] ---------");
            Main.dispatchCommand("tellraw " + player.getName() + " " + builder.toJson());
            player.sendMessage("-------------------------------------");
        } else {
            ToolMode toolMode = ToolMode.getType(item);
            if (info.getScripts(toolMode).size() < 1) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
                return;
            }
            player.sendMessage("----- [ Scripts ] -----");
            int index = 0;
            for (String script : info.getScripts(toolMode)) {
                String[] array = StringUtils.split(script, "|");
                ScriptType scriptType = ScriptType.valueOf(array[0]);
                JsonBuilder builder = new JsonBuilder();
                builder.add(new JsonElement("Index" + (index++) + "=", ChatColor.WHITE));

                JsonElement element = new JsonElement(scriptType.name(), ChatColor.GREEN, ChatFormat.BOLD);
                String command = "/sbp " + scriptType.type() + " run " + StringUtils.replace(array[1], ",", "");
                element.setClickEvent(ClickEventType.SUGGEST_COMMAND, command);
                element.setHoverEvent(HoverEventType.SHOW_TEXT, getTexts(scriptType, BlockCoords.fromString(array[1])));
                builder.add(element);

                Main.dispatchCommand("tellraw " + player.getName() + " " + builder.toJson());
            }
            player.sendMessage("---------------------");
        }
    }

    private void setButton(@NotNull JsonBuilder builder, @NotNull String name, @NotNull String uuid) {
        JsonElement element = new JsonElement(" [", ChatColor.GOLD);
        builder.add(element);
        element = new JsonElement("Enabled", ChatColor.GREEN);
        element.setClickEvent(ClickEventType.RUN_COMMAND, name + "=" + uuid + "=Enabled/" + PlayerListener.KEY_SETTINGS);
        builder.add(element);
        element = new JsonElement("]", ChatColor.GOLD);
        builder.add(element);

        element = new JsonElement("  [", ChatColor.GOLD);
        builder.add(element);
        element = new JsonElement("Disabled", ChatColor.RED);
        element.setClickEvent(ClickEventType.RUN_COMMAND, name + "=" + uuid + "=Disabled/" + PlayerListener.KEY_SETTINGS);
        builder.add(element);
        element = new JsonElement("]", ChatColor.GOLD);
        builder.add(element);

        element = new JsonElement("  [", ChatColor.GOLD);
        builder.add(element);
        element = new JsonElement("View", ChatColor.LIGHT_PURPLE);
        element.setClickEvent(ClickEventType.RUN_COMMAND, name + "=" + uuid + "=View/" + PlayerListener.KEY_SETTINGS);
        builder.add(element);
        element = new JsonElement("]", ChatColor.GOLD);
        builder.add(element);
    }

    @NotNull
    private String getTexts(@NotNull ScriptType scriptType, Location location) {
        ScriptData scriptData = new ScriptData(location, scriptType);
        StringBuilder builder = new StringBuilder();
        StringJoiner joiner = new StringJoiner("\n§6- §b");
        scriptData.getScripts().forEach(joiner::add);
        builder.append("§eAuthor: §a").append(String.join(", ", scriptData.getAuthors(true)));
        builder.append("\n§eCoords: §d").append(BlockCoords.getFullCoords(location));
        builder.append("\n§eScripts:§e\n§6- §b").append(joiner.toString());
        return builder.toString();
    }
}