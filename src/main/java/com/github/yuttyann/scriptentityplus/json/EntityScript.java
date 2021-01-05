package com.github.yuttyann.scriptentityplus.json;

import com.github.yuttyann.scriptentityplus.item.ToolMode;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityScript {

    @SerializedName(value = "normalscripts", alternate = { "scripts" })
    private final Set<String> normalscripts = new LinkedHashSet<>();

    @SerializedName(value = "deathscripts", alternate = { "deathscript" })
    private final Set<String> deathscripts = new LinkedHashSet<>();

    @SerializedName("invincible")
    private boolean invincible;

    @SerializedName("projectile")
    private boolean projectile;

    @NotNull
    public Set<String> getScripts(@NotNull ToolMode toolMode) {
        return toolMode == ToolMode.NORMAL_SCRIPT ? normalscripts : deathscripts;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setProjectile(boolean projectile) {
        this.projectile = projectile;
    }

    public boolean isProjectile() {
        return projectile;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + normalscripts.hashCode();
        hash = prime * hash + deathscripts.hashCode();
        hash = prime * hash + Boolean.hashCode(invincible);
        hash = prime * hash + Boolean.hashCode(projectile);
        return hash;
    }
}