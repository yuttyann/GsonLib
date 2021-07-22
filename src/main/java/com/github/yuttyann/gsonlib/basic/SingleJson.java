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
package com.github.yuttyann.gsonlib.basic;

import java.io.File;
import java.util.function.Consumer;

import com.github.yuttyann.gsonlib.BaseElement;
import com.github.yuttyann.gsonlib.BaseJson;
import com.github.yuttyann.gsonlib.collect.IntSingleMap;

import org.jetbrains.annotations.NotNull;

import io.netty.util.collection.IntObjectMap;

/**
 * GsonLib SingleJson クラス
 * @param <E> エレメントの型({@link SingleElement}を継承してください。)
 * @author yuttyann44581
 */
public abstract class SingleJson<E extends SingleJson.SingleElement> extends BaseJson<E> {

    /**
     * GsonLib SingleElement クラス
     * @author yuttyann44581
     */
    public static abstract class SingleElement extends BaseElement {

        @Override
        @NotNull
        public final Class<? extends BaseElement> getElementType() {
            return SingleElement.class;
        }

        @Override
        public final int hashCode() {
            return 0;
        }
    }

    /**
     * コンストラクタ
     * @apiNote
     * <pre>
     * 実装例です。
     * // キャッシュ(CacheJson)を利用する場合は、
     * // コンストラクタの引数を『 File file 』のみにしてください。
     * // また、上記の方法で実装する場合は、修飾子を『 private 』にすることを推奨します。
     * private xxxJson(&#064;NotNull String name) {
     *     super(name);
     * }
     * </pre>
     * @param file - ファイル
     */
    protected SingleJson(@NotNull File file) {
        super(file);
    }

    /**
     * {@link IntObjectMap}&lt;{@link E}&gt;を生成します。
     * @return {@link IntObjectMap}&lt;{@link E}&gt; - マップ
     */
    @Override
    @NotNull
    protected final IntObjectMap<E> createMap() {
        return new IntSingleMap<>();
    }

    /**
     * インスタンスを生成します。
     * @return {@link E} - インスタンス
     */
    @NotNull
    protected abstract E newInstance();

    /**
     * 要素を取得します。
     * @return {@link E} - 要素
     */
    @NotNull
    public final E load() {
        var elementMap = getElementMap();
        if (elementMap.isEmpty()) {
            elementMap.put(0, newInstance());
        }
        return elementMap.get(0);
    }

    /**
     * 要素が存在するのか確認します。
     * @return {@link boolean} - 要素が存在する場合は{@code true}
     */
    public final boolean has() {
        return !getElementMap().isEmpty();
    }

    /**
     * 要素を削除します。
     */
    public final void remove() {
        getElementMap().clear();
    }

    /**
     * 処理を行った後に要素を保存します。
     * @param action - 処理
     */
    public final void action(@NotNull Consumer<E> action) {
        action.accept(load());
        saveJson();
    }
}