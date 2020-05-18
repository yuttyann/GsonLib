package com.github.yuttyann.scriptentityplus;

import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

public enum Permission {
    TOOL_SCRIPT_CONNECTION("scriptentityplus.tool.scriptconnection");

    private final String node;

    private Permission(@NotNull String node) {
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