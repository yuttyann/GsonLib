package com.github.yuttyann.scriptentityplus.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public enum ButtonType {
    ENABLED("Enabled", true),
    DISABLED("Disabled", false),
    VIEW("View", false);

    private final String type;
    private final boolean isEnabled;

    ButtonType(@NotNull String type, boolean isEnabled) {
        this.type = type;
        this.isEnabled = isEnabled;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @NotNull
    public static ButtonType get(boolean isEnabled) {
        return isEnabled ? ENABLED : DISABLED;
    }

    @Nullable
    public static ButtonType get(@NotNull String name) {
        String upper = name.toUpperCase();
        return Stream.of(values()).filter(s -> s.name().equals(upper)).findFirst().orElse(null);
    }
}