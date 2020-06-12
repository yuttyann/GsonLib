package com.github.yuttyann.scriptentityplus.enums;

import com.github.yuttyann.scriptblockplus.enums.reflection.ClassType;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptentityplus.json.ScriptEntityInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public enum SettingType {
    INVINCIBLE("Invincible"),
    PROJECTILE("Projectile");

    private final String type;

    private SettingType(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public void set(@NotNull ScriptEntityInfo info, @NotNull Object value) {
        try {
            info.getClass().getMethod("set" + type, ClassType.getPrimitive(value.getClass())).invoke(info, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean is(@NotNull ScriptEntityInfo info) {
        try {
            return (boolean) info.getClass().getMethod("is" + type).invoke(info);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    public static SettingType get(@NotNull String name) {
        String upper = name.toUpperCase();
        return StreamUtils.fOrElse(values(), s -> s.name().equals(upper), null);
    }
}
