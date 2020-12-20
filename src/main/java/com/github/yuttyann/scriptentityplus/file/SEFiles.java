package com.github.yuttyann.scriptentityplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.FileReloadEvent;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKeys;
import com.github.yuttyann.scriptentityplus.ScriptEntity;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;

import static com.github.yuttyann.scriptblockplus.file.SBFiles.*;

public class SEFiles implements Listener {

    static {
        Bukkit.getPluginManager().registerEvents(new SEFiles(), ScriptEntity.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFileReload(FileReloadEvent event) {
        reload();
    }

    public static void reload() {
        Plugin plugin = ScriptEntity.getInstance();
        ConfigKeys.load(loadFile(plugin, PATH_CONFIG, true));
        ConfigKeys.load(loadLang(plugin, PATH_LANGS));
        searchKeys(plugin, PATH_CONFIG, PATH_LANGS);

        // ファイル"json/scriptentity"を"json/entityscript"にリネーム
        File dataFolder = ScriptBlock.getInstance().getDataFolder();
        File scriptEntity = new File(dataFolder, "json" + S + "scriptentity");
        if (scriptEntity.exists() && scriptEntity.isDirectory()) {
            scriptEntity.renameTo(new File(dataFolder, "json" + S + "entityscript"));
        }
    }
}