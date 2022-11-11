[![download](https://img.shields.io/github/downloads/nova-27/CrafterePostPlugin/total?color=blue)](https://github.com/nova-27/CrafterePostPlugin/releases)
[![license](https://img.shields.io/github/license/nova-27/CrafterePostPlugin?color=b8b8b8)](https://github.com/nova-27/CrafterePostPlugin/blob/main/LICENSE)
[![Discord](https://img.shields.io/discord/998165329148661781)](https://discord.gg/cps9Rd72ET)

# CrafterePostPlugin

このプラグインは、[CrafterePost](https://crafterepost.netlify.app/)公式のSpigotプラグインです。

選択した範囲をSchematic([Sponge Schematic Specification Version 2](https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md))建築形式やMCSR(Minecraft Schematic based Recording)録画形式のファイルに出力します。

出力したデータを[CrafterePost](https://crafterepost.netlify.app/)に投稿できます。

## 導入方法

[Wiki](../../wiki/導入方法)をご覧ください。

## コマンド一覧

[Wiki](../../wiki/使い方#コマンド一覧)をご覧ください。

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
