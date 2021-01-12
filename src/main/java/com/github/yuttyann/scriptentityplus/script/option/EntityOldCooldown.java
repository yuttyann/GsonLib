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
package com.github.yuttyann.scriptentityplus.script.option;

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import com.github.yuttyann.scriptentityplus.script.EntityOption;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@OptionTag(name = "entity_oldcooldown", syntax = "@e_oldcooldown:")
public class EntityOldCooldown extends EntityOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new EntityOldCooldown();
    }

    @SuppressWarnings("")
    @Override
    protected boolean isValid() throws Exception {
        if (inCooldown()) {
            return false;
        }
        long value = Integer.parseInt(getOptionValue()) * 1000L;
        long[] params = new long[] { System.currentTimeMillis(), value, 0L };
        params[2] = params[0] + params[1];

        Json<PlayerTemp> json = new PlayerTempJson(getFileUniqueId());
        TimerTemp timerTemp = new TimerTemp(getScriptLocation(), getScriptKey());
        Method timerTempSet = TimerTemp.class.getDeclaredMethod("set", long[].class);
        timerTempSet.setAccessible(true);
        timerTempSet.invoke(timerTemp, new Object[] { params });
        json.load().getTimerTemp().add(timerTemp);
        json.saveFile();
        return true;
    }

    @Override
    @NotNull
    protected UUID getFileUniqueId() {
        return OldCooldown.UUID_OLDCOOLDOWN;
    }

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        Set<TimerTemp> timers = new PlayerTempJson(getFileUniqueId()).load().getTimerTemp();
        return get(timers, new TimerTemp(getScriptLocation(), getScriptKey()));
    }
}