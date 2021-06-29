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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * GsonLib CacheJson クラス
 * @author yuttyann44581
 */
public final class CacheJson {

    static final Map<Class<? extends BaseJson<?>>, CacheJson> CACHE_MAP = new HashMap<>();

    private final Class<? extends BaseJson<?>> json;
    private final Constructor<?> constructor;

    /**
     * コンストラクタ
     * @param json - Jsonのクラス
     */
    private CacheJson(@NotNull Class<? extends BaseJson<?>> json) {
        this.json = json;

        var constructor = (Constructor<?>) null;
        try {
            constructor = json.getDeclaredConstructor(File.class);
            constructor.setAccessible(true);
        } catch (SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.constructor = Objects.requireNonNull(constructor);
    }

    /**
     * キャッシュを登録します。
     * @param json - Jsonのクラス
     */
    public static void register(@NotNull Class<? extends BaseJson<?>> json) {
        CACHE_MAP.put(json, new CacheJson(json));
    }

    /**
     * Jsonのクラスを取得します。
     * @return {@link Class}&lt;? extends {@link BaseJson}&gt; - Jsonのクラス
     */
    @NotNull
    public Class<? extends BaseJson<?>> getJsonClass() {
        return json;
    }

    /**
     * インスタンスを生成します。
     * @throws IllegalArgumentException インスタンスの生成に失敗した際にスローされます。
     * @param file - ファイル
     * @return {@link BaseJson} - インスタンス
     */
    @NotNull
    BaseJson<?> newInstance(@NotNull File file) {
        try {
            return (BaseJson<?>) constructor.newInstance(file);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException();
    }
}