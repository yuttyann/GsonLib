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

import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class EntityOption extends TimerOption {

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        return Optional.empty();
    }

    @NotNull
    public final Entity getEntity() {
        if (isEntityRead()) {
            return ((EntityScriptRead) getTempMap()).getEntity();
        }
        return getPlayer();
    }

    @NotNull
    public final Location getScriptLocation() {
        if (isEntityRead()) {
            return ((EntityScriptRead) getTempMap()).getScriptLocation();
        }
        return getLocation();
    }

    public boolean isEntityRead() {
        return getTempMap().getClass().equals(EntityScriptRead.class);
    }
}