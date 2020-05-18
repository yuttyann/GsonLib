package com.github.yuttyann.scriptentityplus.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ScriptEntityInfo {

    @SerializedName("uuid")
    @Expose
    private final UUID uuid;

    @SerializedName("scripts")
    @Expose
    private final Set<String> scripts = new LinkedHashSet<>();

    @SerializedName("invincible")
    @Expose
    private boolean invincible;

    public ScriptEntityInfo(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    @NotNull
    public Set<String> getScripts() {
        return scripts;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isInvincible() {
        return invincible;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}