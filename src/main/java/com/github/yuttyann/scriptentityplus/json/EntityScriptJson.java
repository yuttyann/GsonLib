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
package com.github.yuttyann.scriptentityplus.json;

import com.github.yuttyann.scriptblockplus.file.json.CacheJson;
import com.github.yuttyann.scriptblockplus.file.json.SingleJson;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@JsonTag(path = "json/entityscript")
public class EntityScriptJson extends SingleJson<EntityScript> {

    private static final CacheJson<UUID> CACHE_JSON = new CacheJson<>(EntityScriptJson.class, EntityScriptJson::new);

    protected EntityScriptJson(@NotNull UUID uuid) {
        super(uuid.toString());
    }

    @Override
    @NotNull
    protected EntityScript newInstance() {
        return new EntityScript();
    }

    @NotNull
    public static EntityScriptJson get(@NotNull UUID uuid) {
        return newJson(uuid, CACHE_JSON);
    }
}