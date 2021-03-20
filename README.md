# GsonLib [Java11](https://adoptopenjdk.net/?variant=openjdk11) [![](https://jitpack.io/v/yuttyann/GsonLib.svg)](https://jitpack.io/#yuttyann/GsonLib)

# 概要
[ScriptBlockPlus](https://github.com/yuttyann/ScriptBlockPlus)で利用している、自作ライブラリの単品化を行いました。  
`(これに伴い、BukkitAPIへの依存を無くしました。)`  
基礎となるJsonクラス、Elementクラスを実装することで簡単にJsonを実装することが可能です。  
**(内部的に[Gson](https://github.com/google/gson)を利用しているため、仕様等に関しては各自調べてください。)**

## 参考
**[基礎クラス]** [BaseJson.java](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/BaseJson.java), [BaseElement.java](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/BaseElement.java)  
**[派生クラス]** [Basics](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/)  

## プロジェクトへの追加 [![](https://jitpack.io/v/yuttyann/GsonLib.svg)](https://jitpack.io/#yuttyann/GsonLib)
### Maven
```xml
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>

  <dependency>
    <groupId>com.github.yuttyann</groupId>
    <artifactId>GsonLib</artifactId>
    <version>1.0.0</version>
  </dependency>
```
### Gradle
```gradle
　allprojects {
　　　repositories {
　　　　　...
　　　　　maven { url 'https://jitpack.io' }
　　　}
　}

　dependencies {
　　　implementation 'com.github.yuttyann:GsonLib:1.0.0'
　}
```

## 使い方
基本的には、上記の**基礎、派生クラス**等を参照してください。  
今回は[SingleJson](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/basic/SingleJson.java)の仕様を元に、基本的な機能について解説をしていきます。

**要素を取得する**
```java
// Jsonの取得
File jsonFile = new File(...);
ExampleJson json = new ExampleJson(jsonFile);

// 要素の取得(見つからなかった場合は、インスタンスを生成して返す)
Example element = json.load();
```
**要素が存在するのかどうか**
```java
// Jsonの取得
File jsonFile = new File(...);
ExampleJson json = new ExampleJson(jsonFile);

// 要素が存在するのか判定
json.has();
```
**要素を削除する**
```java
// Jsonの取得
File jsonFile = new File(...);
ExampleJson json = new ExampleJson(jsonFile);

// 要素の削除
json.remove();
```
**要素を保存する**
```java
// Jsonの取得
File jsonFile = new File(...);
ExampleJson json = new ExampleJson(jsonFile);

// 要素の保存
json.saveJson();
```

## Gsonの操作
```java
// GsonHolderの取得
GsonHolder gsonHolder = BaseJson.GSON_HOLDER;

// GsonBuilderの操作(ラムダ式)
gsonHolder.builder(gsonBuilder -> ...);

// Gsonの取得
Gson gson = gsonHolder.getGson();

// 実装例(アダプターの追加)
gsonHolder.builder(gsonBuilder -> gsonBuilder.registerTypeAdapter(クラス, アダプター));
```

## JsonTagの解説
[JsonTag](https://github.com/yuttyann/GsonLib/blob/main/src/main/java/com/github/yuttyann/gsonlib/annotation/JsonTag.java)は、アノテーションと呼ばれる所謂`注釈`です。  
整形を行う際のスペースの数や、整形を許可する要素の上限数を指定することができます。  
また、**Json**を作成する場合は必ずクラスにアノテーション(`@JsonTag`)を付与しなければいけません。  

**実装方法**
```java
// 整形を許可する要素の上限数
@JsonTag(limit = 10000)

// 整形時のインデント
@JsonTag(indent = "   ")

// ファイルを保存した時にキャッシュを削除するのかどうか
@JsonTag(temporary = true)

// ファイルが存在する時のみキャッシュを保存するのかどうか
@JsonTag(cachefileexists = false)


@JsonTag(...)
public class ExampleJson extends SingleJson<Example> {
    ........
}
```

## クラスの作成
基本的には、要素の内容が保存されます。  
また、Jsonの管理は〇〇〇Jsonを継承したクラスで行います。  

**実装例(ScriptBlockPlus)**  
Json &lt;[BlockScriptJson.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/derived/BlockScriptJson.java)&gt;, Element &lt;[BlockScript.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/element/BlockScript.java)&gt;  
Json &lt;[PlayerTimerJson.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/derived/PlayerTimerJson.java)&gt;, Element &lt;[PlayerTimer.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/element/PlayerTimer.java)&gt;  
Json &lt;[PlayerCountJson.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/derived/PlayerCountJson.java)&gt;, Element &lt;[PlayerCount.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/element/PlayerCount.java)&gt;  

**引数無し、要素が一つのみ**  
クラスの詳細 &lt;[SingleJson.java](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/SingleJson.java)&gt;  
クラスの詳細 &lt;[SingleElement.java](https://github.com/yuttyann/GsonLib/blob/main/src/main/java/com/github/yuttyann/gsonlib/basic/SingleJson.java#L40)&gt;
```java
@JsonTag // Jsonクラスには必ずJsonTagを実装する
public class ExampleJson extends SingleJson<Example> // SingleJsonには保存する要素を指定する {

    // キャッシュを行う場合は必ず作成する
    public static final CacheJson CACHE_JSON = new CacheJson(ExampleJson.class, ExampleJson::new);

    // 安全性を向上するためにキャッシュを行う場合は"private"なコンストラクタへ変更する   
    /**
     * コンストラクタ
     * @param file - ファイル
     */
    private ExampleJson(File file) {
        super(file);
    }

    // 要素のインスタンスを生成
    /**
     * インスタンスを生成します。
     * @return Example - インスタンス
     */
    @Override
    protected Example newInstance() {
        return new Example();
    }

    // キャッシュを生成する(基本的にはこのメソッドで取得を行う)
    /**
     * Jsonを取得します。
     * @param file - ファイル
     */
    public static ExampleJson get(File file) {
        /**
         * Jsonを取得します。
         * このメソッドは、キャッシュの保存を行います。
         * キャッシュが見つからない場合は、インスタンスの生成を行います。
         * また、キャッシュを利用する場合は基本的に"private"なコンストラクタの実装を推奨します。
         * @param file - ファイル
         * @param cacheJson - キャッシュ
         * @return BaseJson - インスタンス
         */
        return newJson(file, CACHE_JSON);
    }
}

// 要素のクラスを作成(Jsonに保存される要素)
public class Example extends SingleElement {
    // シングル(引数無し、要素が一つだけ)

    @SerializedName("singleFlag")
    private boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
```
**引数有り、要素が複数**  
クラスの詳細 &lt;[TwoJson.java](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/TwoJson.java)&gt;  
クラスの詳細 &lt;[TwoElement.java](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/TwoJson.java#L42)&gt;   
引数の数を変更したい場合は、[OneJson](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/OneJson.java)(引数一つ)や[ThreeJson](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/ThreeJson.java)(引数三つ)を利用してください。  
また、引数の数を四つ以上にしたい場合は、クラス構造を複製して単純に引数の数を増やしてください。
```java
@JsonTag // Jsonクラスには必ずJsonTagを実装する
public class ExampleJson extends TwoJson<Test1, Test2, Example> // 引数有りのJsonは最初に引数、一番最後に要素を指定する <..., Example> {

    // キャッシュを行う場合は必ず作成する
    public static final CacheJson CACHE_JSON = new CacheJson(ExampleJson.class, ExampleJson::new);

    // 安全性を向上するためにキャッシュを行う場合は"private"なコンストラクタへ変更する   
    /**
     * コンストラクタ
     * @param file - ファイル
     */
    private ExampleJson(File file) {
        super(file);
    }

    // 要素のインスタンスを生成
    /**
     * インスタンスを生成します。
     * @param ... 引数
     * @return Example - インスタンス
     */
    @Override
    protected Example newInstance(Test1 test1, Test2 test2) {
        return new Example(test1, test2);
    }

    // キャッシュを生成する(基本的にはこのメソッドで取得を行う)
    /**
     * Jsonを取得します。
     * @param file - ファイル
     */
    public static ExampleJson get(File file) {
        /**
         * Jsonを取得します。
         * このメソッドは、キャッシュの保存を行います。
         * キャッシュが見つからない場合は、インスタンスの生成を行います。
         * また、キャッシュを利用する場合は基本的に"private"なコンストラクタの実装を推奨します。
         * @param file - ファイル
         * @param cacheJson - キャッシュ
         * @return BaseJson - インスタンス
         */
        return newJson(file, CACHE_JSON);
    }
}

// 要素のクラスを作成(Jsonに保存される要素)
public class Example extends TwoElement<Test1, Test2> {
    // マルチ(引数有り、要素が複数)

    @SerializedName("test1")
    private final Test1 test1;

    @SerializedName("test2")
    private final Test2 test2;

    public Example(Test1 test1, Test2 test2) {
        this.test1 = test1;
        this.test2 = test2;
    }

    // 必ずコンストラクタの引数を返す
    @Override
    protected Test1 getA() {
        return test1;
    }

    // 必ずコンストラクタの引数を返す
    @Override
    protected Test2 getB() {
        return test2;
    }

    // 任意継承
    // 指定された引数が等しいのか比較する
    @Override
    public boolean isElement(Test1 test1, Test2 test2) {
        // 内部処理
        return compare(getA(), test1) && compare(getB(), test2);
    }
}
```