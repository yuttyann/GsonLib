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

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import com.github.yuttyann.scriptentityplus.script.EntityOption;
import org.bukkit.Bukkit;

@OptionTag(name = "entity_delay", syntax = "@e_delay:")
public class EntityDelay extends EntityOption implements Runnable {

    private boolean saveDelay;

    @Override
    public boolean isFailedIgnore() {
        return true;
    }

    @Override
    protected boolean isValid() throws Exception {
        String[] array = getOptionValue().split("/");
        saveDelay = array.length <= 1 || Boolean.parseBoolean(array[1]);
        if (saveDelay && Delay.DELAY_SET.contains(newTimerTemp())) {
            SBConfig.ACTIVE_DELAY.send(getSBPlayer());
        } else {
            if (saveDelay) {
                Delay.DELAY_SET.add(newTimerTemp());
            }
            ((ScriptRead) getTempMap()).setInitialize(false);
            Bukkit.getScheduler().runTaskLater(ScriptBlock.getInstance(), this, Long.parseLong(array[0]));
        }
        return false;
    }

    @Override
    public void run() {
        if (saveDelay) {
            Delay.DELAY_SET.remove(newTimerTemp());
        }
        ScriptRead scriptRead = (ScriptRead) getTempMap();
        if (getSBPlayer().isOnline()) {
            scriptRead.setInitialize(true);
            scriptRead.read(getScriptIndex() + 1);
        } else {
            EndProcessManager.forEachFinally(e -> e.failed(scriptRead), () -> scriptRead.clear());
        }
    }

    private TimerTemp newTimerTemp() {
        return new TimerTemp(getUniqueId(), getScriptLocation(), getScriptKey());
    }
}