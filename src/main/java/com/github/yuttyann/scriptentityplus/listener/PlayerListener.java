package com.github.yuttyann.scriptentityplus.listener;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.Main;
import com.github.yuttyann.scriptentityplus.Permission;
import com.github.yuttyann.scriptentityplus.enums.ButtonType;
import com.github.yuttyann.scriptentityplus.enums.SettingType;
import com.github.yuttyann.scriptentityplus.enums.ToolMode;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import com.github.yuttyann.scriptentityplus.json.ScriptEntity;
import com.github.yuttyann.scriptentityplus.json.ScriptEntityInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

import static com.github.yuttyann.scriptblockplus.utils.StringUtils.split;

public class PlayerListener implements Listener {

    public static final String KEY_TOOL = UUID.nameUUIDFromBytes("KEY_TOOL".getBytes()).toString();
    public static final String KEY_SETTINGS = UUID.nameUUIDFromBytes("KEY_SETTINGS".getBytes()).toString();
    public static final String KEY_SCRIPT = Utils.randomUUID();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().startsWith("/") ? event.getMessage().substring(1) : event.getMessage();
        if (com.github.yuttyann.scriptblockplus.enums.Permission.COMMAND_TOOL.has(player)
                && (command.equals("sbp tool") || command.equals("scriptblockplus tool"))) {
            player.getInventory().addItem(ToolMode.NORMAL_SCRIPT.getItem());
        } else if (com.github.yuttyann.scriptblockplus.enums.Permission.COMMAND_CHECKVER.has(player)
                && (command.equals("sbp checkver") || command.equals("scriptblockplus checkver"))) {
            Main.getInstance().checkUpdate(player, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        String command = event.getCommand().startsWith("/") ? event.getCommand().substring(1) : event.getCommand();
        if (com.github.yuttyann.scriptblockplus.enums.Permission.COMMAND_CHECKVER.has(sender)
                && (command.equals("sbp checkver") || command.equals("scriptblockplus checkver"))) {
            Main.getInstance().checkUpdate(sender, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chat = event.getMessage();
        if (chat.lastIndexOf("/" + KEY_TOOL) != -1 && Permission.TOOL_SCRIPT_CONNECTION.has(player)) {
            String[] array = { split(chat, "/")[0] };
            String type = ScriptType.valueOf(StringUtils.split(array[0], "|")[0]).type();
            SBPlayer.fromPlayer(player).getObjectMap().put(KEY_SCRIPT, array);
            SEConfig.SCRIPT_SELECT.replace(type).send(player);
            event.setCancelled(true);
        } else if (chat.lastIndexOf("/" + KEY_SETTINGS) != -1 && Permission.TOOL_SCRIPT_CONNECTION.has(player)) {
            String[] array = split(split(chat, "/")[0], "=");
            ScriptEntity scriptEntity = new ScriptEntity(UUID.fromString(array[1]));
            if (scriptEntity.has()) {
                try {
                    setting(player, array, scriptEntity.getInfo());
                    scriptEntity.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            event.setCancelled(true);
        }
    }

    private void setting(@NotNull Player player, @NotNull String[] array, @NotNull ScriptEntityInfo info) {
        SettingType settingType = SettingType.get(array[0]);
        ButtonType buttonType = ButtonType.get(array[2]);
        if (settingType != null && buttonType != null) {
            switch (buttonType) {
                case ENABLED:
                case DISABLED:
                    settingType.set(info, buttonType.isEnabled());
                    SEConfig.SETTING_VALUE.replace(array[0], buttonType.getType()).send(player);
                    break;
                case VIEW:
                    String type = ButtonType.get(settingType.is(info)).getType();
                    SEConfig.SETTING_VIEW.replace(array[0], type).send(player);
                    break;
            }
        }
    }
}