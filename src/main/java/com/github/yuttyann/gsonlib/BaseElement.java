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

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * GsonLib BaseElement クラス
 * @author yuttyann44581
 */
public abstract class BaseElement {

    /**
     * 引数同士を比較します。
     * @param argment1 - 引数1
     * @param argment2 - 引数2
     * @return {@link boolean} - 引数が一致するのかどうか
     */
    protected boolean compare(@NotNull Object argment1, @NotNull Object argment2) {
        return Objects.equals(argment1, argment2);
    }

    /**
     * エレメントのクラスを取得します。
     * @return {@link Class}&lt;? extends {@link BaseElement}&gt; - エレメントのクラス
     */
    @NotNull
    public Class<? extends BaseElement> getElementType() {
        return BaseElement.class;
    }
}