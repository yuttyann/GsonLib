package com.github.yuttyann.scriptentityplus.json;

import com.github.yuttyann.scriptentityplus.item.ToolMode;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityScript {

    @SerializedName("scripts")
    @Expose
    private final Set<String> scripts = new LinkedHashSet<>();

    @SerializedName("deathscript")
    @Expose
    private final Set<String> deathscript = new LinkedHashSet<>();

    @SerializedName("invincible")
    @Expose
    private boolean invincible;

    @SerializedName("projectile")
    @Expose
    private boolean projectile;

    @NotNull
    public Set<String> getScripts(@NotNull ToolMode toolMode) {
        return toolMode == ToolMode.NORMAL_SCRIPT ? scripts : deathscript;
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
        return Objects.hash(scripts, deathscript, invincible, projectile);
    }
}