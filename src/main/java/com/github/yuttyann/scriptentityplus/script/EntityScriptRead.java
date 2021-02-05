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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.hook.plugin.Placeholder;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableLocation;
import com.github.yuttyann.scriptentityplus.file.SEFiles;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class EntityScriptRead extends ScriptRead {

    /**
     * エンティティの座標を返すオプションの一覧
     */
    private static final Set<Class<? extends BaseOption>> FILTERS = new HashSet<>();

    static {
        SEFiles.reloadFilters();
    }

    private Option option;
    private Entity entity;
    private Location entityLocation;

    public EntityScriptRead(@NotNull Player player, @NotNull BlockCoords blockCoords, @NotNull ScriptKey scriptKey) {
        super(player, blockCoords, scriptKey);
    }

    @NotNull
    public static final Set<Class<? extends BaseOption>> getFilters() {
        return FILTERS;
    }

    public final void setEntity(@NotNull Entity entity) {
        this.entity = entity;
        this.entityLocation = new UnmodifiableLocation(entity.getLocation());
    }

    @NotNull
    public final Entity getEntity() {
        return entity;
    }

    @Override
    @NotNull
    public final Location getLocation() {
        if (FILTERS.contains(option.getClass())) {
            return entityLocation;
        }
        return super.getLocation();
    }

    @Override
    protected boolean perform(final int index) {
        for (this.index = index; this.index < scripts.size(); this.index++) {
            if (!sbPlayer.isOnline() || entity.isDead()) {
                EndProcessManager.forEach(e -> e.failed(this));
                return false;
            }
            String script = scripts.get(this.index);
            this.option = OptionManager.newInstance(script);
            this.value = Placeholder.INSTANCE.replace(getPlayer(), option.getValue(script));
            if (!option.callOption(this) && isFailedIgnore(option)) {
                return false;
            }
        }
        EndProcessManager.forEach(e -> e.success(this));
        PlayerCountJson.get(sbPlayer).action(PlayerCount::add, scriptKey, blockCoords);
        SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(scriptKey, blockCoords).console();
        return true;
    }
}