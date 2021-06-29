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

import com.github.yuttyann.gsonlib.adapter.FieldExclusion;
import com.github.yuttyann.gsonlib.adapter.NumberAdapter;
import com.github.yuttyann.gsonlib.annotation.JsonTag;
import com.google.common.base.Charsets;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import java.io.*;
import java.util.*;

/**
 * GsonLib BaseJson クラス
 * @param <E> エレメントの型
 * @author yuttyann44581
 */
@SuppressWarnings("unchecked")
public abstract class BaseJson<E extends BaseElement> extends SubElementMap<E> {

    /**
     * GsonLib Status 列挙型
     * <p>
     * キャッシュの状態を表す列挙型
     * @author yuttyann44581
     */
    public enum Status {

        /**
         * キャッシュが生成されていない状態
         * <p>
         * デフォルトの状態です。
        */
        NO_CACHE,

        /**
         * キャッシュを保持している状態
         * <p>
         * 生成方法は{@link BaseJson#newJson(Class, File)}を参照してください。
        */
        KEEP_CACHE,

        /**
         * キャッシュが削除されている状態
         * <p>
         * この状態のインスタンスを保持し続けないでください。
        */
        CLEAR_CACHE,
    }

    public static final GsonHolder GSON_HOLDER = new GsonHolder(new GsonBuilder());

    private static final IntObjectMap<BaseJson<?>> JSON_CACHE = new IntObjectHashMap<>();

    private final File file;
    private final String name;
    private final JsonTag jsonTag;

    private int id;
    private File parent;
    private Status status;
    private CollectionType collectionType;

    private IntObjectMap<E> elementMap;

    // GsonBuilderにアダプター等の追加を行う。
    static {
        GSON_HOLDER.builder(b -> {
            b.setExclusionStrategies(new FieldExclusion());
            b.registerTypeAdapter(Map.class, new NumberAdapter());
            b.registerTypeAdapter(List.class, new NumberAdapter());
        });
    }

    /**
     * コンストラクタ
     * @param file - ファイル
     */
    protected BaseJson(@NotNull File file) {
        if ((this.jsonTag = getClass().getAnnotation(JsonTag.class)) == null) {
            throw new NullPointerException("Annotation not found @JsonTag()");
        }
        var path = file.getPath();
        if (!path.endsWith(".json")) {
            throw new IllegalArgumentException("File: " + path);
        }
        this.file = file;
        this.name = path.substring(path.lastIndexOf(File.separatorChar) + 1, path.lastIndexOf('.'));
        try {
            setMap(loadJson());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        noCache();
    }

    /**
     * Jsonを取得します。
     * <p>
     * このメソッドは、キャッシュの保存を行います。
     * <p>
     * キャッシュが見つからない場合は、インスタンスの生成を行います。
     * <p>
     * また、キャッシュを利用する場合は基本的に"private"なコンストラクタの実装を推奨します。
     * @param <T> - Jsonの型
     * @param json - Jsonのクラス
     * @param file - ファイル
     * @return {@link BaseJson} - インスタンス
     */
    @NotNull
    public static <T extends BaseJson<?>> T newJson(@NotNull Class<T> json, @NotNull File file) {
        var hash = hash(json, file.hashCode());
        var baseJson = JSON_CACHE.get(hash);
        if (baseJson == null) {
            var cacheJson = CacheJson.CACHE_MAP.get(json);
            baseJson = cacheJson.newInstance(file);
            if (baseJson.jsonTag.cachefileexists() && !baseJson.exists()) {
                return (T) baseJson;
            }
            baseJson.keepCache();
            baseJson.setCacheId(hash);
            JSON_CACHE.put(hash, baseJson);
        }
        return (T) baseJson;
    }

    /**
     * キャッシュを取得します。
     * <p>
     * キャッシュが見つからない場合は、生成したインスタンスを返します。
     * <p>
     * また、キャッシュを利用する場合は基本的に"private"なコンストラクタの実装を推奨します。
     * @param <T> Jsonの型
     * @param json - Jsonのクラス
     * @param file - ファイル
     * @return {@link BaseJson} - インスタンス
     */
    @NotNull
    public static <T extends BaseJson<?>> T getCache(@NotNull Class<T> json, @NotNull File file) {
        var baseJson = JSON_CACHE.get(hash(json, file.hashCode()));
        if (baseJson == null) {
            baseJson = CacheJson.CACHE_MAP.get(json).newInstance(file);
        }
        return (T) baseJson;
    }

    /**
     * キャッシュされた全ての要素を削除します。
     */
    public static void clear() {
        JSON_CACHE.entries().forEach(e -> e.value().clearCache());
        JSON_CACHE.clear();
    }

    /**
     * キャッシュされた全ての要素を削除します。
     * @param json - Jsonのクラス 
     */
    public static final void clear(@NotNull Class<? extends BaseJson<?>> json) {
        var iterator = JSON_CACHE.entries().iterator();
        while (iterator.hasNext()) {
            var value = iterator.next().value();
            if (value.getClass().equals(json)) {
                value.clearCache();
                iterator.remove();
            }
        }
    }

    /**
     * {@link IntObjectMap}&lt;{@link E}&gt;を取得します。
     * @return {@link IntObjectMap}&lt;{@link E}&gt; - エレメントのマップ
     */
    @Override
    @NotNull
    protected final IntObjectMap<E> getElementMap() {
        return elementMap;
    }

    /**
     * {@link IntObjectMap}&lt;{@link E}&gt;を生成します。
     * @return {@link IntObjectMap}&lt;{@link E}&gt; - マップ
     */
    @NotNull
    protected IntObjectMap<E> createMap() {
        return new IntObjectHashMap<>();
    }

    /**
     * 要素の再読み込みを行います。
     */
    public final void reload() {
        try {
            setMap(loadJson());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拡張子を除いたファイルの名前を取得します。
     * @return {@link String} - ファイルの名前
     */
    @NotNull
    public final String getName() {
        return name;
    }

    /**
     * ファイルを取得します。
     * @return {@link File} - ファイル
     */
    @NotNull
    public final File getFile() {
        return file;
    }

    /**
     * 親ディレクトリを取得します。
     * @return {@link File} - 親ディレクトリ
     */
    @NotNull
    public final File getParentFile() {
        return parent == null ? parent = file.getParentFile() : parent;
    }

    /**
     * キャッシュIDを設定します。
     * @param id - キャッシュID
     */
    private void setCacheId(final int id) {
        if (this.id == 0) {
            this.id = id;
        }
    }

    /**
     * キャッシュIDを取得します。
     * @return {@link int} - キャッシュID
     */
    public int getCacheId() {
        return id;
    }

    /**
     * キャッシュが生成されていない状態に設定します。
     * <p>
     * 値が設定されていない場合のみ設定される。
     */
    private void noCache() {
        if (getStatus() == null) {
            this.status = Status.NO_CACHE;
        }
    }

    /**
     * キャッシュを保持している状態に設定します。
     * <p>
     * キャッシュが生成されていない場合のみ設定される。
     */
    private void keepCache() {
        if (getStatus() == Status.NO_CACHE) {
            this.status = Status.KEEP_CACHE;
        }
    }

    /**
     * キャッシュが削除されている状態に設定します。
     * <p>
     * キャッシュが保存されている場合のみ設定される。
     */
    private void clearCache() {
        if (getStatus() == Status.KEEP_CACHE) {
            this.status = Status.CLEAR_CACHE;
        }
    }

    /**
     * キャッシュの状態を取得します。
     * @return {@link Status} - キャッシュの状態
     */
    public final Status getStatus() {
        return status;
    }

    /**
     * マップに要素が存在しない場合に{@code true}を返します。
     * @return {@link boolean} - 要素が存在しない場合は{@code true}
     */
    public final boolean isEmpty() {
        return elementMap.isEmpty();
    }

    /**
     * ファイルが存在するのか確認します。
     * @return {@link boolean} - ファイルが存在する場合は{@code true}
     */
    public final boolean exists() {
        return file.exists();
    }

    /**
     * キャッシュされた要素を含め、ファイルを削除します。
     * @return {@link boolean} - 削除に成功した場合は{@code true}
     */
    public final boolean deleteFile() {
        if (!file.exists()) {
            return false;
        }
        try {
            if (getStatus() == Status.KEEP_CACHE) {
                clearCache();
                JSON_CACHE.remove(getCacheId());
            }
        } finally {
            file.delete();
        }
        return true;
    }

    /**
     * Jsonのシリアライズ化を行います。
     */
    public final void saveJson() {
        var parent = getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        var elements = copyElements();
        try (var writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)))) {
            if (elementMap.size() < jsonTag.limit()) {
                writer.setIndent(jsonTag.indent());
            }
            GSON_HOLDER.getGson().toJson(elements, getCollectionType(), writer);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (jsonTag.temporary() && getStatus() == Status.KEEP_CACHE) {
                clearCache();
                JSON_CACHE.remove(getCacheId());
            }
        }
    }

    /**
     * エレメントのコレクションをコピーします。
     * @return {@link Collection}&lt;{@link E}&gt; - エレメントのコレクション
     */
    @NotNull
    private Collection<E> copyElements() {
        int size = elementMap.size() + subSize();
        if (size == 0) {
            return Collections.emptyList();
        }
        if (isSubEmpty()) {
            return elementMap.values();
        }
        var newList = new ArrayList<E>(size);
        for (var element : elementMap.values()) {
            newList.add(element);
        }
        for (var subElement : subValues()) {
            newList.add(subElement);
        }
        return newList;
    }

    /**
     * Jsonのデシリアライズ化を行います。
     * @return {@link Set}&lt;{@link E}&gt; - エレメントの配列
     * @throws ClassNotFoundException
     */
    @Nullable
    private List<E> loadJson() throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return null;
        }
        var parent = getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (var reader = new JsonReader(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)))) {
            return (List<E>) GSON_HOLDER.getGson().fromJson(reader, getCollectionType());
        }
    }

    /**
     * コレクションタイプを取得します。
     * @return {@link CollectionType} - コレクションタイプ 
     */
    @NotNull
    private CollectionType getCollectionType() throws ClassNotFoundException {
        return collectionType == null ? this.collectionType = new CollectionType(Collection.class, this) : collectionType;
    }

    /**
     * マップに要素を追加します。
     * @param elements - エレメントの配列
     */
    private void setMap(@Nullable List<E> elements) {
        var newMap = createMap();
        if (elements != null && elements.size() > 0) {
            int size = elements.size();
            if (size == 1) {
                var element = elements.get(0);
                newMap.put(element.hashCode(), element);
            } else {
                for (int i = 0; i < size; i++) {
                    var element = elements.get(i);
                    int hashCode = element.hashCode();
                    if (newMap.containsKey(hashCode)) {
                        subPut(hashCode, element);
                    } else {
                        newMap.put(hashCode, element);
                    }
                }
            }
        }
        this.elementMap = newMap;
    }

    /**
     * ハッシュコードを生成します。
     * @param hashCode - ハッシュコード
     * @param json - Jsonのクラス
     * @return {@link int} - ハッシュコード
     */
    private static int hash(@NotNull Class<?> json, @NotNull int hashCode) {
        int hash = 1;
        int prime = 31;
        hash = prime * hash + hashCode;
        hash = prime * hash + json.hashCode();
        return hash;
    }

    @Override
    public int hashCode() {
        return hash(getClass(), name.hashCode());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BaseJson)) {
            return false;
        }
        var baseJson = (BaseJson<?>) obj;
        return Objects.equals(name, baseJson.name) && Objects.equals(file, baseJson.file);
    }

    @Override
    @NotNull
    public String toString() {
        try {
            return GSON_HOLDER.getGson().toJson(copyElements(), getCollectionType());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "[]";
    }
}