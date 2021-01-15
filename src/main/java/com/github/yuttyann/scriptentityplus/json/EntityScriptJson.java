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

import com.github.yuttyann.scriptblockplus.file.Json;
import com.github.yuttyann.scriptblockplus.file.json.annotation.JsonTag;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@JsonTag(path = "json/entityscript")
public class EntityScriptJson extends Json<EntityScript> {

    public EntityScriptJson(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    @NotNull
    protected EntityScript newInstance(@NotNull Object[] object) {
        return new EntityScript();
    }
}