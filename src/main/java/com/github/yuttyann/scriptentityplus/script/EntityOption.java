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