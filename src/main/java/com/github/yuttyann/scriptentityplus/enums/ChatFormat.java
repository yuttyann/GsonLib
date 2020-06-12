package com.github.yuttyann.scriptentityplus.enums;

import org.jetbrains.annotations.NotNull;

public enum ChatFormat {
    BOLD("bold"),
    ITALIC("italic"),
    UNDERLINED("underlined"),
    OBFUSCATED("obfuscated"),
    STRIKETHROUGH("strikethrough");

    private final String format;

    private ChatFormat(@NotNull String format) {
        this.format = format;
    }

    @NotNull
    public String getFormat() {
        return format;
    }
}