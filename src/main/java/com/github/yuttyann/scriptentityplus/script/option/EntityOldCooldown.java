package com.github.yuttyann.scriptentityplus.script.option;

import com.github.yuttyann.scriptblockplus.file.json.PlayerTemp;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.time.OldCooldown;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import com.github.yuttyann.scriptentityplus.script.EntityOption;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EntityOldCooldown extends EntityOption {

    private static final UUID UUID_OLDCOOLDOWN;

    static  {
        UUID temp = null;
        try {
            Field field = OldCooldown.class.getDeclaredField("UUID_OLDCOOLDOWN");
            field.setAccessible(true);
            temp = (UUID) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        UUID_OLDCOOLDOWN = temp;
    }

    public EntityOldCooldown() {
        super("entityoldcooldown", "@e_oldcooldown:");
    }

    @Override
    @NotNull
    public Option newInstance() {
        return new EntityOldCooldown();
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
        temp.getInfo().getTimerTemp().add(new TimerTemp(params, getScriptLocation().getFullCoords(), getScriptType()));
        temp.save();
        return true;
    }

    @Override
    @NotNull
    protected UUID getFileUniqueId() {
        return UUID_OLDCOOLDOWN;
    }

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        Set<TimerTemp> set = new PlayerTemp(getFileUniqueId()).getInfo().getTimerTemp();
        return get(set, Objects.hash(true, getScriptLocation().getFullCoords(), getScriptType()));
    }
}