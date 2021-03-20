/**
 * GsonLib - It provides a simple library using Gson.
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
package com.github.yuttyann.gsonlib;

import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

/**
 * GsonLib GsonHolder クラス
 * @author yuttyann44581
 */
public final class GsonHolder {

    private final GsonBuilder gsonBuilder;

    private Gson gson;

    /**
     * コンストラクタ
     * @param gsonBuilder - {@link GsonBuilder}
     */
    public GsonHolder(@NotNull GsonBuilder gsonBuilder) {
        this.gsonBuilder = gsonBuilder;
    }

    /**
     * {@link Gson}を取得します。
     * @return {@link Gson}
     */
    @NotNull
    public Gson getGson() {
        if (gson == null) {
            update();
        }
        return gson;
    }

    /**
     * 設定を更新します。
     */
    public synchronized void update() {
        this.gson = gsonBuilder.create();
    }

    /**
     * 処理の後に設定を更新します。
     * @param action - 処理
     */
    public void builder(@NotNull Consumer<GsonBuilder> action) {
        try {
            action.accept(gsonBuilder);
        } finally {
            update();
        }
    }
}