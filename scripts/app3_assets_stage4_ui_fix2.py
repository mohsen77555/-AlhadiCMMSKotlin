from pathlib import Path

path = Path(__file__).with_name("app3_assets_stage4_ui.py")
text = path.read_text(encoding="utf-8")
old = '    text = replace_range(text, old_bom_start, old_bom_end, new_bom + old_bom_end, "structured component-list panel")'
new = '    text = replace_range(text, old_bom_start, old_bom_end, new_bom, "structured component-list panel")'
if old not in text:
    raise SystemExit("Expected duplicated-block replacement was not found")
path.write_text(text.replace(old, new, 1), encoding="utf-8")
print("Stage 4 component-list block replacement corrected.")
