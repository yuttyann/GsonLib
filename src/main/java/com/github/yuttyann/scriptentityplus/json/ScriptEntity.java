package com.github.yuttyann.scriptentityplus.json;

import com.github.yuttyann.scriptblockplus.file.json.Json;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class ScriptEntity extends Json<ScriptEntityInfo> {

    public ScriptEntity(@NotNull UUID uuid) {
        super(uuid);
    }

    @NotNull
    public ScriptEntityInfo getInfo() {
        int hash = uuid.hashCode();
        ScriptEntityInfo info = StreamUtils.fOrElse(list, p -> p.hashCode() == hash, null);
        if (info == null) {
            list.add(info = new ScriptEntityInfo(uuid));
        }
        return info;
    }

    public boolean has() {
        return getFile(uuid).exists();
    }

    public void delete() {
        File file = getFile(uuid);
        if (file.exists()) {
            file.delete();
        }
    }
}