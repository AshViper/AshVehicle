------

##### 発射位置

複雑なデータ型に属し、現在は乗り物の武器にのみ使用され、弾薬発射時の位置を設定するために使用されます。

|  データ名  |    型    |      説明      | デフォルト値 | 例 |
| :--------: | :------: | :------------: | :----: | :--: |
|  ShootPos  | ShootPos | 弾丸発射位置 |   -    | 下記参照 |

ShootPosの属性は以下の通りです：

|        データ名         |         型         |                    説明                    |   デフォルト値    | 例 |
| :---------------------: | :------------------: | :----------------------------------------: | :---------: | :--: |
|        Transform        |        String        |               使用する変換行列               |  "Default"  |  -   |
|        Positions        |     List\<Vec3\>     |           弾丸発射位置（複数）           | [[0, 0, 0]] |  -   |
|       Directions        | List\<StringOrVec3\> |           弾丸発射方向（複数）           | ["Default"] |  -   |
|   ShootPositionForHud   |         Vec3         |             一人称視点の発射位置             |    null     |  -   |
|  ShootDirectionForHud   |     StringOrVec3     |             一人称視点の発射方向             |    null     |  -   |
|  BoundUpWithAmmoAmount  |       boolean        | 弾丸を発射するたびに発射位置と方向をローテーションするかどうか |    false    |  -   |
|      ViewPosition       |         Vec3         |                  観察位置                  |    null     |  -   |
|      ViewDirection      |     StringOrVec3     |                  観察方向                  |    null     |  -   |

変換行列とStringOrVec3にはいくつかのプリセット値が存在します。ここでは以下のメソッドを参照してください：

```java
com/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity.java
```

```java
protected void registerTransforms() {
        positionTransform.put("VehicleFlat", this::getVehicleFlatTransform);
        positionTransform.put("Turret", this::getTurretTransform);
        positionTransform.put("Barrel", this::getBarrelTransform);
        positionTransform.put("WeaponStation", this::getGunTransform);
        positionTransform.put("WeaponStationBarrel", this::getPassengerWeaponStationBarrelTransform);
        positionTransform.put("Default", this::getVehicleTransform);

        vectorTransform.put("Turret", this::getTurretVector);
        vectorTransform.put("Barrel", this::getBarrelVector);
        vectorTransform.put("WeaponStationBarrel", this::getPassengerWeaponStationVector);
        vectorTransform.put("DeltaMovement", tick -> getDeltaMovement().normalize());
        vectorTransform.put("Up", this::getUpVec);
        vectorTransform.put("Default", this::getViewVector);

        rotationTransform.put("Turret", tick -> VectorTool.combineRotationsTurret(tick, this));
        rotationTransform.put("Barrel", tick -> VectorTool.combineRotationsBarrel(tick, this));
        rotationTransform.put("RotationsYaw", tick -> VectorTool.combineRotationsYaw(tick, this));
        rotationTransform.put("Default", tick -> VectorTool.combineRotations(tick, this));
}
```

------

##### その他のデータ

ここは分類しにくいデータです。将来的に削除されるかもしれません。

|            データ名            |   型    |                             説明                             |  デフォルト値   | 例 |
| :----------------------------: | :-----: | :----------------------------------------------------------: | :-------: | :--: |
|            GunType             | GunType |                       武器タイプ、列挙型                       | "Special" |  -   |
|   WithdrawAmmoWhenChangeSlot   | boolean |                    武器切り替え時に弾薬を排出するかどうか                    |   false   |  -   |
|  ClearHoldProgressAfterShoot   | boolean |                 発射後にチャージ進行度をクリアするかどうか                 |   false   |  -   |
|          SoundRadius           | double  |                      武器効果音の伝播半径                      |     0     |  -   |
|       ShootAnimationTime       |   int   |                  乗り物武器に使用される発射アニメーション時間                  |     0     |  -   |
|      IsAntiAirProjectile       | boolean |                    発射物が対空弾であるか                    |   false   |  -   |
|  IsClusterMunitionsProjectile  | boolean |                      発射物がクラスター弾であるか                      |   false   |  -   |
|          SpreadAmount          |   int   |             発射物がクラスター弾の場合、その分裂数             |    10     |  -   |
|          SpreadAngle           |   int   |             発射物がクラスター弾の場合、その分裂角度             |    15     |  -   |
|   IsArmorPiercingProjectile    | boolean |                      発射物が徹甲弾であるか                      |   false   |  -   |
|   IsHighExplosiveProjectile    | boolean |                      発射物が高爆発弾（HE）であるか                      |   false   |  -   |
|     IsGrapeShotProjectile      | boolean |                      発射物がグレープショットであるか                      |   false   |  -   |
|    AddShooterDeltaMovement     | boolean | 発射物の運動量に発射者の運動量を上乗せするかどうか。trueに設定した場合、<br />Velocity属性は運動量上乗せの倍率になります |   false   |  -   |

GunTypeには以下の種類があります：

- "Rifle" ライフル
- "Shotgun" ショットガン
- "Sniper" スナイパーライフル
- "MachineGun" マシンガン
- "Handgun" ハンドガン
- "Smg" サブマシンガン
- "DirectLauncher" 直射ランチャー（ロケットランチャーなど）
- "CurvedLauncher" 曲射ランチャー（グレネードなど）
- "Special" 特殊武器

なお、IsClusterMunitionsProjectileという属性は近いうちに削除される予定です。



------

#### 乗り物データ

すべてのフィールドは以下のクラスにあります：

```java
com/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData.java
```



##### 基礎データ

|      データ名      |   型    |                             説明                             |     デフォルト値     |          例          |
| :----------------: | :-----: | :----------------------------------------------------------: | :------------: | :------------------: |
|         ID         | String  | このファイルで変更する乗り物を指定します。空欄または未記入の場合、デフォルトでファイル名を乗り物IDとして使用しようとします |       ""       | "superbwarfare:ah_6" |
|     MaxHealth      |  float  |                       乗り物の最大耐久値                       |       50       |         810          |
|   RepairCooldown   |   int   |                乗り物の自然回復開始までのクールダウン時間                | 設定ファイルと同じ |         114          |
|    RepairAmount    |  float  |                      乗り物の自然回復量                      | 設定ファイルと同じ |         5.14         |
|  SelfHurtPercent   |  float  |            乗り物のHPがこの割合を下回った時、自動でダメージを受け始めます            |      0.1       |          -           |
|   SelfHurtAmount   |  float  |             乗り物が自動ダメージを受け始めた時、毎tick減少するHP量             |      0.1       |          -           |
|     MaxEnergy      |   int   |                        乗り物の最大エネルギー                        |   2147483647   |          0           |
|       UpStep       |  float  |                        乗り物の登坂高度                        |       0        |         1.5          |
|        Mass        |  float  |                     乗り物の重量、単位はトン                     |       1        |          -           |
|      Gravity       | double  |                       乗り物の重力加速度                       |      0.06      |          -           |
|    AllowFreeCam    | boolean | 乗り物に乗っている時にフリーカメラキー（デフォルトC）を長押ししてフリーカメラを有効にできるかどうか。<br />飛行乗り物にのみ有効 |     false      |         true         |
|      HasDecoy      | boolean |     デコイを持っているか。飛行乗り物はフレア、地上乗り物はスモークを使用します     |     false      |         true         |
|  SendHitParticles  | boolean |                    被弾時にパーティクル効果を表示するか                    |      true      |          -           |

------

##### 衝突判定 (Hitbox)

| データ名 |      型       |        説明        | デフォルト値 | 例 |
| :------: | :-------------: | :----------------: | :----: | :--: |
|   OBB    | List\<OBBInfo\> | 乗り物の詳細な衝突判定 |   []   | 下記参照 |

OBBInfoの属性は以下の通りです：

|  データ名   |    型    |                           説明                            |  デフォルト値   |    例     |
| :---------: | :------: | :-------------------------------------------------------: | :-------: | :-------: |
|    Size     |   Vec3   |                  OBB衝突ボックスの3軸の半分の長さ                  | [0, 0, 0] | [1, 2, 3] |
|  Position   |   Vec3   |                   OBB衝突ボックスの中心点位置                   | [0, 0, 0] | [1, 2, 3] |
|  Transform  |  String  |                    OBB衝突ボックスの回転行列                    | "Default" |     -     |
|  Rotation   |  String  | OBB衝突ボックスが回転に追従するパーツ。現在は<br />"Turret"と"Barrel"のみ | "Default" |     -     |
|    Part     | OBB.Part |                    OBB衝突ボックスのパーツタイプ                    |  "Body"   |     -     |

ここの回転行列タイプは前述の**発射位置**を参照してください。

OBB.Part列挙型は以下の種類に分かれます：

- "Empty" 何もなし
- "WheelLeft" 左車輪
- "WheelRight" 右車輪
- "Turret" 砲塔
- "MainEngine" メインエンジン
- "SubEngine" サブエンジン
- "Body" 本体部分
- "Interactive" インタラクティブパーツ（レンダリング時にオレンジ色になります。例：63式の砲身）

この部分はあまりにも謎めいているため、本体のコードとデータパックの書き方を参照することをお勧めします。

------

##### 座席情報

| データ名 |           型           |      説明      | デフォルト値 | 例 |
| :------: | :----------------------: | :------------: | :----: | :--: |
|  Seats   | ObjectToList\<SeatInfo\> | 乗り物の座席情報 |   []   | 下記参照 |

SeatInfoは複雑なデータ型で、属性は以下の通りです：

|     データ名      |          型          |                             説明                             |  デフォルト値   | 例 |
| :---------------: | :--------------------: | :----------------------------------------------------------: | :-------: | :--: |
|   HidePassenger   |        boolean         |                     この座席の乗客を非表示にするかどうか                     |   false   |  -   |
|    IsEnclosed     |        Boolean         |     この座席が密閉座席かどうか（密閉座席は乗客が受けるダメージを吸収します）     |   null    | true |
|     Transform     |         String         |                乗り物に対するこの座席の乗客の回転行列                | "Default" |  -   |
|     Position      |          Vec3          |                回転行列の中心点に対する乗客の位置                | [0, 0, 0] |  -   |
|    Orientation    |         float          |                回転行列の正方向に対する乗客の角度                |     0     |  -   |
|   CanRotateBody   |        Boolean         |                      乗客が体を回転できるかどうか                      |   false   |  -   |
|   CanRotateHead   |        Boolean         |                      乗客が頭を回転できるかどうか                      |   true    |  -   |
|     MinPitch      |         float          |                          最小ピッチ角                          |    -90    |  -   |
|     MaxPitch      |         float          |                          最大ピッチ角                          |    90     |  -   |
|      MinYaw       |         float          |                       最小ヨー角                       |   -514    |  -   |
|      MaxYaw       |         float          |                       最大ヨー角                       |    514    |  -   |
|      Weapons      | ObjectToList\<String\> |                       この座席の武器情報                       |    []     | 下記参照 |
|     CameraPos     |       CameraPos        |                        乗客のカメラ位置                        |   null    | 下記参照 |
|      BanHand      |        Boolean         |                   乗客の手持ちアイテム使用を禁止するかどうか                   |   false   |  -   |
|    Sensitivity    |          Vec3          | 乗り物に乗っている時のプレイヤーのマウス感度。<br />[照準感度, 一人称腰だめ撃ち感度, 三人称腰だめ撃ち感度] | [1, 1, 1] |  -   |
|   DismountInfo    |      DismountInfo      |                     乗客降車時の各種データ                     |   null    | 下記参照 |

ここでWeapons属性はこの座席にどの武器が割り当てられているかを示しています。具体的な書き方は後述の**乗り物の武器**部分を参照してください。

CameraPosも複雑なデータ型で、属性は以下の通りです：

|      データ名       |     型     |                             説明                             |   デフォルト値    | 例 |
| :-----------------: | :----------: | :----------------------------------------------------------: | :---------: | :--: |
|      Transform      |    String    |                       カメラの回転行列                       |  "Default"  |  -   |
|      Position       |     Vec3     |               回転行列の中心点に対するカメラの位置               |  [0, 0, 0]  |  -   |
|      Direction      | StringOrVec3 |                         カメラの方向                         |  "Default"  |  -   |
|    ZoomPosition     |     Vec3     |                      照準時のカメラ位置                      |    null     |  -   |
|    ZoomDirection    | StringOrVec3 |                      照準時のカメラ方向                      |    null     |  -   |
|  UseFixedCameraPos  |   boolean    |                    固定カメラ位置を使用するかどうか                    |    false    |  -   |
|    UseSimulate3P    |   boolean    | 一人称視点で三人称視点をシミュレートすることを許可するかどうか。<br />例：内装のない乗り物（Lav-150）の乗員位置視点 |    false    |  -   |
|    Simulate3PPos    |     Vec2     |                    三人称視点シミュレーションの位置                    |   [6, 1]    |  -   |
|  UseAircraftCamera  |   boolean    |                  飛行乗り物カメラモードを使用するかどうか                  |    false    |  -   |
|  AircraftCameraPos  |     Vec3     |                      飛行乗り物カメラ位置                      | [0, 3, -10] |  -   |

DismountInfoの属性は以下の通りです：

|     データ名     |     型     |                  説明                  |  デフォルト値   | 例 |
| :--------------: | :----------: | :------------------------------------: | :-------: | :--: |
|    Transform     |    String    |      プレイヤー降車位置に適用される回転行列      | "Default" |  -   |
|     Position     |     Vec3     | 回転行列の中心点に対するプレイヤー降車位置 | [0, 0, 0] |  -   |
|     CanEject     |   boolean    |            乗客の射出を許可するかどうか            | "Default" |  -   |
|  EjectPosition   |     Vec3     |                射出位置                |   null    |  -   |
|  EjectDirection  | StringOrVec3 |               射出方向               |   "Up"    |  -   |
|    EjectForce    |    double    |               射出の強さ               |     2     |  -   |

では、5人乗り乗り物のSeatsデータは以下のようになります：

```json
"Seats": [
    {
      "HidePassenger": true,
      "CameraPos": {
        "Transform": "Barrel",
        "ZoomPosition": [0.1, 0.0517, 0],
        "Direction": "Barrel"
      },
      "Weapons": ["Cannon", "MachineGun"],
      "Transform": "Turret",
      "Position": [0.36, -0.65, 0.56],
      "CanRotateHead": false,
      "MinPitch": -15,
      "MaxPitch": 32.5,
      "Sensitivity": [0.55, 0.73, 1]
    },
    {
      "HidePassenger": true,
      "BanHand": true,
      "Transform": "Vehicle",
      "Position": [0, 1, 0],
      "CameraPos": {
        "UseSimulate3P": true,
        "Simulate3PPos": [6, 1]
      }
    },
    {
      "HidePassenger": true,
      "BanHand": true,
      "Transform": "Vehicle",
      "Position": [0, 1, 0],
      "CameraPos": {
        "UseSimulate3P": true,
        "Simulate3PPos": [6, 1]
      }
    },
    {
      "HidePassenger": true,
      "BanHand": true,
      "Transform": "Vehicle",
      "Position": [0, 1, 0],
      "CameraPos": {
        "UseSimulate3P": true,
        "Simulate3PPos": [6, 1]
      }
    },
    {
      "HidePassenger": true,
      "BanHand": true,
      "Transform": "Vehicle",
      "Position": [0, 1, 0],
      "CameraPos": {
        "UseSimulate3P": true,
        "Simulate3PPos": [6, 1]
      }
    }
]
```

これもMod内のLav-150歩兵戦闘車の座席情報です。5つの位置すべてで乗客が非表示になり、手持ちアイテムの使用が阻止されていることがわかります。そのうち1番席のみ武器が割り当てられているため、武器に関連するカメラ位置、感度などのデータを追加で設定する必要があります。他の4つの位置は乗客席であり、一人称視点と三人称視点での表現形式は同じで、UseSimulate3Pを使用して三人称視点をシミュレートしています。

以下はMod内のA-10A戦闘機の座席情報です。この乗り物は座席が1つだけで、乗客の射出が許可されています：

```json
"Seats": {
    "IsEnclosed": true,
    "Transform": "Vehicle",
    "Position": [0, 2.125, 3.7],
    "CanRotateHead": false,
    "Sensitivity": [0, 0, 0],
    "CameraPos": {
      "Transform": "Vehicle",
      "Position": [0, 3.725, 3.7],
      "UseFixedCameraPos": true,
      "UseAircraftCamera": true,
      "AircraftCameraPos": [0, 4, -14]
    },
    "DismountInfo": {
      "Transform": "Vehicle",
      "Position": [1.6, 0.5, 3.8],
      "CanEject": true,
      "EjectPosition": [0, 4.2, 3.7],
      "EjectDirection": "Up",
      "EjectForce": 4
    },
    "Weapons": ["Cannon", "Rocket", "Bomb", "Missile"]
}
```

読者はMod本体の他の乗り物の書き方を自分で調べることができます。

------

##### 乗り物の武器

バージョン0.8.8から、乗り物の武器は前述の銃器と同じシステムを共有しています。作者はこれが非常に良いことであり、悪い点は一つもないと考えています。

| データ名 |          型           |        説明        | デフォルト値 | 例 |
| :------: | :---------------------: | :----------------: | :----: | :--: |
| Weapons  | Map<String, JsonObject> | 乗り物のすべての武器情報 |   {}   | 下記参照 |

WeaponsはMap型のデータで、形式は以下の通りです：

```json
"武器名": {
    // ここの内容はGunDataの書き方と同じです
}
```

ここのデータについては特に説明することはないので、直接例を挙げて説明します。

比較的単純な武器から始めましょう。例えばLav-150歩兵戦闘車です：

```json
"Weapons": {
    "Cannon": {
      "Icon": "superbwarfare:textures/overlay/vehicle/weapon/icons/cannon_20mm.png",
      "DefaultFireMode": "Auto",
      "AvailableFireModes": "Auto",
      "Projectile": "superbwarfare:small_cannon_shell",
      "DefaultZoom": 3,
      "RPM": 300,
      "Velocity": 20,
      "Damage": 45,
      "ExplosionDamage": 12,
      "ExplosionRadius": 4,
      "RecoilTime": 29,
      "RecoilForce": 0.55,
      "Spread": 0.5,
      "HeatPerShoot": 7,
      "NaturalCooldown": 1,
      "ShootAnimationTime": 3,
      "Crosshair": "@VehicleUsApc",
      "CrosshairColor": "0x66FF00",
      "Name": "weapon.superbwarfare.20mm_cannon",
      "SoundInfo": {
        "Fire1P": "superbwarfare:lav_150_cannon_fire_1p",
        "Fire3P": "superbwarfare:lav_150_cannon_fire_3p",
        "Fire3PFar": "superbwarfare:lav_150_cannon_far",
        "Fire3PVeryFar": "superbwarfare:lav_150_cannon_veryfar",
        "Change": "superbwarfare:into_missile"
      },
      "SoundRadius": 24,
      "AmmoType": "superbwarfare:small_shell",
      "ShootPos": {
        "Transform": "Barrel",
        "Positions": [[0.02, 0.0517, 3.2]],
        "Directions": ["Barrel"]
      }
    },
    "MachineGun": {
      "Icon": "superbwarfare:textures/overlay/vehicle/weapon/icons/gun_7_62mm.png",
      "DefaultFireMode": "Auto",
      "AvailableFireModes": "Auto",
      "Spread": 0.5,
      "CrosshairColor": "0x66FF00",
      "Projectile": "superbwarfare:projectile",
      "RPM": 600,
      "Damage": 9.5,
      "BypassesArmor": 0.3,
      "Velocity": 30,
      "RecoilTime": 22,
      "RecoilForce": 0.1,
      "HeatPerShoot": 4,
      "NaturalCooldown": 1,
      "ShootAnimationTime": 2,
      "AmmoType": "@RifleAmmo",
      "ShootPos": {
        "Transform": "Barrel",
        "Positions": [[0.3, 0.0517, 0.5]],
        "Directions": ["Barrel"]
      },
      "Crosshair": "@VehicleCommonGun",
      "Name": "weapon.superbwarfare.7_62mm_coax",
      "SoundInfo": {
        "Fire1P": "superbwarfare:coax_fire_1p",
        "Fire3P": "superbwarfare:m_60_fire_3p",
        "Fire3PFar": "superbwarfare:m_60_far",
        "Fire3PVeryFar": "superbwarfare:m_60_veryfar",
        "Change": "superbwarfare:into_cannon"
      },
      "SoundRadius": 12
    }
}
```

ここではCannonとMachineGunという名前の2種類の武器が定義されていることがわかります。後ろのデータも武器データの書き方です。では、先ほどの座席情報のところに戻って、この乗り物の1番席の書き方を見てみましょう：

```json
"Seats": [
    {
      // ...
      "Weapons": ["Cannon", "MachineGun"],
      // ...
    },
    // ...
]
```

ここでSeatsのWeaponsに渡されたデータが、まさに**一番外側のWeapons**で定義された武器名であることが容易にわかります。これにより、1番席にこれら2つの武器をバインドする効果が実現されます。

次はもう少し複雑なものです。前の部分で触れたShootPos内のPositionsとBoundUpWithAmmoAmountを覚えていますか？Mod内のYX-100の3番席武器「スウォームドローン」は、このようにして発射位置を切り替えています。例は以下の通りです：

```json
"Weapons": {
     "SwarmDrone": {
      "Icon": "superbwarfare:textures/overlay/vehicle/weapon/icons/swarm_drone.png",
      "DefaultFireMode": "Auto",
      "AvailableFireModes": ["Auto"],
      "Spread": 0.3,
      "Magazine": 14,
      "EmptyReloadTime": 140,
      "DefaultZoom": 2,
      "CrosshairColor": "0x00ff66",
      "Projectile": "superbwarfare:swarm_drone",
      "RPM": 600,
      "ExplosionDamage": 150,
      "ExplosionRadius": 5,
      "Velocity": 2,
      "ShootPos": {
        "Transform": "Turret",
        // ここでは複数の発射位置を規定しており、発射するたびに下にループします
        "Positions": [
          [-1.6290875, 0.75536875, -1.76616875],
          [1.6290875, 0.75536875, -1.76616875],
          [-1.7822125, 0.75536875, -1.9193],
          [1.7822125, 0.75536875, -1.9193],
          [-1.6290875, 0.75536875, -2.07241875],
          [1.6290875, 0.75536875, -2.07241875],
          [-1.7822125, 0.75536875, -2.22555],
          [1.7822125, 0.75536875, -2.22555],
          [-1.6290875, 0.75536875, -2.37866875],
          [1.6290875, 0.75536875, -2.37866875],
          [-1.7822125, 0.75536875, -2.5318],
          [1.7822125, 0.75536875, -2.5318],
          [-1.6290875, 0.75536875, -2.68491875],
          [1.6290875, 0.75536875, -2.68491875]
        ],
        "BoundUpWithAmmoAmount": true,
        "Directions": [[0, 1, 0]],
        "ShootPositionForHud": [0.86219375, 1.67, -0.5696875],
        "ViewDirection": "Passenger"
      },
      "SeekWeaponInfo": {
        "SeekDirection": "Passenger",
        "SeekRange": 384,
        "SeekAngle": 20,
        "SeekTime": 10,
        "OnlyLockEntity": true
      },
      // 複数の弾薬タイプと属性の上書きはここでも有効です
      "AmmoType": [
        {
          "Ammo": "superbwarfare:swarm_drone",
          "ShouldUnload": false
        },
        {
          "Ammo": "superbwarfare:swarm_drone",
          "ShouldUnload": false,
          "Override": {
            "SeekWeaponInfo": {
              "SeekDirection": "Passenger",
              "SeekRange": 384,
              "SeekAngle": 20,
              "SeekTime": 10,
              "OnlyLockBlock": true
            }
          }
        }
      ],
      "Crosshair": "@VehicleCommonMissile",
      "CrosshairZooming": "@VehicleCommonSeekMissile",
      "Name": "weapon.superbwarfare.swarm_drone",
      "SoundInfo": {
        "Fire1P": "superbwarfare:yx_100_swarm_drone_release",
        "Fire3P": "superbwarfare:yx_100_swarm_drone_release",
        "VehicleReload3p": "superbwarfare:missile_reload",
        "VehicleReloadSoundTime": 7
      },
      "SoundRadius": 4
    }
}
```

最後は乗り物武器特有の書き方、テンプレート（Template）です。例えば、乗り物に多くの種類の似たような武器を定義する必要がある場合、各武器に必要なデータをすべて書き直すのは面倒です。そこで、武器データテンプレートを定義できます。Mod内のMi-28攻撃ヘリコプターのデータを例にします：

```json
"Weapons": {
    // これがテンプレートです
    "@Missile": {
      "Icon": "superbwarfare:textures/overlay/vehicle/weapon/icons/missile_9m120.png",
      "DefaultFireMode": "Semi",
      "AvailableFireModes": "Semi",
      "RPM": 12,
      "EmptyReloadTime": 240,
      "AmmoType": "superbwarfare:medium_anti_ground_missile",
      "Projectile": "superbwarfare:wire_guide_missile",
      "Name": "weapon.superbwarfare.9m_120_missile",
      "Damage": 650,
      "ExplosionDamage": 80,
      "ExplosionRadius": 7,
      "Velocity": 3,
      "CrosshairColor": "0xFFC700",
      "SoundInfo": {
        "Fire1P": "superbwarfare:bomb_release",
        "Fire3P": "superbwarfare:bomb_release",
        "VehicleReload3p": "superbwarfare:bomb_reload",
        "VehicleReloadSoundTime": 11,
        "Change": "superbwarfare:into_missile"
      },
      "SoundRadius": 8
    },
    "DriverMissile": {
      "DefaultZoom": 2,
      "Magazine": 4,
      "Crosshair": "@AirCraftMissile",
      // ここでテンプレートを直接適用します
      "Template": "@Missile",
      "ShootPos": {
        "Transform": "Vehicle",
        "ShootPositionForHud": [0, 3.5625, 2.25],
        "Positions": [[-2.258, 1.932, 0], [-2.258, 1.67, 0], [-2.446, 1.932, 0], [-2.446, 1.67, 0]],
        "BoundUpWithAmmoAmount": true
      }
    },
    "PassengerMissile": {
      "DefaultZoom": 3,
      "Magazine": 8,
      "Template": "@Missile",
      "Crosshair": "@VehicleCommonMissile",
      "ShootPos": {
        "Transform": "Vehicle",
        "ShootPositionForHud": [0, 1.0625, 5.625],
        "Positions": [[2.258, 1.932, 0], [2.258, 1.67, 0], [2.446, 1.932, 0], [2.446, 1.67, 0], [2.806, 1.932, 0], [2.806, 1.67, 0], [2.993, 1.932, 0], [2.993, 1.67, 0]],
        "BoundUpWithAmmoAmount": true,
        "ViewDirection": "Barrel"
      }
    }
}
```

武器テンプレートの名前に厳格な要件はありませんが、Superb Warfareの命名規則に従い、テンプレート武器と通常の武器を区別しやすくするために、「@+名前」と命名することをお勧めします。

テンプレートを使用した武器データはテンプレートの属性を継承するため、通常の武器に必要な属性を補足するだけで済みます。
