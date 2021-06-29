# GsonLib [Java11](https://adoptopenjdk.net/?variant=openjdk11) [![](https://jitpack.io/v/yuttyann/GsonLib.svg)](https://jitpack.io/#yuttyann/GsonLib)

# 概要
[ScriptBlockPlus](https://github.com/yuttyann/ScriptBlockPlus)で利用している、自作ライブラリの単品化を行いました。  
`(これに伴い、BukkitAPIへの依存を無くしました。)`  
基礎となるJsonクラス、Elementクラスを実装することで簡単にJsonを実装することが可能です。  
**(内部的に[Gson](https://github.com/google/gson)を利用しているため、仕様等に関しては各自調べてください。)**

# 使用ライブラリ
| Name | Description | Version |
|:---|:---|:---:|
| [Gson](https://github.com/google/gson) | Jsonを扱うメインのライブラリです。 | **2.8.6** |
| [Guava](https://github.com/google/guava) | Multimapを利用しています。 | **30.0-jre** |
| [Netty](https://github.com/netty/netty) | 高速化のためIntMapを利用しています。 | **4.1.50.Final** |
| [Junit](https://github.com/junit-team/junit4) | デバッグを行うために利用しています。 | **4.13.1** |

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
    <version>1.0.2</version>
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
　　　implementation 'com.github.yuttyann:GsonLib:1.0.2'
　}
```

## 参考
**[基礎クラス]** [BaseJson.java](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/BaseJson.java), [BaseElement.java](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/BaseElement.java)  
**[派生クラス]** [Basics](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/)

## 使い方
基本的には、上記の**基礎、派生クラス**等を参照してください。  
今回は[SingleJson](https://github.com/yuttyann/GsonLib/blob/main/src/main/java/com/github/yuttyann/gsonlib/basic/SingleJson.java)の仕様を元に、基本的な機能について解説をしていきます。

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
**ファイルを削除する**
```java
// Jsonの取得
File jsonFile = new File(...);
ExampleJson json = new ExampleJson(jsonFile);

// ファイルの削除(キャッシュも削除する)
json.deleteFile();
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
@JsonTag(limit = 100000)

// 整形時のインデント
@JsonTag(indent = "  ")

// ファイルを保存した時にキャッシュを削除するのかどうか
@JsonTag(temporary = false)

// ファイルが存在する時のみキャッシュを保存するのかどうか
@JsonTag(cachefileexists = true)


@JsonTag(...)
public class ExampleJson extends SingleJson<Example> {
    ........
}
```

## Excludeの解説
[Exclude](https://github.com/yuttyann/GsonLib/blob/main/src/main/java/com/github/yuttyann/gsonlib/annotation/Exclude.java)は、上記と同じ`注釈`です。  
デシリアライズ化を行う際に、この注釈を付与したフィールドをスルーすることができます。  

**実装方法**
```java
public class Example extends SingleElement {

    @SerializedName("flag")
    private boolean flag1;

    @Exclude
    private boolean flag2;

    ........
}
```

## クラスの作成
基本的には、要素の内容が保存されます。  
また、Jsonの管理はxxxJsonを継承したクラスで行います。  

**実装例(ScriptBlockPlus)**  
Json &lt;[BlockScriptJson.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/derived/BlockScriptJson.java)&gt;, Element &lt;[BlockScript.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/element/BlockScript.java)&gt;  
Json &lt;[PlayerTimerJson.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/derived/PlayerTimerJson.java)&gt;, Element &lt;[PlayerTimer.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/element/PlayerTimer.java)&gt;  
Json &lt;[PlayerCountJson.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/derived/PlayerCountJson.java)&gt;, Element &lt;[PlayerCount.java](https://github.com/yuttyann/ScriptBlockPlus/blob/master/src/main/java/com/github/yuttyann/scriptblockplus/file/json/element/PlayerCount.java)&gt;  

**キャッシュの実装**  
クラスの詳細 &lt;[CacheJson.java](https://github.com/yuttyann/GsonLib/blob/main/src/main/java/com/github/yuttyann/gsonlib/CacheJson.java)&gt;  
`(基本的に実装方法は変わらないため、SingleJsonを利用している場合を前提に話を進めます。)`
```java
@JsonTag(...)
public class ExampleJson extends SingleJson<Example> {

    // キャッシュを行う場合は、必ず実装する
    static {
        CacheJson.register(ExampleJson.class);
    }

    // 安全性の面でアクセス修飾子を"private"へ変更する
    private ExampleJson(File file) {
        super(file);
    }

    // 標準装備
    @Override
    protected Example newInstance() {
        return new Example();
    }

    // インスタンスを生成します(キャッシュが存在する場合は、そこから取得します)
    public static ExampleJson newJson(File file) {
        return newJson(ExampleJson.class, file);
    }
}
```

**引数無し、要素が一つのみ**  
クラスの詳細 &lt;[SingleJson.java](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/SingleJson.java)&gt;  
クラスの詳細 &lt;[SingleElement.java](https://github.com/yuttyann/GsonLib/blob/main/src/main/java/com/github/yuttyann/gsonlib/basic/SingleJson.java#L40)&gt;

```java
// Json
@JsonTag
public class ExampleJson extends SingleJson<Example> {

    /**
     * コンストラクタ
     * @param file - ファイル
     */
    public ExampleJson(File file) {
        super(file);
    }

    /**
     * インスタンスを生成します。
     * @return Example - インスタンス
     */
    @Override
    protected Example newInstance() {
        return new Example();
    }
}

// Element
public class Example extends SingleElement {

    @SerializedName("flag")
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
引数の数を変更したい場合は、[OneJson](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/OneJson.java)や[ThreeJson](https://github.com/yuttyann/GsonLib/tree/main/src/main/java/com/github/yuttyann/gsonlib/basic/ThreeJson.java)を利用してください。  
また、引数の数を４つ以上にしたい場合は、クラス構造を複製して単純に引数の数を増やしてください。

```java
// Json
@JsonTag
public class ExampleJson extends TwoJson<Integer, Integer, Example> {

    /**
     * コンストラクタ
     * @param file - ファイル
     */
    public ExampleJson(File file) {
        super(file);
    }

    /**
     * インスタンスを生成します。
     * @param x 引数X
     * @param y 引数Y
     * @return Example - インスタンス
     */
    @Override
    protected Example newInstance(Integer x, Integer y) {
        return new Example(x, y);
    }
}

// Element
public class Example extends TwoElement<Integer, Integer> {

    @SerializedName("x")
    private final Integer x;

    @SerializedName("y")
    private final Integer y;

    @SerializedName("option")
    private int value;

    public Example(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    // 必ずコンストラクタの引数1を返す
    @Override
    protected Integer getA() {
        return x;
    }

    // 必ずコンストラクタの引数2を返す
    @Override
    protected Integer getB() {
        return y;
    }

    // 任意のメソッド
    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
```

**使用例**  
上記に記述したクラスを実際に使ってみましょう。
```java
// filesフォルダの内部に生成する。
File jsonFile = new File("files/example.json");

// ExampleJson を生成する。
ExampleJson json = new ExampleJson(jsonFile);

// 要素に、任意の値を設定する。
json.load(1, 2).setValue(100); // x:1,y:2を指定 値を100に設定
json.load(1, 2).setValue(50);  // x:1,y:2を指定 値を50に設定
json.load(0, 1).setValue(100); // x:0,y:1を指定 値を100に設定

// Jsonの保存を行う。
json.saveJson();

// 結果表示
System.out.println("toString: " + json.toString());
// toString: [{"x":1,"y":2,"option":50},{"x":0,"y":1,"option":100}]
```