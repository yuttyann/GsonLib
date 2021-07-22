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
import java.util.Objects;
import java.util.function.Consumer;

import com.github.yuttyann.gsonlib.BaseElement;
import com.github.yuttyann.gsonlib.BaseJson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * GsonLib ThreeJson クラス
 * @param <A> 引数1の型
 * @param <B> 引数2の型
 * @param <C> 引数3の型
 * @param <E> エレメントの型({@link ThreeElement}を継承してください。)
 * @author yuttyann44581
 */
public abstract class ThreeJson<A, B, C, E extends ThreeJson.ThreeElement<A, B, C>> extends BaseJson<E> {

    /**
     * GsonLib ThreeElement クラス
     * @param <A> 引数1の型
     * @param <B> 引数2の型
     * @param <C> 引数3の型
     * @author yuttyann44581
     */
    public static abstract class ThreeElement<A, B, C> extends BaseElement {

        /**
         * 引数{@link A}を取得します。
         * <p>
         * コンストラクタ宣言時に渡す引数と同じでなければなりません。
         * @return {@link A} - 引数1
         */
        @Nullable
        protected abstract A getA();

        /**
         * 引数{@link B}を取得します。
         * <p>
         * コンストラクタ宣言時に渡す引数と同じでなければなりません。
         * @return {@link B} - 引数2
         */
        @Nullable
        protected abstract B getB();

        /**
         * 引数{@link C}を取得します。
         * <p>
         * コンストラクタ宣言時に渡す引数と同じでなければなりません。
         * @return {@link C} - 引数3
         */
        @Nullable
        protected abstract C getC();

        /**
         * 引数が一致するのか比較します。
         * @param a - 引数1
         * @param b - 引数2
         * @param c - 引数3
         * @return {@link boolean} - 要素が存在する場合は{@code true}
         */
        public boolean isElement(@Nullable A a, @Nullable B b, @Nullable C c) {
            return compare(getA(), a) && compare(getB(), b) && compare(getC(), c);
        }

        /**
         * ハッシュコードを生成します。
         * @return {@link int} - ハッシュコード
         */
        @Override
        public final int hashCode() {
            return ThreeJson.hash(getA(), getB(), getC());
        }

        @Override
        @NotNull
        public final Class<? extends BaseElement> getElementType() {
            return ThreeElement.class;
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
    protected ThreeJson(@NotNull File file) {
        super(file);
    }

    /**
     * インスタンスを生成します。
     * @param a - 引数1
     * @param b - 引数2
     * @param c - 引数3
     * @return {@link E} - インスタンス
     */
    @NotNull
    protected abstract E newInstance(@Nullable A a, @Nullable B b, @Nullable C c);

    /**
     * 要素を取得します。
     * @param a - 引数1
     * @param b - 引数2
     * @param c - 引数3
     * @return {@link E} - 要素
     */
    @NotNull
    public final E load(@Nullable A a, @Nullable B b, @Nullable C c) {
        int hash = hash(a, b, c);
        var element = getElementMap().get(hash);
        if (element == null) {
            getElementMap().put(hash, element = newInstance(a, b, c));
        } else if (!element.isElement(a, b, c)) {
            var subHash = Integer.valueOf(hash);
            if ((element = subGet(subHash, e -> e.isElement(a, b, c))) == null) {
                subPut(subHash, element = newInstance(a, b, c));
            }
        }
        return element;
    }

    /**
     * 要素を取得します。
     * <p>
     * 要素が存在しなかった場合は、{@code null}を返します。
     * @param a - 引数1
     * @param b - 引数2
     * @param c - 引数3
     * @return {@link E} - 要素
     */
    @Nullable
    public final E fastLoad(@Nullable A a, @Nullable B b, @Nullable C c) {
        int hash = hash(a, b, c);
        var element = getElementMap().get(hash);
        if (element == null) {
            return null;
        } else if (!element.isElement(a, b, c) && isSubNotEmpty()) {
            element = subGet(hash, e -> e.isElement(a, b, c));
        }
        return element;
    }

    /**
     * 要素が存在するのか確認します。
     * @param a - 引数1
     * @param b - 引数2
     * @param c - 引数3
     * @return {@link boolean} - 要素が存在する場合は{@code true}
     */
    public final boolean has(@Nullable A a, @Nullable B b, @Nullable C c) {
        return fastLoad(a, b, c) != null;
    }

    /**
     * 一致する要素を削除します。
     * @param a - 引数1
     * @param b - 引数2
     * @param c - 引数3
     * @return {@link boolean} - 削除に成功した場合は{@code true}
     */
    public final boolean remove(@Nullable A a, @Nullable B b, @Nullable C c) {
        int hash = hash(a, b, c);
        var element = getElementMap().get(hash);
        if (element == null) {
            return false;
        }
        if (element.isElement(a, b, c)) {
            getElementMap().remove(hash);
            if (isSubNotEmpty()) {
                subMapFirstShift(hash);
            }
        } else if (isSubNotEmpty()) {
            return subRemove(hash, e -> e.isElement(a, b, c));
        }
        return true;
    }

    /**
     * 処理を行った後に要素を保存します。
     * @param action - 処理
     * @param a - 引数1
     * @param b - 引数2
     * @param c - 引数3
     */
    public final void action(@NotNull Consumer<E> action, @Nullable A a, @Nullable B b, @Nullable C c) {
        action.accept(load(a, b, c));
        saveJson();
    }

    private static int hash(@Nullable Object a, @Nullable Object b, @Nullable Object c) {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + Objects.hashCode(a);
        hash = prime * hash + Objects.hashCode(b);
        hash = prime * hash + Objects.hashCode(c);
        return hash;
    }
}