------

##### ダメージ軽減

Superb Warfareの乗り物の強さの半分は、超高倍率のダメージ軽減によるものです。

|          データ名           |                      型                      |           説明           | デフォルト値 |  例   |
| :-------------------------: | :--------------------------------------------: | :----------------------: | :----: | :---: |
| ApplyDefaultDamageModifiers |                    boolean                     | デフォルトのダメージ軽減修飾子を使用するかどうか |  true  | false |
|       DamageModifiers       | ObjectToList\<StringToObject\<DamageModify\>\> |        ダメージ軽減修飾子        |   []   | 下記参照  |

DamageModifyは複雑なデータ型で、属性は以下の通りです：

| データ名 |    型    |     説明     |   デフォルト値   | 例 |
| :----: | :--------: | :----------: | :--------: | :--: |
|  Type  | ModifyType | ダメージ修飾タイプ | "Immunity" |  -   |
| Value  |   float    | 操作する数値 |     0      |  -   |
| Source |   String   |   ダメージ源   |   "All"    |  -   |

ここでModifyTypeは列挙型で、以下の4種類に分かれます：

- "Immunity" 完全無効
- "Reduce" 固定値ダメージ軽減
- "Multiply" 指定値を乗算
- "Invalid" 解析無効。書き間違えた場合は自動的にこれとして処理されます

ダメージ源属性Sourceも複数のタイプに分かれます。以下は詳細な書き方です：

```json
// 全体ダメージ軽減
"Source": "All"

// 特定のダメージタイプに対する軽減
"Source": "minecraft:lava"

// 特定のカテゴリのダメージタイプに対する軽減（ダメージタイプタグを使用）
"Source": "#superbwarfare:projectile"

// 特定のエンティティによるダメージに対する軽減
"Source": "@minecraft:arrow"

// 特定のカテゴリのエンティティによるダメージに対する軽減（エンティティタグを使用）
"Source": "@#superbwarfare:aerial_bomb"

// カスタム関数計算。バージョン0.8.8では未有効
"Source": "$function(xxx)"
```

したがって、DamageModifyの例は以下のようになります：

```json
"DamageModifiers": [
    {
        "Source": "All",
        "Type": "Reduce",
        "Value": 8
    }
]
```

この書き方の効果は、すべてのダメージを8ポイント減少させることです。

もちろん、このように書くのは面倒なので、より簡単な解析方法が提供されています：

```json
"DamageModifiers": [
	"All - 8"
]
```

効果は上記と同じです。

つまり、以下のような短縮形式で記述できます：

```json
"DamageModifiers": [
    // 短縮形の基本フォーマット
    "タイプ 演算子 数値",
    
    // 固定値軽減の書き方
    "All - 8",
    // 乗算軽減の書き方
    "All * 0.2",
    // 完全無効の書き方
    "All 0"
]
```

では、例を見てみましょう。完全な乗り物ダメージ軽減の例は以下の通りです：

```json
"DamageModifiers": [
    "minecraft:lava - -11",
    "minecraft:lava * 10",
    "All * 0.2",
    "minecraft:arrow * 1.5",
    "minecraft:trident * 1.5",
    "minecraft:mob_attack * 2.5",
    "minecraft:mob_attack_no_aggro * 2",
    "minecraft:mob_projectile * 1.5",
    "minecraft:explosion * 6",
    "minecraft:player_explosion * 6",
    "superbwarfare:custom_explosion * 2.0",
    "superbwarfare:projectile_explosion * 2",
    "superbwarfare:mine * 0.75",
    "superbwarfare:projectile_hit * 1.25",
    "superbwarfare:grapeshot_hit * 0.3",
    "superbwarfare:laser * 1.25",
    "@#superbwarfare:aerial_bomb * 3",
    "#superbwarfare:projectile * 0.25",
    "#superbwarfare:projectile_absolute * 0.85",
    "superbwarfare:vehicle_strike * 4",
    "@superbwarfare:mortar_shell * 1.25",
    "@superbwarfare:gun_grenade * 1.5",
    "@superbwarfare:javelin_missile * 0.8",
    "@superbwarfare:igla_9k38_missile * 0.75",
    "All - 11"
]
```

DamageModifyには優先順位があることに注意してください。計算の優先順位：無効 > 固定値軽減 > 乗算。操作方法が同じ場合は、上から下の順に処理されます。

バージョン0.8.8では、カスタムダメージ軽減アルゴリズムを使用したい場合、依然としてコードを手書きする必要があります。

例えば：

```java
@Override
public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> {
                    damage *= getHealth() > 0.1f ? 1 : 0.05f;
                    return damage;
                });
}
```

ApplyDefaultDamageModifiers属性がtrueの場合、つまりデフォルトのダメージ軽減修飾子が有効になっている場合、以下の効果が自動的に付与されます：

```java
public static DamageModifier createDefaultModifier() {
        return new DamageModifier()
                .immuneTo(EntityType.POTION)
                .immuneTo(EntityType.AREA_EFFECT_CLOUD)
                .immuneTo(DamageTypes.FALL)
                .immuneTo(DamageTypes.CACTUS)
                .immuneTo(DamageTypes.DROWN)
                .immuneTo(DamageTypes.DRAGON_BREATH)
                .immuneTo(DamageTypes.WITHER)
                .immuneTo(DamageTypes.WITHER_SKULL);
}

// および追加の衝突ダメージ軽減
modifier.reduce(5, ModDamageTypes.VEHICLE_STRIKE);
```

------

##### 破壊後の挙動

|   データ名    |    型     |       説明       | デフォルト値 | 例 |
| :---------: | :---------: | :--------------: | :----: | :--: |
| DestroyInfo | DestroyInfo | 乗り物が破壊された後の挙動 |   -    | 下記参照 |

DestroyInfoは複雑なデータ型で、属性は以下の通りです：

|      データ名       |           型            |                             説明                             | デフォルト値 | 例 |
| :-----------------: | :-----------------------: | :----------------------------------------------------------: | :----: | :--: |
|   CrashPassengers   |          boolean          |                    乗客に墜落ダメージを与えるかどうか                    | false  |  -   |
|  ExplodePassengers  |          boolean          |                  乗客に乗り物破壊ダメージを与えるかどうか                  |  true  |  -   |
|    ExplodeBlocks    |          boolean          |                  破壊時の爆発がブロックを破壊するかどうか                  |  true  |  -   |
|   ExplosionDamage   |           float           |                       破壊時の爆発ダメージ                       |   0    |  -   |
|   ExplosionRadius   |           float           |                       破壊時の爆発半径                       |   0    |  -   |
|    ParticleType     | ParticleTool.ParticleType | 破壊時の爆発パーティクル効果。以下の5つから1つを選択：<br />"Mini", "Small", "Medium", "Huge", "Giant" | "Mini" |  -   |

ここで墜落ダメージとは、N回（デフォルトは5回）の大量ダメージ（デフォルト114514）を与えることです。これらの数値はサーバー設定ファイルにあり、将来的にデータパックで設定できるようになる可能性も排除できません。乗り物破壊ダメージは、単発の大量ダメージを与えることです。

DestroyInfoの例は以下の通りです：

```json
"DestroyInfo": {
    "ParticleType": "Giant",
    "ExplosionDamage": 800,
    "ExplosionRadius": 16,
    "CrashPassengers": true
}
```

（ジョーク：なんて脆い缶詰だ）

------

##### 自動索敵

レーザータレットやCIWS（近接防御火器システム）など、自動索敵が可能な砲塔型乗り物にのみ使用されます。

|  データ名  |   型   |        説明        | デフォルト値 | 例 |
| :------: | :------: | :----------------: | :----: | :--: |
| SeekInfo | SeekInfo | 乗り物の自動索敵データ |  null  | 下記参照 |

SeekInfoは複雑なデータ型で、属性は以下の通りです：

|      データ名      |  型  |                             説明                             | デフォルト値 | 例 |
| :--------------: | :----: | :----------------------------------------------------------: | :----: | :--: |
|   MaxSeekRange   | double |                         最大索敵範囲                         |   64   |  -   |
|   MinSeekRange   | double |                         最小索敵範囲                         |   1    |  -   |
| ChangeTargetTime |  int   | ターゲット切り替え時間。この時間内にターゲットを攻撃できない場合、<br />自動的にターゲットを切り替えて再索敵します |   60   |  -   |
|  SeekIterative   |  int   |                        索敵に必要な時間                        |   20   |  -   |
|  MinTargetSize   | double |                     ロックオン対象の敵の最小体積                     |  0.25  |  -   |
|  SeekEnergyCost  |  int   |                      自動索敵で消費するエネルギー                      |  1000  |  -   |

例えば、レーザータレットの自動索敵データは以下のようになります：

```json
"SeekInfo": {
    "MinSeekRange": 1,
    "MaxSeekRange": 72,
    "ChangeTargetTime": 60,
    "SeekIterative": 30,
    "MinTargetSize": 0.1,
    "SeekEnergyCost": 500
}
```

------

##### 表示効果

|     データ名      |       型       |              説明              |                          デフォルト値                          |                             例                             |
| :-----------: | :--------------: | :----------------------------: | :------------------------------------------------------: | :----------------------------------------------------------: |
|  VehicleIcon  | ResourceLocation | 乗り物のアイコン、右上のキルログに使用 | "superbwarfare:textures/<br />gun_icon/default_icon.png" |                              -                               |
| ContainerIcon | ResourceLocation |        乗り物コンテナのバッジ        |                           null                           | "superbwarfare:textures/<br />gui/vehicle/type/aircraft.png" |
|   HUDColor    |     ModColor     |         乗り物HUDの色          |                        "0x66FF00"                        |                              -                               |

これについては特に言うことはありません。

Modはいくつか乗り物アイコンを提供しています。以下の通りです：

- "superbwarfare:textures/gui/vehicle/type/aircraft.png" 航空機
- "superbwarfare:textures/gui/vehicle/type/civilian.png" 民間車両
- "superbwarfare:textures/gui/vehicle/type/defense.png" 防御タワー
- "superbwarfare:textures/gui/vehicle/type/land.png" 地上車両
- "superbwarfare:textures/gui/vehicle/type/otto.png" 車椅子
- "superbwarfare:textures/gui/vehicle/type/water.png" 水上乗り物



------

##### 乗り物の容量

バージョン0.8.8ではこれは**有効になっていない**ため、何を記入しても無意味です。

|        データ名        |         型         |      説明      |  デフォルト値  | 例 |
| :------------------: | :------------------: | :------------: | :------: | :--: |
| VehicleContainerType | VehicleContainerType |  乗り物の容量サイズ  | "Medium" | 下記参照 |
|   HasUpgradeSlots    |       boolean        | アップグレードスロットを持っているか |  false   |  -   |

VehicleContainerTypeは8種類に分かれ、そのうち5種類のみが開閉可能なメニューを持っています：

- "Empty" 0x0 メニューなし
- "One" 1x1 メニューなし
- "Mini" 1x9 メニューあり
- "Small" 3x9 メニューあり
- "Medium" 6x9 メニューあり
- "Large" 6x13 メニューあり
- "Huge" 6x17 メニューあり（現在はデフォルトでこれです）
- "Special" 3x4 メニューなし（63式の特殊タイプにのみ使用）

アップグレードスロットがある場合、既存のメニューに加えて、乗り物モジュールを装備できる3つのスロットが追加されます。

**この機能はまだ実装されていません。**

------

##### 乗り物のエンジン

バージョン0.8.8では、ついに車速を調整できるようになりました。

|   データ名    |    型    |        説明        |             デフォルト値              | 例 |
| :---------: | :--------: | :----------------: | :-----------------------------: | :--: |
| EngineType  | EngineType |    乗り物のエンジンタイプ    |             "Empty"             | 下記参照 |
| EngineInfo  | JsonObject | 乗り物エンジンの詳細データ |               {}                | 下記参照 |
| EngineSound | SoundEvent |      エンジン音      | "minecraft:intentionally_empty" |  -   |
|  HornSound  | SoundEvent |     クラクション音     | "minecraft:intentionally_empty" |  -   |

EngineTypeは複数のタイプに分かれ、異なる種類のエンジンに対応しています：

- "Empty" デフォルト値、エンジンなし
- "Fixed" 固定エンジン、固定砲などの乗り物に適用
- "Wheel" 車輪エンジン、装輪歩兵戦闘車や一般車両に適用
- "Track" 履帯エンジン、戦車などに適用
- "Helicopter" ヘリコプターエンジン
- "Ship" 船舶エンジン
- "Aircraft" 固定翼機エンジン
- "WheelChair" 車椅子エンジン
- "Tom6" TomF6F特有のエンジン

注意：このフィールドはEngineInfoデータの解析方法に直接影響するため、必ず正しいエンジンタイプを記入してください！

EngineInfoは指定されたEngineTypeに従って、自動的に以下の数種類に解析されます：

**汎用エンジン**データ：

|      データ名       |   型   |             説明              | デフォルト値 | 例 |
| :---------------: | :----: | :---------------------------: | :----: | :--: |
|  EnergyCostRate   | double |         エネルギー消費率          |   1    |  -   |
|     Buoyancy      | double | 浮力。0より大きい場合、乗り物は水陸両用とみなされます |   0    |  -   |
|     Increment     | float  |         前進加速度          | 0.001  |  -   |
|     Decrement     | float  |         後退加速度          | 0.001  |  -   |
| EngineSoundVolume | float  |          エンジンの音量           |  0.4   |  -   |

他のいくつかのエンジンは、汎用データに基づいて独自のデータを追加しています。

**車輪エンジン**データ：（汎用エンジンを継承）

|        データ名        |   型   |       説明       | デフォルト値 | 例 |
| :------------------: | :----: | :--------------: | :----: | :--: |
|    WheelRotSpeed     | double |   車輪回転速度   |   0    |  -   |
|  WheelDifferential   | double |     車輪差動（デファレンシャル）     |   0    |  -   |
|    SteeringSpeed     | float  |     旋回速度     |  0.1   |  -   |
| MaxForwardSpeedRate  | float  | 最大前進速度係数 |  0.2   |  -   |
| MaxBackwardSpeedRate | float  | 最大後退速度係数 |  -0.1  |  -   |

**履帯エンジン**データ：（車輪エンジンを継承）

|      データ名       |   型   |     説明     | デフォルト値 | 例 |
| :---------------: | :----: | :----------: | :----: | :--: |
|   TrackRotSpeed   | double | 履帯回転速度 |   0    |  -   |
| TrackDifferential | double |   履帯差動   |   0    |  -   |

**車椅子エンジン**データ：（車輪エンジンを継承）

|     データ名     |   型    |          説明          | デフォルト値 | 例 |
| :------------: | :-----: | :--------------------: | :----: | :--: |
|  BodyRollRate  | double  |    車体ロール回転率    |   1    |  -   |
|    CanJump     | boolean | ジャンプ可能か（デフォルトはスペースキー） | false  |  -   |
| JumpEnergyCost |   int   |     ジャンプのエネルギー消費     |  400   |  -   |
|  JumpCoolDown  |   int   |     ジャンプのクールダウン時間     |   3    |  -   |
|   JumpForce    | double  |       ジャンプの強さ       |  0.6   |  -   |

本当にこのエンジンを使う人がいるんですか……

**船舶エンジン**データ：（汎用エンジンを継承）

|        データ名        |   型   |       説明       | デフォルト値 | 例 |
| :------------------: | :----: | :--------------: | :----: | :--: |
|    BodyPitchRate     | double | 船体ピッチ回転率 |   1    |  -   |
|     BodyRollRate     | double | 船体ロール回転率 |   1    |  -   |
|    SteeringSpeed     | float  |     旋回速度     |  0.1   |  -   |
| MaxForwardSpeedRate  | float  | 最大前進速度係数 |  0.2   |  -   |
| MaxBackwardSpeedRate | float  | 最大後退速度係数 |  -0.1  |  -   |

**ヘリコプターエンジン**データ：（汎用エンジンを継承）

|      データ名      |     型     |     説明     |             デフォルト値              | 例 |
| :--------------: | :--------: | :----------: | :-----------------------------: | :--: |
|    PitchSpeed    |   float    | ピッチ回転速度 |                1                |  -   |
|     YawSpeed     |   float    | ヨー回転速度 |                1                |  -   |
|    RollSpeed     |   float    | ロール回転速度 |                1                |  -   |
|    LiftSpeed     |   float    |   揚力係数   |                1                |  -   |
| EngineStartSound | SoundEvent | エンジン始動音 | "minecraft:intentionally_empty" |  -   |

**固定翼機エンジン**データ：（ヘリコプターエンジンを継承）

|     データ名      |   型    |      説明      | デフォルト値 | 例 |
| :-------------: | :-----: | :------------: | :----: | :--: |
|    SpeedRate    |  float  |  飛行速度率  |   1    |  -   |
|     HasGear     | boolean | 着陸装置（ランディングギア）を持っているか |  true  |  -   |
| GearRotateAngle |  float  | 着陸装置の回転角度 |   85   |  -   |

乗り物が着陸装置を持っている場合、左下に着陸装置の状態HUDが表示されます。

**Tom6エンジン**データは固定翼機エンジンと同じですが、着陸装置データの影響を受けません。



なお、クラクション機能はすべての乗り物に標準装備されているわけではなく、コードで独自に追加する必要があります。

------

##### 位置と回転

またしても非常に抽象的な部分です。理解できない場合は、本体のソースコードとデータを確認してください。

|                データ名                 |   型   |                       説明                       |   デフォルト値    | 例 |
| :-----------------------------------: | :----: | :----------------------------------------------: | :---------: | :--: |
|         ThirdPersonCameraPos          |  Vec3  | 三人称視点位置。<br />実際のレンダリング時は逆順に配置されます |  [0, 1, 3]  |  -   |
|          RotateOffsetHeight           | float  |                乗り物の回転中心の高さ                |      0      |  -   |
|               TurretPos               |  Vec3  |                   主武器の座標                   |    null     |  -   |
|            TurretTurnSpeed            |  Vec2  |                  主武器の回転速度                  |   [5, 5]    |  -   |
|            TurretYawRange             |  Vec2  |                 主武器のヨー角範囲                 | [-514, 514] |  -   |
|           TurretPitchRange            |  Vec2  |                 主武器のピッチ角範囲                 |  [-10, 30]  |  -   |
|         TurretControllerIndex         |  int   |               主武器操作者の座席番号               |      0      |  -   |
|                HudType                | String |                乗り物武器のHUDタイプ                 |  "@Empty"   |  -   |
|               BarrelPos               |  Vec3  |                  乗り物の砲身位置                  |  [0, 0, 0]  |  -   |
|       PassengerWeaponStationPos       |  Vec3  |                 乗客ウェポンステーションの位置                 |    null     |  -   |
|    PassengerWeaponStationBarrelPos    |  Vec3  |               乗客ウェポンステーションの砲身位置               |  [0, 0, 0]  |  -   |
|    PassengerWeaponStationTurnSpeed    |  Vec2  |                乗客ウェポンステーションの回転速度                |   [5, 5]    |  -   |
|    PassengerWeaponStationYawRange     |  Vec2  |               乗客ウェポンステーションのヨー角範囲               | [-514, 514] |  -   |
|   PassengerWeaponStationPitchRange    |  Vec2  |               乗客ウェポンステーションのピッチ角範囲               |  [-10, 30]  |  -   |
| PassengerWeaponStationControllerIndex |  int   |             乗客ウェポンステーション操作者の座席番号             |      1      |  -   |

これらのデータは、乗り物の主武器、副武器の発射角度や発射位置を調整するために使用されます。具体的な例については、Mod内のデータを参照してください。

------

##### 衝突破壊

|     データ名     |      型      |          説明          | デフォルト値 | 例 |
| :------------: | :------------: | :--------------------: | :----: | :--: |
| CollisionLevel | CollisionLevel | 乗り物の衝突破壊ブロックデータ |   -    | 下記参照 |

CollisionLevelは複雑なデータ型で、属性は以下の通りです：

|   データ名    |     型      |            説明            | デフォルト値 | 例 |
| :---------: | :-----------: | :------------------------: | :----: | :--: |
|    Level    |      int      | 乗り物の衝突破壊ブロックの強度レベル |   2    |  -   |
| PowerLimits | List\<Limit\> | 対応するレベルの破壊効果を有効にするための要件 |   []   | 下記参照 |

乗り物のブロック破壊レベルは5段階に分かれています：

- 0 ブロックを破壊しない
- 1 柔らかいブロックのみ破壊可能。対応する設定項目を有効にする必要あり
- 2 普通のブロックを破壊可能。対応する設定項目を有効にする必要あり
- 3 硬めのブロックを破壊可能。対応する設定項目を有効にする必要あり
- 4 ビーストインパクトモード。一定硬度以下のあらゆるブロックを破壊可能。対応する設定項目を有効にする必要あり

破壊可能かどうかはブロックタグによって制御され、以下の場所にあります：

```
data/superbwarfare/tags/blocks/soft_collision.json
data/superbwarfare/tags/blocks/normal_collision.json
data/superbwarfare/tags/blocks/hard_collision.json
```

許可される破壊レベルはサーバー設定によって制御されますが、コマンドを使用して調整することもできます：

```
/sbw config collisionDestroy none|soft|normal|hard|beastly
```

Limitの書き方はVec3と似ていますが、属性は以下の通りです：

```json
[出力要件, 運動量要件, 等号を含むか]
```

YX-100主力戦車の破壊効果を例にします：

```json
"CollisionLevel": {
    "Level": 4,
    "PowerLimits": [
      [0, 0, true],
      [0, 0, true],
      [0.1, 0.05, false],
      [1, 0.3, false]
    ]
}
```

つまり、この乗り物の破壊レベルは4であり、最初の2レベルはどのような状況でもトリガーされます。衝突レベル要件が3のブロックの場合、出力が0.1より大きいか、運動量が0.05より大きい場合にのみ破壊できます。レベル4も同様です。

------

##### 地形適応

|         データ名          |     型     |            説明             | デフォルト値 | 例 |
| :---------------------: | :----------: | :-------------------------: | :----: | :--: |
|      TerrainCompat      | List\<Vec3\> | 地面の高さを検出するためのN個の点座標 |  null  | 下記参照 |
| TerrainCompatRotateRate |    float     |   乗り物の地形適応回転幅    |   1    |  -   |
|    InertiaRotateRate    |    float     |  乗り物が慣性の影響を受ける回転幅   |   0    |  -   |

地形適応とは、乗り物の中心を原点とするいくつかの座標点を選択し、最終的に平均回転角度を算出することです。

例は以下の通りです：

```json
"TerrainCompat": [
    [1.1875, 0, 3.1875],
    [-1.1875, 0, 3.1875],
    [1.1875, 0, -1.0625],
    [-1.1875, 0, -1.0625]
]
```

もちろん、座標をいくつ書いても構いません。

------

##### その他のデータ

|           データ名            |    型     |                         説明                         | デフォルト値  | 例 |
| :-------------------------: | :---------: | :--------------------------------------------------: | :-----: | :--: |
| UsePassengerCreativeAmmoBox |   boolean   | 乗り物の武器が弾薬を読み込む際、乗客が持っているクリエイティブモード弾薬箱を読み込むかどうか |  true   |  -   |
|     HasLowHealthWarning     |   boolean   |             乗り物が低HP時に警告を発するかどうか             |  true   |  -   |
|            Type             | VehicleType |                      乗り物のタイプ                      | "Empty" | 下記参照 |

VehicleTypeは列挙型で、以下の種類に分かれます：

- "Empty" 空タイプ、デフォルト
- "Tank" 戦車
- "APC" 装甲運搬車 (APC)
- "AA" 対空車両 (AA)
- "Airplane" 固定翼機
- "Helicopter" ヘリコプター
- "Car" 一般車両
- "Artillery" 火砲
- "Defense" 防御タワー
- "Boat" 船
- "Drone" ドローン
- "Special" 特殊乗り物



### （四）リソースファイル

（うめき声/叫び声）
