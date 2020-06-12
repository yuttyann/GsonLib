package com.github.yuttyann.scriptentityplus.script.option;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptentityplus.Main;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;
import com.github.yuttyann.scriptentityplus.script.EntityOption;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityDelay extends EntityOption implements Runnable {

    private Entity entity;
    private Location location;
    private boolean unSaveExec;

    public EntityDelay() {
        super("entitydelay", "@e_delay:");
    }

    @Override
    @NotNull
    public Option newInstance() {
        return new EntityDelay();
    }

    @Override
    public boolean isFailedIgnore() {
        return true;
    }

    @Override
    protected boolean isValid() {
        if (!isEntityRead(getSBRead())) {
            throw new UnsupportedOperationException();
        }
        String[] array = StringUtils.split(getOptionValue(), "/");
        unSaveExec = array.length > 1 && Boolean.parseBoolean(array[1]);
        String blockCoords = getScriptLocation().getFullCoords();
        if (!unSaveExec && getMapManager().containsDelay(getUniqueId(), getScriptType(), blockCoords)) {
            SBConfig.ACTIVE_DELAY.send(getSBPlayer());
        } else {
            if (!unSaveExec) {
                getMapManager().putDelay(getUniqueId(), getScriptType(), blockCoords);
            }
            this.entity = getEntity();
            this.location = entity.getLocation();
            Bukkit.getScheduler().runTaskLater(getPlugin(), this, Long.parseLong(array[0]));
        }
        return false;
    }

    @Override
    public void run() {
        if (!unSaveExec) {
            getMapManager().removeDelay(getUniqueId(), getScriptType(), getScriptLocation().getFullCoords());
        }
        if (getSBPlayer().isOnline()) {
            if (entity.isDead()) {
                EntityListener.TEMP_ENTITIES.add(entity = Main.getInstance().createArmorStand(location));
            }
            try {
                getSBRead().put(EntityListener.KEY_ENTITY, entity);
                getSBRead().read(getScriptIndex() + 1);
            } finally {
                EntityListener.TEMP_ENTITIES.removeIf(e -> {
                    boolean result = e.getUniqueId().equals(entity.getUniqueId());
                    if (result) {
                        entity.remove();
                    }
                    return result;
                });
            }
        } else {
            EndProcessManager.forEach(e -> e.failed(getSBRead()));
        }
    }
}