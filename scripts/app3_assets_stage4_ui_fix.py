from pathlib import Path

path = Path(__file__).with_name("app3_assets_stage4_ui.py")
text = path.read_text(encoding="utf-8")
old = '''# AssetsScreen signature and detail call.
if "bomHeaders: List<AssetBomHeaderEntity>" not in text[text.find("private fun AssetsScreen"):text.find("private fun AssetCard")]:
    text = replace_once(text, "    characteristics: List<AssetCharacteristicEntity>,\\n    bomItems: List<AssetBomItemEntity>,", "    characteristics: List<AssetCharacteristicEntity>,\\n    bomHeaders: List<AssetBomHeaderEntity>,\\n    bomItems: List<AssetBomItemEntity>,", "assets screen header parameter")
    text = replace_once(text, "    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,\\n    onSaveBom: (AssetBomItemEntity) -> Unit,", "    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,\\n    onSaveBomHeader: (AssetBomHeaderEntity) -> Unit,\\n    onDeleteBomHeader: (AssetBomHeaderEntity) -> Unit,\\n    onSaveBom: (AssetBomItemEntity) -> Unit,", "assets screen header callbacks")
    text = replace_once(text, "            characteristics = characteristics.filter { it.assetId == detailAsset.id },\\n            bomItems = bomItems.filter { it.assetId == detailAsset.id },", "            characteristics = characteristics,\\n            bomHeaders = bomHeaders,\\n            bomItems = bomItems,", "asset detail header data")
    text = replace_once(text, "            onDeleteCharacteristic = onDeleteCharacteristic,\\n            onSaveBom = onSaveBom,", "            onDeleteCharacteristic = onDeleteCharacteristic,\\n            onSaveBomHeader = onSaveBomHeader,\\n            onDeleteBomHeader = onDeleteBomHeader,\\n            onSaveBom = onSaveBom,", "asset detail header callbacks")
'''
new = '''# AssetsScreen signature and detail call.
assets_start = text.find("private fun AssetsScreen(")
assets_end = text.find("private fun AssetCard", assets_start)
assets_segment = text[assets_start:assets_end]
if "bomHeaders: List<AssetBomHeaderEntity>" not in assets_segment:
    assets_segment = assets_segment.replace(
        "    characteristics: List<AssetCharacteristicEntity>,\\n    bomItems: List<AssetBomItemEntity>,",
        "    characteristics: List<AssetCharacteristicEntity>,\\n    bomHeaders: List<AssetBomHeaderEntity>,\\n    bomItems: List<AssetBomItemEntity>,",
        1,
    )
    assets_segment = assets_segment.replace(
        "    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,\\n    onSaveBom: (AssetBomItemEntity) -> Unit,",
        "    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,\\n    onSaveBomHeader: (AssetBomHeaderEntity) -> Unit,\\n    onDeleteBomHeader: (AssetBomHeaderEntity) -> Unit,\\n    onSaveBom: (AssetBomItemEntity) -> Unit,",
        1,
    )
    assets_segment = assets_segment.replace(
        "            characteristics = characteristics.filter { it.assetId == detailAsset.id },\\n            bomItems = bomItems.filter { it.assetId == detailAsset.id },",
        "            characteristics = characteristics,\\n            bomHeaders = bomHeaders,\\n            bomItems = bomItems,",
        1,
    )
    assets_segment = assets_segment.replace(
        "            onDeleteCharacteristic = onDeleteCharacteristic,\\n            onSaveBom = onSaveBom,",
        "            onDeleteCharacteristic = onDeleteCharacteristic,\\n            onSaveBomHeader = onSaveBomHeader,\\n            onDeleteBomHeader = onDeleteBomHeader,\\n            onSaveBom = onSaveBom,",
        1,
    )
    text = text[:assets_start] + assets_segment + text[assets_end:]
'''
if old not in text:
    raise SystemExit("Expected stage 4 UI patch block was not found")
path.write_text(text.replace(old, new, 1), encoding="utf-8")
print("Stage 4 UI patch script corrected.")
