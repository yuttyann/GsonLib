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
package com.github.yuttyann.scriptentityplus.file;

import com.github.yuttyann.scriptblockplus.file.config.ConfigKey;
import com.github.yuttyann.scriptblockplus.file.config.ReplaceKey;

import java.util.ArrayList;
import java.util.List;

import static com.github.yuttyann.scriptblockplus.file.config.ConfigKeys.*;

public class SEConfig {

    // List Key
    public static final ConfigKey<List<String>> REPLACES = stringListKey("Replaces", new ArrayList<>());
    public static final ConfigKey<List<String>> SCRIPT_CONNECTION = stringListKey("ScriptConnection", new ArrayList<>());

    // String Keys
    public static final ConfigKey<String> INVINCIBLE_TEXT = stringKey("InvincibleTextMessage", "");
    public static final ConfigKey<String> PROJECTILE_TEXT = stringKey("ProjectileTextMessage", "");

    // Replace Keys
    public static final ReplaceKey SETTING_VALUE = replaceKey("SettingValueMessage", "", "%name%", "%value%");
    public static final ReplaceKey SETTING_VIEW = replaceKey("SettingViewMessage", "", "%name%", "%value%");
    public static final ReplaceKey SCRIPT_SELECT = replaceKey("ScriptSelectMessage", "", "%scriptkey%");
    public static final ReplaceKey SCRIPT_SETTING_ENTITY = replaceKey("ScriptSettingEntityMessage", "", "%toolmode%", "%entitytype%");
    public static final ReplaceKey SCRIPT_REMOVE_ENTITY = replaceKey("ScriptRemoveEntityMessage", "", "%entitytype%");
}