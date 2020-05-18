package com.github.yuttyann.scriptentityplus.item;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKey;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.Permission;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import com.github.yuttyann.scriptentityplus.json.ScriptEntity;
import com.github.yuttyann.scriptentityplus.json.ScriptEntityInfo;
import com.github.yuttyann.scriptentityplus.json.tellraw.*;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;
import com.github.yuttyann.scriptentityplus.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptConnection extends ItemAction {

    public ScriptConnection() {
        super(get());
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
        ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
        switch (action) {
            case LEFT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                break;
            case RIGHT_CLICK_AIR:
                if (objectMap.getBoolean(EntityListener.KEY_OFF)) {
                    off();
                } else {
                    main(objectMap);
                }
                break;
            case LEFT_CLICK_BLOCK:
                left(objectMap);
                break;
            default:
        }
        return true;
    }

    private void left(ObjectMap objectMap) {
        if (isSneaking) {
            JsonBuilder builder = new JsonBuilder();
            builder.add(new JsonElement("ScriptTypes: ", ChatColor.WHITE));
            String blockCoords = BlockCoords.getFullCoords(location);
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
            Bukkit.dispatchCommand(player, "tellraw " + player.getName() + " " + builder.toJson());
        } else {
            ArrayList<String> list = new ArrayList<>();
            for (ScriptType scriptType : ScriptType.values()) {
                if (ScriptBlock.getInstance().getMapManager().containsCoords(location, scriptType)) {
                    list.add(scriptType.name() + "|" + BlockCoords.getFullCoords(location));
                }
            }
            if (list.size() > 0) {
                objectMap.put(PlayerListener.KEY_SCRIPT, list.toArray(new String[0]));
                String types = list.stream()
                        .map(s -> ScriptType.valueOf(StringUtils.split(s, "|")[0]).type())
                        .collect(Collectors.joining(", "));
                SEConfig.SCRIPT_SELECT.replace(types).send(player);
            }
        }
    }

    private void main(ObjectMap objectMap) {
        Optional<Entity> entity = getEntity();
        if (!entity.isPresent()) {
            return;
        }
        if (isSneaking) {
            entity.ifPresent(value -> new ScriptEntity(value.getUniqueId()).delete());
            SEConfig.SCRIPT_REMOVE_ENTITY.replace(entity.get().getType().name()).send(player);
        } else {
            if (!objectMap.has(PlayerListener.KEY_SCRIPT)) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
                return;
            }
            ScriptEntity scriptEntity = new ScriptEntity(entity.get().getUniqueId());
            ScriptEntityInfo info = scriptEntity.getInfo();
            for (String script : objectMap.get(PlayerListener.KEY_SCRIPT, new String[0])) {
                String[] array = StringUtils.split(script, "|");
                Location location = BlockCoords.fromString(array[1]);
                ScriptType scriptType = ScriptType.valueOf(array[0]);
                if (ScriptBlock.getInstance().getMapManager().containsCoords(location, scriptType)) {
                    info.getScripts().add(script);
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
            SEConfig.SCRIPT_SETTING_ENTITY.replace(entity.get().getType().name()).send(player);
        }
    }

    private void off() {
        Optional<Entity> entity = getEntity();
        if (!entity.isPresent()) {
            return;
        }
        ScriptEntity scriptEntity = new ScriptEntity(entity.get().getUniqueId());
        ScriptEntityInfo info = scriptEntity.getInfo();
        if (info.getScripts().size() < 1) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(player);
            return;
        }
        if (isSneaking) {
            info.setInvincible(!info.isInvincible());
            try {
                scriptEntity.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ConfigKey<String> configKey = info.isInvincible() ? SEConfig.INVINCIBLE_ENABLED : SEConfig.INVINCIBLE_DISABLED;
            Utils.sendColorMessage(player, configKey.getValue());
        } else {
            player.sendMessage("----- [ Scripts ] -----");
            int index = 0;
            for (String script : info.getScripts()) {
                String[] array = StringUtils.split(script, "|");
                ScriptType scriptType = ScriptType.valueOf(array[0]);
                JsonBuilder builder = new JsonBuilder();
                builder.add(new JsonElement("Index" + (index++) + "=", ChatColor.WHITE));

                JsonElement element = new JsonElement(scriptType.name(), ChatColor.GREEN, ChatFormat.BOLD);
                String command = "/sbp " + scriptType.type() + " run " + StringUtils.replace(array[1], ",", "");
                element.setClickEvent(ClickEventType.SUGGEST_COMMAND, command);
                element.setHoverEvent(HoverEventType.SHOW_TEXT, getTexts(scriptType, BlockCoords.fromString(array[1])));
                builder.add(element);
                Bukkit.dispatchCommand(player, "tellraw " + player.getName() + " " + builder.toJson());
            }
            player.sendMessage("---------------------");
        }
    }

    @NotNull
    private String getTexts(@NotNull ScriptType scriptType, Location location) {
        ScriptData scriptData = new ScriptData(location, scriptType);
        StringJoiner joiner = new StringJoiner("\n§6- §b");
        scriptData.getScripts().forEach(joiner::add);
        String author = "§eAuthor: §a" + String.join(", ", scriptData.getAuthors(true));
        return author + "\n§eCoords: §d" + BlockCoords.getFullCoords(location) + "\n§eScripts:§e\n§6- §b" + joiner.toString();
    }

    public static boolean is(@NotNull ItemStack item) {
        return ItemUtils.isItem(item, Material.BONE, s -> s.equals("§dScript Connection"));
    }

    @NotNull
    public static ItemStack get() {
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName("§dScript Connection");
        List<String> lore = new ArrayList<>(SEConfig.SCRIPT_CONNECTION.getValue());
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, StringUtils.setColor(lore.get(i), true));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}