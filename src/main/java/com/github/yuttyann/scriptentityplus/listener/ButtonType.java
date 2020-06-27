package com.github.yuttyann.scriptentityplus.listener;

import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ButtonType {
    ENABLED("Enabled", true),
    DISABLED("Disabled", false),
    VIEW("View", false);

    private final String type;
    private final boolean isEnabled;

    private ButtonType(@NotNull String type, boolean isEnabled) {
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
        return StreamUtils.fOrElse(values(), s -> s.name().equals(upper), null);
    }
}