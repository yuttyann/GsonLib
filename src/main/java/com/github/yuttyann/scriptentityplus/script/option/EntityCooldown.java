package com.github.yuttyann.scriptentityplus.script.option;

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.json.PlayerTempJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import com.github.yuttyann.scriptentityplus.script.EntityOption;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

@OptionTag(name = "entity_cooldown", syntax = "@e_cooldown:")
public class EntityCooldown extends EntityOption {

    @Override
    @NotNull
    public Option newInstance() {
        return new EntityCooldown();
    }

    @Override
    protected boolean isValid() throws Exception {
        if (inCooldown()) {
            return false;
        }
        long value = Integer.parseInt(getOptionValue()) * 1000L;
        long[] params = new long[] { System.currentTimeMillis(), value, 0L };
        params[2] = params[0] + params[1];

        Json<PlayerTemp> json = new PlayerTempJson(getFileUniqueId());
        TimerTemp timerTemp = new TimerTemp(getFileUniqueId(), getScriptLocation(), getScriptType());
        Method timerTempSet = TimerTemp.class.getDeclaredMethod("set", long[].class);
        timerTempSet.setAccessible(true);
        timerTempSet.invoke(timerTemp, new Object[] { params });
        json.load().getTimerTemp().add(timerTemp);
        json.saveFile();
        return true;
    }

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        Set<TimerTemp> timers = new PlayerTempJson(getFileUniqueId()).load().getTimerTemp();
        return get(timers, new TimerTemp(getFileUniqueId(), getScriptLocation(), getScriptType()));
    }
}