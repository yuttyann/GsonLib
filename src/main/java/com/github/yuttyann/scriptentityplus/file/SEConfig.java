package com.github.yuttyann.scriptentityplus.file;

import com.github.yuttyann.scriptblockplus.file.config.ConfigKey;
import com.github.yuttyann.scriptblockplus.file.config.ReplaceKey;

import java.util.ArrayList;
import java.util.List;

import static com.github.yuttyann.scriptblockplus.file.config.ConfigKeys.*;

public class SEConfig {

    // List Key
    public static final ConfigKey<List<String>> SCRIPT_CONNECTION = stringListKey("ScriptConnection", new ArrayList<>());

    // String Keys
    public static final ConfigKey<String> INVINCIBLE_TEXT = stringKey("InvincibleTextMessage", "");
    public static final ConfigKey<String> PROJECTILE_TEXT = stringKey("ProjectileTextMessage", "");

    // Replace Keys
    public static final ReplaceKey SETTING_VALUE = replaceKey(stringKey("SettingValueMessage", ""), "%name%", "%value%");
    public static final ReplaceKey SETTING_VIEW = replaceKey(stringKey("SettingViewMessage", ""), "%name%", "%value%");
    public static final ReplaceKey SCRIPT_SELECT = replaceKey(stringKey("ScriptSelectMessage", ""), "%scripttype%");
    public static final ReplaceKey SCRIPT_SETTING_ENTITY = replaceKey(stringKey("ScriptSettingEntityMessage", ""), "%toolmode%", "%entitytype%");
    public static final ReplaceKey SCRIPT_REMOVE_ENTITY = replaceKey(stringKey("ScriptRemoveEntityMessage", ""), "%entitytype%");
}