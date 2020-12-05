package com.github.yuttyann.scriptentityplus.script.option;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptentityplus.Main;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;
import com.github.yuttyann.scriptentityplus.script.EntityOption;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class EntityDelay extends EntityOption implements Runnable {

    private static final Delay DELAY = new Delay();

    private Entity entity;
    private Location location;
    private boolean saveDelay;

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
        saveDelay = array.length > 1 && Boolean.parseBoolean(array[1]);
        String blockCoords = getScriptLocation().getFullCoords();
        if (saveDelay && containsDelay(getUniqueId(), getScriptType(), blockCoords)) {
            SBConfig.ACTIVE_DELAY.send(getSBPlayer());
        } else {
            if (saveDelay) {
                putDelay(getUniqueId(), getScriptType(), blockCoords);
            }
            this.entity = getEntity();
            this.location = entity.getLocation();
            Bukkit.getScheduler().runTaskLater(getPlugin(), this, Long.parseLong(array[0]));
        }
        return false;
    }

    @Override
    public void run() {
        if (saveDelay) {
            removeDelay(getUniqueId(), getScriptType(), getScriptLocation().getFullCoords());
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

    private void putDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
        try {
            Method method = Delay.class.getDeclaredMethod("putDelay", UUID.class, ScriptType.class, String.class);
            method.setAccessible(true);
            method.invoke(DELAY, uuid, scriptType, fullCoords);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void removeDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
        try {
            Method method = Delay.class.getDeclaredMethod("removeDelay", UUID.class, ScriptType.class, String.class);
            method.setAccessible(true);
            method.invoke(DELAY, uuid, scriptType, fullCoords);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private boolean containsDelay(@NotNull UUID uuid, @NotNull ScriptType scriptType, @NotNull String fullCoords) {
        try {
            Method method = Delay.class.getDeclaredMethod("containsDelay", UUID.class, ScriptType.class, String.class);
            method.setAccessible(true);
            return (boolean) method.invoke(DELAY, uuid, scriptType, fullCoords);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
}