package com.github.yuttyann.scriptentityplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.script.SBRead;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerOption;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class EntityOption extends TimerOption {

    public EntityOption(@NotNull String name, @NotNull String syntax) {
        super(name, syntax);
    }

    @Override
    @NotNull
    protected Optional<TimerTemp> getTimerTemp() {
        return Optional.empty();
    }

    @NotNull
    public final Entity getEntity() {
        if (isEntityRead(getSBRead())) {
            return ((ScriptRead) getSBRead()).getEntity();
        }
        return getPlayer();
    }

    @NotNull
    public final BlockCoords getScriptLocation() {
        if (isEntityRead(getSBRead())) {
            return ((ScriptRead) getSBRead()).getScriptLocation();
        }
        return new BlockCoords(getLocation());
    }

    public boolean isEntityRead(@NotNull SBRead sbRead) {
        return sbRead.getClass().equals(ScriptRead.class);
    }
}