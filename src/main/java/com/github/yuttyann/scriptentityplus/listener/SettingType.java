package com.github.yuttyann.scriptentityplus.listener;

import com.github.yuttyann.scriptblockplus.enums.reflection.ClassType;
import com.github.yuttyann.scriptentityplus.json.EntityScript;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public enum SettingType {
    INVINCIBLE("Invincible"),
    PROJECTILE("Projectile");

    private final String type;

    SettingType(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public void set(@NotNull EntityScript info, @NotNull Object value) {
        try {
            info.getClass().getMethod("set" + type, ClassType.getPrimitive(value.getClass())).invoke(info, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean is(@NotNull EntityScript info) {
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
        return Stream.of(values()).filter(s -> s.name().equals(upper)).findFirst().orElse(null);
    }
}
