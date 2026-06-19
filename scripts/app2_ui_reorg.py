from pathlib import Path

path = Path("app/src/main/java/com/alhadi/cmms/ui/CmmsApp.kt")
text = path.read_text(encoding="utf-8")

if "private data class MoreGroup" in text and "MORE_GROUPS.forEach" in text:
    print("UI reorganization already applied.")
    raise SystemExit(0)

old_start = text.index("private val ALL_MORE_MODULES = listOf(")
old_end = text.index("\n\n@Composable\nprivate fun MoreGrid", old_start)

grouped_modules = '''/**
 * All available modules that can appear under the "المزيد" tab.
 * Each entry declares route, localized title/subtitle, icon, and accent color.
 */
private val ALL_MORE_MODULES = listOf(
    MoreModule(MoreRoute.Notifications, "البلاغات", "بلاغات الصيانة", Icons.Filled.NotificationsActive, AccentRed),
    MoreModule(MoreRoute.Inventory, "المخزون", "قطع الغيار والحركات", Icons.Filled.Inventory2, AccentPurple),
    MoreModule(MoreRoute.Procurement, "المشتريات", "طلبات الشراء والاستلام", Icons.Filled.ShoppingCart, AccentGreen),
    MoreModule(MoreRoute.Suppliers, "الموردون", "إدارة الموردين", Icons.Filled.Store, AccentTeal),
    MoreModule(MoreRoute.Reports, "التقارير", "مؤشرات وتحليلات", Icons.Filled.Analytics, AccentBlue),
    MoreModule(MoreRoute.PreventiveMaintenance, "الصيانة الدورية", "جدول المهام الوقائية", Icons.Filled.EventRepeat, AccentTeal),
    MoreModule(MoreRoute.TaskLists, "قوالب العمل", "قوالب العمليات", Icons.AutoMirrored.Filled.List, AccentBlue),
    MoreModule(MoreRoute.Meters, "العدّادات", "القراءات والقياسات", Icons.Filled.Speed, AccentPurple),
    MoreModule(MoreRoute.Locations, "المواقع الفنية", "هرمية المواقع", Icons.Filled.AccountTree, AccentGreen),
    MoreModule(MoreRoute.Capa, "الإجراءات CAPA", "تصحيحية ووقائية", Icons.Filled.FactCheck, AccentOrange),
    MoreModule(MoreRoute.Failures, "تحليل الأعطال", "MTTR / MTBF", Icons.Filled.TrendingUp, AccentRed),
    MoreModule(MoreRoute.Audit, "سجل الحوكمة", "من فعل ماذا ومتى", Icons.Filled.History, AccentNavy),
    MoreModule(MoreRoute.Trash, "سلة المحذوفات", "استرجاع أو حذف نهائي", Icons.Filled.Delete, AccentBrown),
    MoreModule(MoreRoute.Admin, "الإدارة", "المستخدمون والصلاحيات", Icons.Filled.AdminPanelSettings, AccentOrange)
)

private data class MoreGroup(val title: String, val routes: List<MoreRoute>)

private val MORE_GROUPS = listOf(
    MoreGroup("العمل والصيانة", listOf(
        MoreRoute.Notifications,
        MoreRoute.PreventiveMaintenance,
        MoreRoute.TaskLists,
        MoreRoute.Meters,
        MoreRoute.Capa,
        MoreRoute.Failures
    )),
    MoreGroup("الأصول والمواقع", listOf(
        MoreRoute.Locations
    )),
    MoreGroup("المخزون والمشتريات", listOf(
        MoreRoute.Inventory,
        MoreRoute.Procurement,
        MoreRoute.Suppliers
    )),
    MoreGroup("التقارير والحوكمة", listOf(
        MoreRoute.Reports,
        MoreRoute.Audit
    )),
    MoreGroup("الإدارة وسلة المحذوفات", listOf(
        MoreRoute.Admin,
        MoreRoute.Trash
    ))
)'''

text = text[:old_start] + grouped_modules + text[old_end:]

fun_start = text.index("private fun MoreGrid(")
brace_start = text.index("{", fun_start)
idx = brace_start
depth = 0
while idx < len(text):
    ch = text[idx]
    if ch == "{":
        depth += 1
    elif ch == "}":
        depth -= 1
        if depth == 0:
            fun_end = idx + 1
            break
    idx += 1
else:
    raise RuntimeError("Could not find end of MoreGrid function")

signature = text[fun_start:brace_start + 1]
new_body = '''
    val modules = ALL_MORE_MODULES.filter { allowedMoreRoute(user, it.route) }
    val moduleMap = modules.associateBy { it.route }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (canManage) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            IconBubble(Icons.Filled.UploadFile, AccentGreen, AccentGreen.copy(alpha = 0.14f), 40)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("استيراد من Excel", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "حوّل ملف صيانة الآلة إلى أصل وخطط وقطع غيار وأمر عمل تلقائياً.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = onImportBundled, modifier = Modifier.weight(1f)) { Text("استيراد قالب FVV المرفق") }
                            OutlinedButton(onClick = onPickExcel, modifier = Modifier.weight(1f)) { Text("رفع ملف Excel") }
                        }
                    }
                }
            }
        }

        MORE_GROUPS.forEach { group ->
            val groupModules = group.routes.mapNotNull { moduleMap[it] }
            if (groupModules.isNotEmpty()) {
                item {
                    Text(
                        text = group.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(groupModules.chunked(2)) { rowModules ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        rowModules.forEach { m ->
                            ModuleCard(m.title, m.subtitle, m.icon, m.color, Modifier.weight(1f)) { onOpen(m.route) }
                        }
                        if (rowModules.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("تسجيل الخروج", "إنهاء الجلسة الحالية", Icons.AutoMirrored.Filled.Logout, AccentNavy, Modifier.weight(1f)) { onLogout() }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}'''

text = text[:fun_start] + signature + new_body + text[fun_end:]
path.write_text(text, encoding="utf-8")
print("Applied App2 More screen grouped UI layout.")
