package com.github.yuttyann.scriptentityplus.script.option;

import com.github.yuttyann.scriptblockplus.file.json.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import com.github.yuttyann.scriptentityplus.script.EntityOption;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class EntityCooldown extends EntityOption {

    public EntityCooldown() {
        super("entitycooldown", "@e_cooldown:");
    }

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

        PlayerTemp temp = new PlayerTemp(getFileUniqueId());
        temp.getInfo().getTimerTemp().add(new TimerTemp(params, getFileUniqueId(), getScriptLocation().getFullCoords(), getScriptType()));
        temp.save();
        return true;
    }

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        Set<TimerTemp> set = new PlayerTemp(getFileUniqueId()).getInfo().getTimerTemp();
        return get(set, Objects.hash(false, getFileUniqueId(), getScriptLocation().getFullCoords(), getScriptType()));
    }
}