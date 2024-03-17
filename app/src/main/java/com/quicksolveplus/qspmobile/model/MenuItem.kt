package com.quicksolveplus.qspmobile.model

data class MenuItem(
    var imgIcon: Int,
    var count: Int,
    var title: String,
    var drawerItemType: DrawerItemType
)

enum class DrawerItemType(type: Int) {
    Clients(1), Employees(2), Schedule(3), Dashboard(4), MyTimeSheets(5), ServiceRecord(6), OpenShifts(7), DailyServiceNotes(8)
}
