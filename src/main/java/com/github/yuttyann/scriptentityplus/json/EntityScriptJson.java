package com.github.yuttyann.scriptentityplus.json;

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonOptions;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@JsonOptions(path = "json/entityscript", file = "{id}.json")
public class EntityScriptJson extends Json<EntityScript> {

    public EntityScriptJson(@NotNull UUID uuid) {
        super(uuid);
    }

    public void delete() {
        file.delete();
    }

    @Override
    @NotNull
    protected EntityScript newInstance(@NotNull Object[] object) {
        return new EntityScript();
    }
}