package com.github.yuttyann.scriptentityplus.listener;

import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.Main;
import com.github.yuttyann.scriptentityplus.Permission;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import com.github.yuttyann.scriptentityplus.item.ScriptConnection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.UUID;

import static com.github.yuttyann.scriptblockplus.utils.StringUtils.removeStart;
import static com.github.yuttyann.scriptblockplus.utils.StringUtils.split;

public class PlayerListener implements Listener {

    static {
        new ScriptConnection().put();
    }

    public static final String KEY_TOOL = UUID.nameUUIDFromBytes("KEY_TOOL".getBytes()).toString();
    public static final String KEY_SCRIPT = Utils.randomUUID();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().startsWith("/") ? event.getMessage().substring(1) : event.getMessage();
        if (com.github.yuttyann.scriptblockplus.enums.Permission.COMMAND_TOOL.has(player)
                && (command.equals("sbp tool") || command.equals("scriptblockplus tool"))) {
            player.getInventory().addItem(ScriptConnection.get());
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
        if (chat.contains("/" + KEY_TOOL)) {
            if (Permission.TOOL_SCRIPT_CONNECTION.has(player)) {
                String[] array = { split(removeStart(chat, "say "), "/")[0] };
                String type = ScriptType.valueOf(StringUtils.split(array[0], "|")[0]).type();
                SBPlayer.fromPlayer(player).getObjectMap().put(KEY_SCRIPT, array);
                SEConfig.SCRIPT_SELECT.replace(type).send(player);
            }
            event.setCancelled(true);
        }
    }
}