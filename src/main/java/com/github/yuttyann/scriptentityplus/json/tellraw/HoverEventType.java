package com.github.yuttyann.scriptentityplus.json.tellraw;

import org.jetbrains.annotations.NotNull;

public enum HoverEventType {
    SHOW_TEXT("show_text"),
    SHOW_ITEM("show_item"),
    SHOW_ACHIEVEMENT("show_achievement");

    private final String type;

    private HoverEventType(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public String getType() {
        return type;
    }
}