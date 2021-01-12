/**
 * ScriptEntityPlus - Allow you to add script to any entities.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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