from pathlib import Path

path = Path(__file__).with_name("app3_assets_stage4_ui.py")
text = path.read_text(encoding="utf-8")
marker = "# AssetDetailScreen signature."
insert_tag = "# Ensure the asset-detail call receives all structured component-list data."

if insert_tag not in text:
    block = '''# Ensure the asset-detail call receives all structured component-list data.
assets_start = text.find("private fun AssetsScreen(")
assets_end = text.find("private fun AssetCard", assets_start)
if assets_start < 0 or assets_end < 0:
    raise RuntimeError("AssetsScreen markers not found while wiring component-list headers")
assets_segment = text[assets_start:assets_end]
detail_call_start = assets_segment.find("        AssetDetailScreen(")
detail_call_end = assets_segment.find("\n        )\n        return", detail_call_start)
if detail_call_start < 0 or detail_call_end < 0:
    raise RuntimeError("AssetDetailScreen call markers not found")
detail_call = assets_segment[detail_call_start:detail_call_end]
if "bomHeaders = bomHeaders" not in detail_call:
    if "            characteristics = characteristics," in detail_call:
        detail_call = detail_call.replace(
            "            characteristics = characteristics,",
            "            characteristics = characteristics,\n            bomHeaders = bomHeaders,",
            1,
        )
    elif "            characteristics = characteristics.filter { it.assetId == detailAsset.id }," in detail_call:
        detail_call = detail_call.replace(
            "            characteristics = characteristics.filter { it.assetId == detailAsset.id },",
            "            characteristics = characteristics,\n            bomHeaders = bomHeaders,",
            1,
        )
    else:
        raise RuntimeError("AssetDetailScreen characteristics argument not found")
if "            bomItems = bomItems.filter { it.assetId == detailAsset.id }," in detail_call:
    detail_call = detail_call.replace(
        "            bomItems = bomItems.filter { it.assetId == detailAsset.id },",
        "            bomItems = bomItems,",
        1,
    )
assets_segment = (
    assets_segment[:detail_call_start]
    + detail_call
    + assets_segment[detail_call_end:]
)
text = text[:assets_start] + assets_segment + text[assets_end:]

'''
    if marker not in text:
        raise SystemExit("AssetDetailScreen signature marker was not found")
    text = text.replace(marker, block + marker, 1)

path.write_text(text, encoding="utf-8")
print("Stage 4 asset-detail component-list wiring corrected.")
