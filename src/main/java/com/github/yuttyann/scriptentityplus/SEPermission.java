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
package com.github.yuttyann.scriptentityplus;

import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public enum SEPermission {
    TOOL_SCRIPT_CONNECTION("scriptentityplus.tool.scriptconnection");

    private final String node;

    SEPermission(@NotNull String node) {
        this.node = node;
    }

    @NotNull
    public String getNode() {
        return node;
    }

    public boolean has(@NotNull Permissible permissible) {
        return permissible.hasPermission(node);
    }
}