package com.github.yuttyann.scriptentityplus.file;

import com.github.yuttyann.scriptblockplus.event.FileReloadEvent;
import com.github.yuttyann.scriptblockplus.file.Files;
import com.github.yuttyann.scriptblockplus.file.Lang;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKeys;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.yaml.YamlConfig;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptentityplus.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SEFiles implements Listener {

    private static final String PREFIX = "se_";

    public static final String PATH_CONFIG = "config.yml";
    public static final String PATH_LANGS = "langs" + Files.S + "{code}.yml";

    static {
        Bukkit.getPluginManager().registerEvents(new SEFiles(), Main.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFileReload(FileReloadEvent event) {
        reload();
    }

    public static void reload() {
        ConfigKeys.load(loadConfig());
        ConfigKeys.load(loadLang());

        searchKeys();
    }

    public static void searchKeys() {
        YamlConfig config = getConfig();
        if (config.getFile().exists()) {
            Files.sendNotKeyMessages(Main.getInstance(), config, PATH_CONFIG);
        }
        YamlConfig lang = getLang();
        if (lang.getFile().exists()) {
            Files.sendNotKeyMessages(Main.getInstance(), lang, "langs" + Files.S + lang.getFileName());
        }
    }

    @NotNull
    public static YamlConfig getConfig() {
        return Files.getFiles().get(PREFIX + PATH_CONFIG);
    }

    @NotNull
    public static YamlConfig getLang() {
        return Files.getFiles().get(PREFIX + PATH_LANGS);
    }

    @NotNull
    private static YamlConfig loadConfig() {
        return Files.putFile(PREFIX + PATH_CONFIG, YamlConfig.load(Main.getInstance(), PATH_CONFIG, true));
    }

    @NotNull
    private static YamlConfig loadLang() {
        String language = SBConfig.LANGUAGE.getValue();
        if (StringUtils.isEmpty(language) || "default".equalsIgnoreCase(language)) {
            language = Locale.getDefault().getLanguage();
        }
        Lang lang = new Lang(Main.getInstance(), language);
        return Files.putFile(PREFIX + PATH_LANGS, lang.load(PATH_LANGS, "lang"));
    }
}