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
package com.github.yuttyann.scriptentityplus.script;

import com.github.yuttyann.scriptblockplus.event.ScriptReadEndEvent;
import com.github.yuttyann.scriptblockplus.event.ScriptReadStartEvent;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.hook.plugin.Placeholder;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableLocation;
import com.github.yuttyann.scriptentityplus.ScriptEntity;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EntityScriptRead extends ScriptRead {

    private final Location scriptLocation;

    public EntityScriptRead(@NotNull Player player, @NotNull Entity entity, @NotNull Location location, @NotNull ScriptKey scriptKey) {
        super(player, entity.getLocation(), scriptKey);
        this.scriptLocation = new UnmodifiableLocation(location);
    }

    @NotNull
    public final Entity getEntity() {
        return (Entity) Objects.requireNonNull(get(EntityListener.KEY_ENTITY));
    }

    @NotNull
    public final Location getScriptLocation() {
        return scriptLocation;
    }

    @Override
    public boolean read(int index) {
        if (!blockScript.has(scriptLocation)) {
            SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
            return false;
        }
        if (!sortScripts(blockScript.get(scriptLocation).getScript())) {
            SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptKey).send(sbPlayer);
            SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptLocation, scriptKey).console();
            return false;
        }
        Bukkit.getPluginManager().callEvent(new ScriptReadStartEvent(ramdomId, this));
        try {
            return perform(index);
        } finally {
            Bukkit.getPluginManager().callEvent(new ScriptReadEndEvent(ramdomId, this));
            StreamUtils.filter(this, ScriptRead::isInitialize, ObjectMap::clear);
        }
    }

    @Override
    protected boolean perform(final int index) {
        for (this.index = index; this.index < scripts.size(); this.index++) {
            if (!sbPlayer.isOnline()) {
                EndProcessManager.forEach(e -> e.failed(this));
                return false;
            }
            String script = scripts.get(this.index);
            Option option = OptionManager.newInstance(script);
            this.value = Placeholder.INSTANCE.replace(getPlayer(), option.getValue(script));
            if (!option.callOption(this) && isFailedIgnore(option)) {
                return false;
            }
        }
        EndProcessManager.forEach(e -> e.success(this));
        new PlayerCountJson(sbPlayer.getUniqueId()).action(PlayerCount::add, scriptLocation, scriptKey);
        SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptLocation, scriptKey).console();
        return true;
    }

    @NotNull
    private String replace(String script) {
        List<String> list = ScriptEntity.getInstance().getConfig().getStringList("Replaces");
        String result = list.stream()
                .map(s -> s.split(">>>"))
                .filter(a -> a.length > 1 && script.contains(a[0]))
                .map(a -> StringUtils.replace(script, a[0], a[1]))
                .collect(Collectors.joining());
        return StringUtils.isEmpty(result) ? script : result;
    }
}