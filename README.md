[![download](https://img.shields.io/github/downloads/nova-27/CrafterePostPlugin/total?color=blue)](https://github.com/nova-27/CrafterePostPlugin/releases)
[![license](https://img.shields.io/github/license/nova-27/CrafterePostPlugin?color=b8b8b8)](https://github.com/nova-27/CrafterePostPlugin/blob/main/LICENSE)

# CrafterePostPlugin

このプラグインは、[CrafterePost](https://crafterepost.netlify.app/)公式のSpigotプラグインです。

選択した範囲をSchematic([Sponge Schematic Specification Version 2](https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md))
建築形式やMCSR(Minecraft Schematic based Recording)録画形式のファイルに出力します。

出力したデータを[CrafterePost](https://crafterepost.netlify.app/)に投稿できます。

## 最低要件

- Spigot 1.13以上
- Java 11以上

## 導入

1. [Releases](https://github.com/nova-27/CrafterePostPlugin/releases)から最新のプラグインをダウンロードします。
2. 依存プラグインである[WorldEdit](https://dev.bukkit.org/projects/worldedit/files)
   と[ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)をダウンロードします。
3. ダウンロードした3つのプラグインを、Spigotサーバーの`plugins`フォルダに移動します。

## コマンド一覧

- `/crapos schem ファイル名`
    - WorldEditのwandで選択した範囲をSchematic建築形式で出力します。
    - 出力ファイル: `plugins/CrafterePost/ファイル名.schem`
- `/crapos record start ファイル名`
    - WorldEditのwandで選択した範囲をMCSR録画形式で録画開始します。
    - 出力ファイル: `plugins/CrafterePost/ファイル名.mcsr`
- `/crapos record stop`
    - MCSRの録画を停止します。
    - 録画中にプレイヤーがサーバーを退出した場合、その時点で自動的に録画を停止します。

## ビルド

1. ソースコードをダウンロードします。

```shell
git clone https://github.com/nova-27/CrafterePostPlugin.git
```

2. ビルドします。

```shell
cd CrafterePostPlugin
./gradlew shadowJar
```

3. `build/libs`ディレクトリにプラグインjarが生成されます。

## ライセンス

当リポジトリは [CC0 1.0 Universal](https://creativecommons.org/publicdomain/zero/1.0/deed) のもとで公開されています。
