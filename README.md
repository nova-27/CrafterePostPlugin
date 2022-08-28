[![download](https://img.shields.io/github/downloads/nova-27/MCWebPlugin/total?color=blue)](https://github.com/nova-27/MCWebPlugin/releases)
[![license](https://img.shields.io/github/license/nova-27/MCWebPlugin?color=b8b8b8)](https://github.com/nova-27/MCWebPlugin/blob/main/LICENSE)

# MCWebPlugin
このプラグインは、[MCWeb](https://mcviewer.netlify.app/)公式のSpigotプラグインです。  
選択した範囲をSchematic([Sponge Schematic Specification Version 2](https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-2.md))建築形式やMCSR(Minecraft Schematic based Recording)録画形式のファイルに出力します。  
出力したデータを[MCWeb](https://mcviewer.netlify.app/)に投稿し、利用することができます。  

## 導入
現時点では**Spigot 1.18.x**でのみ動作確認済みです。

1. [Releases](https://github.com/nova-27/MCWebPlugin/releases)から最新のプラグインをダウンロードします。
2. 依存プラグインである[WorldEdit](https://dev.bukkit.org/projects/worldedit/files)と[ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)をダウンロードします。
3. ダウンロードした3つのプラグインを、Spigotサーバーの`plugins`フォルダに移動します。

## コマンド一覧
- `/mcweb schem`
  - WorldEditのwandで選択した範囲をSchematic形式で出力します。
  - 出力ファイル: `plugins/MCWeb/test.schem`
- `/mcweb record`
  - WorldEditのwandで選択した範囲をMCSR形式で録画開始・停止します。
  - 出力ファイル: `plugins/MCWeb/test.mcsr`

## ビルド
1. ソースコードをダウンロードします。
```shell
git clone https://github.com/nova-27/MCWebPlugin.git
```
2. ビルドします。
```shell
cd MCWebPlugin
./gradlew shadowJar
```
3. `build/libs`ディレクトリにプラグインjarが生成されます。

## ライセンス
当リポジトリは [CC0 1.0 Universal](https://creativecommons.org/publicdomain/zero/1.0/deed) のもとで公開されています。
