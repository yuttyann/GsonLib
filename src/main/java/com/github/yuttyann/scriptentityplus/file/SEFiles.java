package com.github.yuttyann.scriptentityplus.file;

import com.github.yuttyann.scriptblockplus.event.FileReloadEvent;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKeys;
import com.github.yuttyann.scriptentityplus.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import static com.github.yuttyann.scriptblockplus.file.Files.*;

public class SEFiles implements Listener {

    static {
        Bukkit.getPluginManager().registerEvents(new SEFiles(), Main.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFileReload(FileReloadEvent event) {
        reload();
    }

    public static void reload() {
        Plugin plugin = Main.getInstance();
        ConfigKeys.load(loadFile(plugin, PATH_CONFIG, true));
        ConfigKeys.load(loadLang(plugin, PATH_LANGS));
        searchKeys(plugin, PATH_CONFIG, PATH_LANGS);
    }
}