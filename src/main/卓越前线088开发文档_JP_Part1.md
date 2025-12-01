## Superb Warfare 0.8.8 開発ドキュメント

### （零）はじめに

**注意：**このドキュメントを読むには、**JSON**の基礎知識、十分な忍耐力、そして正常に機能する脳が必要です。さらに、解凍ソフトの使用方法を習得しており、一般的な圧縮形式を認識できることが**必須**です。これらの要件を満たさない場合は、直ちにこのドキュメントを閉じてください。

Superb Warfareバージョン0.8.8では、武器と乗り物のメカニズムが大幅に変更され、乗り物のほとんどの属性をデータパックで設定できるようになりました。

新バージョンの乗り物データの場所が変更され、パスは `data/superbwarfare/sbw/vehicles` になりました。これに対応して、乗り物リソースのパスは `assets/superbwarfare/sbw/vehicles` になります。

あなたが**一般プレイヤー**で、Mod内の武器や乗り物の数値を変更したい場合は、本文の（一）と（三）の部分を読んでください。

あなたが**アドオンMod作者**の場合は、本文の（二）、（三）、（四）の部分を読んでください。



### （一）データパック作成 & 使用チュートリアル

#### 必要なツール

推奨：VS Code (Microsoft War Code)

WIP



### （二）アドオンMod作成チュートリアル

#### 必要なツール

推奨：IntelliJ IDEA Community（もちろんUltimateでも構いません）

あなたは一人一人一人 (WIP)



### （三）データファイル

#### 基礎知識

データファイルは武器データと乗り物データの2つの部分に分かれており、武器データは `data/superbwarfare/sbw/guns`、乗り物データは `data/superbwarfare/sbw/vehicles` にあります。

すべてのフィールドは大文字と小文字が区別されます。JSONファイルでは英数字の二重引用符で囲む必要がありますが、数字は囲む必要はありません。

すべての時間単位はデフォルトで**tick（ティック）**です。1秒=20tickです。

単純なデータ型（String、int、doubleなど）の記述例は以下の通りです：

```json
"ID": "superbwarfare:aa_12",
"Damage": 114.5
```

複雑なデータ型（データ自体がオブジェクトである場合）の記述例は以下の通りです：

```json
"Projectile": {
    "Type": "minecraft:arrow",
    "Data": {
        "damage": ...
    }
}
```

JsonObjectは通常のJSONオブジェクトです。

本文では、複雑なデータ型の属性について特別な注釈を付けます。

------

さらに、Superb Warfareではいくつかの特殊なデータ型を提供しています。

**StringToObject<?>**：? はラップされた型を表します。このタイプのデータについては、その型の通常の表現方法を使用することも、プリセットの短縮表現を選択することもできます。

例えば、後述する `StringToObject<ProjectileInfo>` 属性の場合、`Type` が短縮フィールドであれば、以下のように表現できます。

```json
"Projectile": {
    "Type": "superbwarfare:projectile"
}
// または
"Projectile": "superbwarfare:projectile"
```

**ObjectToList<?>**：上記と同様に、このタイプは単一のデータを書き込むことも、リストを書き込むこともできます。

```json
// 単一データ
"AmmoType": "FE"
// 複数データ
"AmmoType": [
    "FE",
    "@RifleAmmo"
]
```

**StringOrVec3**：文字列を入力することも、Vec3のように配列を入力することもできます。

```json
// プリセットフィールドを使用
"SeekDirection": "Default"

// ベクトルを使用
"SeekDirection": [1, 1, 4]
```

#### 武器データ

すべてのフィールドは以下のクラスにあります：

```java
com/atsuishio/superbwarfare/data/gun/DefaultGunData.java
```



##### 基礎データ

|       データ名       |   型   |                             説明                             | デフォルト値 |          例           |
| :------------------: | :----: | :----------------------------------------------------------: | :----------: | :-------------------: |
|          ID          | String | このファイルで変更する武器を指定します。空欄または未記入の場合、デフォルトでファイル名を武器IDとして使用しようとします |      ""      | "superbwarfare:aa_12" |
|    MaxDurability     |  int   | 武器の最大耐久値。未記入の場合はデフォルトで破壊不可<br />1.20.1バージョンではこのデータを動的に変更しようとしないでください |      0       |          114          |
|  DurabilityPerShoot  |  int   |    発射ごとの耐久消費量。上記が0の場合、この属性は無効です   |      1       |          514          |
|      MaxEnergy       |  int   |                        武器の最大エネルギー                        |      0       |         1919          |
|   MaxReceiveEnergy   |  int   |              武器の1回あたりの最大充電エネルギー（通常は使用しません）              |      -1      |          810          |
|   MaxExtractEnergy   |  int   |              武器の1回あたりの最大抽出エネルギー（通常は使用しません）              |      -1      |          893          |
|        Spread        | double |                         武器の弾丸散布界                         |      0       |           -           |
|    ZoomSpreadRate    | double |                       照準時の散布界倍率                       |     0.1      |           -           |
|        Damage        | double |                        武器の基礎ダメージ                        |      0       |           -           |
|       Headshot       | double |                        武器のヘッドショット倍率                        |     1.5      |           -           |
|       Velocity       | double |               武器の発射初速、単位 m/tick               |      0       |           -           |
|    BypassesArmor     | double | 武器の装甲貫通割合（パーセンテージではありません）<br />0.46は46%の貫通を表し、100%を超えることも可能です |      0       |          0.2          |
|   ExplosionDamage    | double |             爆発ダメージ（発射物が爆発物の場合のみ有効）             |      0       |           -           |
|   ExplosionRadius    | double |         爆発半径、単位メートル（発射物が爆発物の場合のみ有効）         |      0       |           -           |
|       Gravity        | double |                      発射物の重力加速度                      |     0.05     |           -           |
|         RPM          |  int   |                          毎分発射速度                          |     600      |           -           |
|       Magazine       |  int   | マガジン容量。**未記入**または0以下の数値を入力した場合、マガジン弾薬ではなくインベントリの弾薬をデフォルトで使用します |      0       |           -           |
|     BurstAmount      |  int   |         武器がBurst発射モードを使用する場合の、1回の連射数          |      0       |           3           |
|    BurstCooldown     |  int   | 武器がBurst発射モードを使用する場合の、連射間の間隔時間（単位はおよそミリ秒） |      30      |           -           |
|        Range         |  int   |               武器の射程、現在はレーザータイプの武器にのみ使用               |     128      |           -           |
|   AmmoCostPerShoot   |  int   |                    発射ごとに消費する弾薬数                    |      1       |           -           |
|   ProjectileAmount   |  int   |       発射ごとに発射される弾数、1より大きい場合はデフォルトでショットガン照準を使用        |      1       |          12           |
|        Weight        |  int   |         武器の重量。移動速度、武器の取り出し速度、ダッシュ撃ち遅延に影響します         |      1       |           -           |
|      ShootDelay      |  int   |                発射前の遅延（単位はおよそミリ秒）                |      0       |           -           |

------

##### 反動

|   データ名    |   型   |                             説明                             | デフォルト値 |    例     |
| :-----------: | :----: | :----------------------------------------------------------: | :----------: | :-------: |
|    RecoilX    | double |                        水平方向の反動                        |      0       |  0.0114   |
|    RecoilY    | double |                        垂直方向の反動                        |      0       |  0.0514   |
|    Recoil     | double |                   実際の反動、プレイヤーを押し出すことができます                   |      0       |    0.5    |
|  RecoilTime   |  int   |                反動の持続時間、乗り物の武器にのみ使用                |      0       |     -     |
|  RecoilForce  | float  |               実際の反動の強さ、乗り物の武器にのみ使用               |      0       |     -     |
|  ShootShake   |  Vec3  | 発射時の周囲のプレイヤーの画面振動幅、形式は[x, y, z]、x：範囲、y：振動時間、z：振幅 |     null     | [5,6,9] |

------

##### ズーム

|   データ名    |   型   |              説明              | デフォルト値 | 例 |
| :-----------: | :----: | :----------------------------: | :----------: | :--: |
|  DefaultZoom  | double |     武器のデフォルト照準倍率     |     1.25     |  5   |
|    MinZoom    | double | 最小倍率、空欄の場合はデフォルト倍率と同じ |      -       | 1.5  |
|    MaxZoom    | double | 最大倍率、空欄の場合はデフォルト倍率と同じ |      -       |  8   |

------

##### 近接攻撃

|     データ名      |   型   |                    説明                    | デフォルト値 | 例 |
| :---------------: | :----: | :----------------------------------------: | :----------: | :--: |
|    MeleeDamage    | double |     武器の近接ダメージ、未記入の場合は近接攻撃不可     |      0       |  10  |
|   MeleeDuration   |  int   |           攻撃モーション全体の持続時間           |      16      |  -   |
|  MeleeDamageTime  |  int   | 近接攻撃開始後、何tick後に近接ダメージを与えるか |      6       |  -   |

------

##### 弾薬/発射体

複雑なデータ型に属します。

|   データ名   |               型               |         説明         |           デフォルト値           |  例  |
| :----------: | :------------------------------: | :------------------: | :------------------------: | :--: |
|  Projectile  | StringToObject\<ProjectileInfo\> | 発射するエンティティの登録名 | "superbwarfare:projectile" | 下記参照 |

```json
"Projectile": {
  "Type": "minecraft:arrow",
  "Data": {
    "damage": "@sbw:damage",
    "pickup": 2,
    "crit": false
  }
}
```

ProjectileInfoの属性は以下の通りです：

| データ名 |     型     |                         説明                          |           デフォルト値           | 例 |
| :------: | :--------: | :---------------------------------------------------: | :------------------------: | :--: |
|   Type   |   String   | 発射するエンティティの登録名。**"ray"**と記入するとレーザー武器になります | "superbwarfare:projectile" |  -   |
|   Data   | JsonObject |                       エンティティのNBT                       |            null            |  -   |

ここでTypeはプリセットの短縮フィールドです。

Modは属性を自動的に置換するいくつかのフィールドを保持しています。例えば、例のdamage部分は `@sbw:damage` を使用しており、この投射物のNBTを武器ダメージに自動的に置換します。使用可能なすべてのフィールドは以下の通りです：

|         データ名          |   型   |           説明           |
| :-----------------------: | :----: | :----------------------: |
|        @sbw:owner         |  UUID  | データを射撃手のUUIDに置換します |
|  @sbw:owner_string_lower  | String |  全小文字形式のUUID文字列  |
|  @sbw:owner_string_upper  | String |  全大文字形式のUUID文字列  |
|        @sbw:damage        | double |   武器のダメージ数値に置換します   |
|   @sbw:explosion_radius   | double | 武器の爆発半径数値に置換します |
|        @sbw:spread        | double |   武器の拡散数値に置換します   |

------

##### 発射モード

|       データ名       |                      型                      |                             説明                             |  デフォルト値  | 例 |
| :------------------: | :--------------------------------------------: | :----------------------------------------------------------: | :------: | :--: |
|   DefaultFireMode    |                     String                     | 武器のデフォルト発射モード。デフォルトで以下の3つを提供、1つを選択して記入<br/>"Semi", "Burst", "Auto" | ["Semi"] |  -   |
|  AvailableFireModes  | ObjectToList\<StringToObject\<FireModeInfo\>\> |                    武器で使用可能なすべての発射モード                    |  "Semi"  | 下記参照 |

FireModeInfoは複雑なデータ型に属し、属性は以下の通りです：

|  データ名  |     型     |                             説明                             | デフォルト値 |  例  |
| :--------: | :--------: | :----------------------------------------------------------: | :----: | :----: |
|    Mode    |    Enum    | 組み込みの発射モード列挙定数。以下の3つのみ："Semi", "Burst", "Auto" | "Semi" |   -    |
|    Name    |   String   | 発射モードの名前。DefaultFireModeはこの名前を通じて発射モードを検索できます | "Semi" | "Hold" |
|  Override  | JsonObject |                    基底クラスの特定の属性を上書きするために使用                    |  null  |  下記参照  |

ここでModeとNameはプリセットの短縮フィールドです。

AvailableFireModesの例は以下の通りです：

```json
// 通常の武器
"AvailableFireModes": ["Semi", "Auto"]

// 属性の上書きを行う
"AvailableFireModes": [
    "Auto",
    {
      "Mode": "Semi",
      "Name": "Semi",
      "Override": {
        "RecoilX": 0.004,
        "RecoilY": 0.009,
        "Spread": 6,
        "Damage": 200,
        "SoundRadius": 12,
        "Headshot": 2.5,
        "Velocity": 51,
        "BypassesArmor": 0.7,
        "HeatPerShoot": 5,
        "RPM": 300,
        "AmmoCostPerShoot": 1000
      }
    }
],
"Damage": 10
```

属性の上書きを持つ発射モードの場合、武器がこのモードに切り替わると、データがそれに応じて変化します。上記の記述では、武器ダメージは発射モードが "Auto" の時は10に設定され、"Semi" に切り替わると200に設定されます。

------

##### 弾薬タイプ

複雑なデータ型に属します。

|  データ名  |                      型                      |           説明           | デフォルト値 | 例 |
| :--------: | :--------------------------------------------: | :----------------------: | :----: | :--: |
|  AmmoType  | ObjectToList\<StringToObject\<AmmoConsumer\>\> | 武器の発射に必要な弾薬 |   []   | 下記参照 |

AmmoConsumerの属性は以下の通りです：

|    データ名    |               型               |                             説明                             |                            デフォルト値                            |     例     |
| :------------: | :------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------: |
|      Ammo      |              String              |                       弾薬タイプの識別子                       |                             null                             | "@RifleAmmo" |
|    AmmoSlot    |              String              | 弾薬スロット。**同じスロット**を持つ2つの弾薬タイプ間で切り替える場合、デフォルトで弾薬がアンロードされます<br />"Melee"と記入すると、弾薬のアンロードはトリガーされず、武器は純粋な近接モードになります |                          "Default"                           |      -       |
|   Projectile   | StringToObject\<ProjectileInfo\> |                       発射される投射物情報                       |                             null                             |     下記参照     |
|      Icon      |              String              |                この弾薬タイプのアイコン、乗り物の武器に使用                | "superbwarfare:textures/<br />overlay/vehicle/weapon/icons/empty.png" |      -       |
|  ShouldUnload  |             boolean              |      他の弾薬タイプからこのタイプに**切り替える**際、弾薬をアンロードする必要があるか      |                             true                             |    false     |
|    Override    |            JsonObject            |                    基底クラスの特定の属性を上書きするために使用                    |                             null                             |     下記参照     |

ここでAmmoはプリセットの短縮フィールドです。

AmmoTypeには複数の記述方法があり、ここの短縮方法はAmmo属性に対応しています。例は以下の通りです：

```json
// 1. アイテム登録名を使用
"AmmoType": "minecraft:arrow"

// 2. アイテムタグを使用、"#"で始まる
"AmmoType": "#minecraft:logs"

// 3. 基礎弾薬タイプを使用、"@"で始まる。@HandgunAmmo, @RifleAmmo, @ShotgunAmmo, @SniperAmmo, @HeavyAmmoの計5種類があります
"AmmoType": "@RifleAmmo"

// 4. その他のプレハブタイプを使用
// "empty" は空タイプに対応
// "infinity", "infinite" は無限弾薬に対応
// "fe", "rf", "energy" はエネルギーを弾薬として使用することに対応
"AmmoType": "empty"
```

複数のAmmoType、または複雑な記述の場合の例は以下の通りです：

```json
"AmmoType": [
    "superbwarfare:grenade_40mm",
    {
      "Ammo": "@ShotgunAmmo",
      "AmmoSlot": "Default",
      "Projectile": "superbwarfare:projectile",
      "Override": {
        "Damage": 10,
        "Headshot": 1.75,
        "BypassesArmor": 0.2,
        "ExplosionDamage": 0,
        "ProjectileAmount": 6,
        "Velocity": 10,
        "RecoilX": 0.006,
        "RecoilY": 0.018,
        "Crosshair": "@GunDefault"
      }
    },
    {
      "AmmoSlot": "Melee",
      "Override": {
        "ProjectileAmount": 0,
        "MeleeDuration": 15,
        "Crosshair": "@GunDefault"
      }
    }
]
```

上記の記述は、この武器が3種類の弾薬タイプを持つことを示しています。1つ目はグレネードを消費し、2つ目はショットガンの弾薬を消費して発射物を弾丸に変更し、3つ目は純粋な近接モードであり、属性の上書きを使用して発射物の数を上書きしています。

------

##### 熱量

|       データ名        |   型   |            説明            | デフォルト値 | 例 |
| :-------------------: | :----: | :------------------------: | :----: | :--: |
|     HeatPerShoot      | double |   武器が発射ごとに発生させる熱量   |   0    |  -   |
|    NaturalCooldown    | double | 自然状態で毎tick減少する熱量 |  0.25  |  -   |
|  InWaterCooldownRate  | double |  水中または雨の中での放熱倍率  |  1.1   |  -   |
|  InSnowCooldownRate   | double |    粉雪の中での放熱倍率    |  1.5   |  -   |
|  InFireCooldownRate   | double |    炎の中での放熱倍率    |  0.6   |  -   |
|  InLavaCooldownRate   | double |    溶岩の中での放熱倍率    |  0.2   |  -   |

武器の毎tickの放熱量は、プレイヤーがいる環境に応じて対応する倍率が乗算されます。

------

##### リロード

この部分は非常に抽象的ですが、幸いなことに乗り物の武器ではそれほど多くの属性を使用しません。

まずはリロードタイプです：

|    データ名     |        型         |        説明        |    デフォルト値    |           例            |
| :-------------: | :---------------: | :----------------: | :----------: | :-----------------------: |
|   ReloadTypes   | Set\<ReloadType\> | 使用可能なすべてのリロードタイプ | ["Magazine"] | ["Magazine", "Iterative"] |

リロードタイプは "Magazine"（マガジン式）、"Clip"（クリップ式）、"Iterative"（逐次装填式）の中から任意に選択できます。

次にリロード関連の属性です：

|        データ名         |   型    |                             説明                             | デフォルト値 | 例 |
| :---------------------: | :-----: | :----------------------------------------------------------: | :----: | :--: |
|    NormalReloadTime     |   int   |                武器の通常リロード時間、単位はtick                |   0    |  -   |
|     EmptyReloadTime     |   int   |                      武器の空倉リロード時間                      |   0    |  59  |
|     BoltActionTime      |   int   |                     ボルトアクション武器のボルト操作時間                     |   0    |  22  |
|       PrepareTime       |   int   |                  薬室に弾がある時の、リロード準備時間                  |   0    |  29  |
|     PrepareLoadTime     |   int   | 薬室に弾がない時、準備段階で薬室に1発装填する必要がある場合のリロード準備時間<br />（M870やSecond Cataclysmのように、リロード準備期間中に薬室に1発装填する武器に適用） |   0    |  -   |
|    PrepareEmptyTime     |   int   | 薬室に弾がない時、準備段階で薬室に1発装填する必要がない場合のリロード準備時間 |   0    |  -   |
|   PrepareAmmoLoadTime   |   int   | 準備段階で装填が必要な場合、準備段階終了まで残りどれくらいの時に薬室に1発装填するか |   1    |  -   |
|      IterativeTime      |   int   |                      単発装填ごとの時間                      |   0    |  -   |
|  IterativeAmmoLoadTime  |   int   | 武器のリロードタイプがIterativeの場合、装填タイマーがこのtick残っている時に薬室に1発装填します |   1    |  -   |
|   IterativeLoadAmount   |   int   |     武器のリロードタイプがIterativeの場合、単発装填ごとの装填数      |   1    |  -   |
|       FinishTime        |   int   |                      リロード終了段階の時間                      |   0    |  -   |
|       AutoReload        | boolean |                       自動リロードが可能か                       | false  |  -   |
|       ZoomReload        | boolean |                      照準時にリロードが可能か                      |  true  |  -   |

------

##### 使用可能なモジュール

|     データ名     |          型          |        説明        | デフォルト値 | 例 |
| :--------------: | :--------------------: | :----------------: | :----: | :--: |
|  AvailablePerks  | ObjectToList\<String\> | 使用可能な武器モジュールリスト |  下記参照  |  -   |

デフォルト値は以下の通りです：

```json
"AvailablePerks": [
    "@Ammo",
    "superbwarfare:field_doctor",
    "superbwarfare:powerful_attraction",
    "superbwarfare:intelligent_chip",
    "superbwarfare:monster_hunter",
    "superbwarfare:vorpal_weapon",
    "!superbwarfare:micro_missile",
    "!superbwarfare:longer_wire",
    "!superbwarfare:cupid_arrow"
]
```

以下のタイプを入力できます：

- @ で始まり、Ammo, Functional, Damage を続けると、そのタイプのすべてのモジュールが使用可能であることを示します
- モジュール登録名、そのモジュールが使用可能であることを示します
- 英語の感嘆符 ! で始まり、モジュール登録名を続けると、そのモジュールが使用不可であることを示します

モジュールの使用可否の関係は順序の影響を受けます。必ず @ を先頭に、! を最後に配置してください。

------

##### ダメージ減衰

複雑なデータ型に属します。これを書くことはお勧めしません。プリセットを使用することをお勧めします。

|    データ名    |     型     |                        説明                        | デフォルト値 | 例 |
| :------------: | :----------: | :------------------------------------------------: | :----: | :--: |
|  DamageReduce  | DamageReduce | 武器弾薬の距離減衰タイプ。距離減衰が不要な武器は記述しなくてよい |  下記参照  |  -   |

DamageReduceの属性は以下の通りです：

|   データ名    |    型    |                            説明                            | デフォルト値 | 例 |
| :-----------: | :--------: | :--------------------------------------------------------: | :----: | :--: |
|     Type      | ReduceType |              プリセット減衰タイプ。以下の2つの属性を上書きします              |  null  |  -   |
|     Rate      |   double   |                武器弾薬ダメージの距離による減衰率                |   0    | 0.05 |
|  MinDistance  |   double   | 武器弾薬ダメージの最小減衰距離。この距離を超えた弾薬はダメージが低下し始めます |   0    |  15  |

武器ダメージ減衰式：

```
最終ダメージ = 元のダメージ / (1 + Rate * max(0, 現在距離 - MinDistance))
```

プリセットの減衰タイプは以下の通りです：

|  Name   |  Rate  | MinDistance |
| :-----: | :----: | :---------: |
| Shotgun |  0.05  |     15      |
| Sniper  | 0.001  |     150     |
|  Heavy  | 0.0007 |     250     |
| Handgun |  0.03  |     40      |
|  Rifle  | 0.007  |     100     |
|   Smg   |  0.02  |     50      |
|  Empty  |   0    |      0      |

したがって、以下のように記述できます：

```json
//  プリセットを使用
"DamageReduce": {
    "Type": "Handgun"
}

// プリセットを使用しない
"DamageReduce": {
    "Rate": 0.114,
    "MinDistance": 514
}
```

------

##### 表示効果

なぜこれがクライアントリソースにないのかは聞かないでください。現在のOverrideの書き方はこれをDataに放り込むことしかサポートしていません。

|      データ名      |   型   |               説明                |                          デフォルト値                          |    例    |
| :----------------: | :------: | :-------------------------------: | :------------------------------------------------------: | :--------: |
|        Icon        |  String  |     武器アイコン、右下のあれです      | "superbwarfare:textures/<br />gun_icon/default_icon.png" |     -      |
|     Crosshair      |  String  |           腰だめ撃ち時の照準            |                      "@GunDefault"                       |     -      |
|  CrosshairZooming  |  String  |           照準時の照準            |                         "@Empty"                         |     -      |
|   CrosshairColor   | ModColor | 照準の色、形式はRGB、乗り物の武器に使用 |                            -                             | "0xFFFFFF" |
|        Name        |  String  |    武器の表示名、乗り物の武器に使用     |                           null                           |     -      |

ここでCrosshairとCrosshairZoomingは一部のプリセット値を提供しています。例えば：

- @Empty 何もなし
- @GunDefault デフォルト銃器照準
- @VehicleDefault デフォルト乗り物照準、その他の乗り物照準は後述
- @Custom カスタム、自分でコードを使用してレンダリングする必要があります

実際にはここにResourceLocation、つまり照準画像のパスを書き込むこともできますが、現在は乗り物の照準に対してのみ有効です。

ModColorは色の属性であり、複数の形式で書き込むことができます：

```json
// 文字列を渡すことができます。プレフィックスは "#"、"0x"、あるいは書かなくても構いません
"CrosshairColor": "#114514"
"CrosshairColor": "0x114514"
"CrosshairColor": "114514"

// 文字列を書かずに、int形式の数字を直接渡すこともできます
"CrosshairColor": 114514
```

------

##### ロックオンメカニズム

一部の銃器や乗り物の武器はターゲットロックが可能です。まずは銃器のロックオンから：

|     データ名      |      型      |          説明          | デフォルト値 | 例 |
| :---------------: | :------------: | :--------------------: | :----: | :--: |
|     SeekType      |      Enum      | どのような状況でロックオンを行うか | "None" |  -   |
|     SeekTime      |      int       |     ロックオンに必要な時間     |   20   |  -   |
|     SeekAngle     |     double     |       ロックオン角度       |   10   |  -   |
|     SeekRange     |     double     |       ロックオン範囲       |  384   |  -   |
|  MinTargetHeight  |     double     | ロックオン対象の最小地上高 |   0    |  -   |
|  MaxTargetHeight  |     double     | ロックオン対象の最大地上高 | 114514 |  -   |
|  SeekWeaponInfo   | SeekWeaponInfo |  乗り物武器専用ロックオンデータ  |  null  | 下記参照 |

ここでSeekTypeは "None", "HoldFire", "HoldZoom" の3つから1つを選択でき、それぞれロックオンなし、発射キー長押し時にロックオン、照準時にロックオンに対応します。

乗り物武器専用のSeekWeaponInfoについては、属性は以下の通りです：

|       データ名        |     型     |               説明               |  デフォルト値   | 例 |
| :-------------------: | :----------: | :------------------------------: | :-------: | :--: |
|     SeekDirection     | StringOrVec3 |            ロックオン方向            | "Default" |  -   |
|       SeekTime        |     int      |          ロックオンに必要な時間          |    10     |  -   |
|       SeekAngle       |    double    |            ロックオン角度            |    20     |  -   |
|       SeekRange       |    double    |            ロックオン範囲            |    384    |  -   |
|    MinTargetHeight    |    double    |      ロックオン対象の最小地上高      |     0     |  -   |
|    MaxTargetHeight    |    double    |      ロックオン対象の最大地上高      |  114514   |  -   |
|     MinTargetSize     |    double    |       ロックオン対象の最小体積       |     0     |  -   |
|  CalculateTrajectory  |   boolean    | 弾道を計算し、予測照準枠を表示するかどうか |   false   |  -   |
|     OnlyLockBlock     |   boolean    |        ブロック座標のみロックするか        |   false   |  -   |
|    OnlyLockEntity     |   boolean    |          エンティティのみロックするか          |   false   |  -   |

なぜSeekWeaponInfoに上位と重複するデータがあるのかについては、チュートリアル作者も今のところ不明です。

将来的に「なぜこんな設計にしたのか」と気づいてこの部分を整理する可能性も排除できません。

------

##### 効果音

複雑なデータ型に属します。

|  データ名   |   型    |        説明        | デフォルト値 | 例 |
| :---------: | :-------: | :----------------: | :----: | :--: |
|  SoundInfo  | SoundInfo | 各種状況で再生される効果音 |   -    | 下記参照 |

SoundInfoの属性は以下の通りです：

|         データ名         |          型          |                   説明                   |             デフォルト値              | 例 |
| :----------------------: | :--------------------: | :--------------------------------------: | :-----------------------------: | :--: |
|          Fire1P          |       SoundEvent       |             一人称発射音             |              null               |  -   |
|          Fire3P          |       SoundEvent       |             三人称発射音             |              null               |  -   |
|        Fire3PFar         |       SoundEvent       |        三人称発射音（遠距離）        |              null               |  -   |
|      Fire3PVeryFar       |       SoundEvent       |      三人称発射音（超遠距離）      |              null               |  -   |
|       Fire1PSilent       |       SoundEvent       |       サプレッサー装着時の一人称発射音       |              null               |  -   |
|       Fire3PSilent       |       SoundEvent       |       サプレッサー装着時の三人称発射音       |              null               |  -   |
|     Fire3PFarSilent      |       SoundEvent       |  サプレッサー装着時の三人称発射音（遠距離）  |              null               |  -   |
|   Fire3PVeryFarSilent    |       SoundEvent       | サプレッサー装着時の三人称発射音（超遠距離） |              null               |  -   |
|       ReloadNormal       |       SoundEvent       |              通常リロード音              |              null               |  -   |
|       ReloadEmpty        |       SoundEvent       |              空倉リロード音              |              null               |  -   |
|      VehicleReload       |       SoundEvent       |              乗り物リロード音              |              null               |  -   |
|     VehicleReload3p      |       SoundEvent       |          乗り物リロードの三人称音          |              null               |  -   |
|  VehicleReloadSoundTime  |          int           |          乗り物リロード音の再生時間          |                0                |  -   |
|      ReloadPrepare       |       SoundEvent       |            リロード準備段階の音            |              null               |  -   |
|    ReloadPrepareEmpty    |       SoundEvent       |          空倉リロード準備段階の音          |              null               |  -   |
|    ReloadPrepareLoad     |       SoundEvent       |          リロード準備段階の装填音          |              null               |  -   |
|        ReloadLoop        |       SoundEvent       |          単発リロードの繰り返し装填音          |              null               |  -   |
|        ReloadEnd         |       SoundEvent       |            単発リロード終了音            |              null               |  -   |
|           Bolt           |       SoundEvent       |                ボルト操作音                |              null               |  -   |
|          Change          |       SoundEvent       |       武器切り替え音、乗り物の武器に使用       |              null               |  -   |
|         Locking          |       SoundEvent       |            ターゲットロック中の音            | "minecraft:intentionally_empty" |  -   |
|          Locked          |       SoundEvent       |             ターゲットロック完了音             | "minecraft:intentionally_empty" |  -   |
|    FireSoundInstances    |       SoundEvent       |              乗り物の発射音              |              null               |  -   |
|    CancellableSounds     | ObjectToList\<String\> |        武器切り替え時に再生をキャンセルすべき音        |               []                | 下記参照 |

Superb WarfareはSoundEventの解析ソリューションを提供しており、形式は以下の通りです：

```json
// 効果音名を直接記入できます
"Fire1P": "superbwarfare:heng"

// 固定範囲の効果音の場合、名前の後ろにスペースを空けて範囲を記述します
"Fire3P": "superbwarfare:heng 114"
```

以下は武器の効果音の例です：

```json
"SoundInfo": {
    "Fire1P": "superbwarfare:javelin_fire_1p",
    "Fire3P": "superbwarfare:javelin_fire_3p",
    "Fire3PFar": "superbwarfare:javelin_far",
    "ReloadEmpty": "superbwarfare:javelin_reload_empty",
    "Locking": "superbwarfare:javelin_locking",
    "Locked": "superbwarfare:javelin_locked",
    "CancellableSounds": [
      "superbwarfare:javelin_reload_empty",
      "superbwarfare:javelin_locking",
      "superbwarfare:javelin_locked"
    ]
}
```

効果音もOverrideを通じて上書きできます。
