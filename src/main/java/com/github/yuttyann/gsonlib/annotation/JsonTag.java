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
package com.github.yuttyann.gsonlib.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * GsonLib JsonTag 注釈
 * @author yuttyann44581
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface JsonTag {

    /**
     * インデントを取得します。
     * <p>
     * 整形を行う際に利用されます。
     * @return {@link String} - インデント
     */
    @NotNull
    String indent() default "  ";

    /**
     * 整形を許可する要素数の上限を取得します。
     * @return {@link int} - 上限値
     */
    @NotNull
    int limit() default 100000;

    /**
     * ファイルを保存した時にキャッシュを削除するのかどうか。
     * @return {@link boolean} - 削除を行う場合は{@code true}
     */
    boolean temporary() default false;

    /**
     * ファイルが存在する時のみキャッシュを保存するのかどうか。
     * @return {@link boolean} - ファイルが存在する時のみキャッシュを保存する場合は{@code true}
     */
    boolean cachefileexists() default true;
}