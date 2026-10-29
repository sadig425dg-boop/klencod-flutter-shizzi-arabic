package dev.shizzi

import android.service.quicksettings.Tile

enum class TileAction { START, STOP, OPEN_APP, NONE }

data class TileRender(
    val state: Int,
    val subtitle: String,
    val action: TileAction,
)

object SessionTile {

    const val LABEL = "Shizzi"

    fun render(session: SessionUiState, shizuku: ShizukuState, isStopping: Boolean): TileRender {
        if (shizuku !is ShizukuState.Ready) return unavailable(shizuku)

        return when (session.status) {
            UiStatus.LOADING -> TileRender(
                state = Tile.STATE_UNAVAILABLE,
                subtitle = if (isStopping) "جاري الإيقاف…" else "جاري البدء…",
                action = TileAction.NONE,
            )

            UiStatus.CONNECTED -> TileRender(
                state = Tile.STATE_ACTIVE,
                subtitle = describeConnected(session),
                action = TileAction.STOP,
            )

            UiStatus.ERROR -> TileRender(
                state = Tile.STATE_INACTIVE,
                subtitle = "اضغط للمحاولة مجدداً",
                action = TileAction.START,
            )

            UiStatus.READY -> TileRender(
                state = Tile.STATE_INACTIVE,
                subtitle = "اضغط للمشاركة",
                action = TileAction.START,
            )
        }
    }

    private fun unavailable(shizuku: ShizukuState) = TileRender(
        state = Tile.STATE_UNAVAILABLE,
        subtitle = describe(shizuku),
        action = TileAction.OPEN_APP,
    )

    private fun describe(shizuku: ShizukuState): String = when (shizuku) {
        ShizukuState.NotInstalled -> "Shizuku غير مثبّت"
        ShizukuState.NotRunning -> "Shizuku غير شغّال"
        ShizukuState.PermissionRequired -> "الصلاحية مطلوبة"
        is ShizukuState.Ready -> ""
    }

    private fun describeConnected(session: SessionUiState): String = when (session.clientCount) {
        0 -> "لا توجد أجهزة"
        1 -> "جهاز واحد · ${Traffic.format(session.traffic.down)}"
        else -> "${session.clientCount} أجهزة · ${Traffic.format(session.traffic.down)}"
    }
}
