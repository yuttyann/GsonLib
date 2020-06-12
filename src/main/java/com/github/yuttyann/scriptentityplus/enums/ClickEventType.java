package com.github.yuttyann.scriptentityplus.enums;

import org.jetbrains.annotations.NotNull;

public enum ClickEventType {
    OPEN_URL("open_url"),
    RUN_COMMAND("run_command"),
    SUGGEST_COMMAND("suggest_command");

    private final String type;

    private ClickEventType(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public String getType() {
        return type;
    }
}