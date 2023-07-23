package com.brave.mydragdropcomposeapp.util

import com.brave.mydragdropcomposeapp.entity.ApplicationInfo

object SortUtils {


    fun calculPos(
        list: ArrayList<ApplicationInfo>, app: ApplicationInfo
    ) {
        var currentPos = findCurrentCell(app.posX, app.posY)
        if (app.position == LauncherConfig.POSITION_HOME) {

            if (currentPos < 0) currentPos = 0
            else if (currentPos >= list.size) currentPos = list.size - 1
            var isEmpty = true
            list.forEach {
                if (it.cellPos == currentPos) {
                    isEmpty = false
                    LogUtils.e("it.cellPos = $it.cellPos      currentPos = $currentPos ")
                    return@forEach
                }
            }
            if (isEmpty) {
                findPosByCell(currentPos)?.let {
                    app.orignX = it[0]
                    app.orignY = it[1]
                    app.cellPos = currentPos
                }
            }
        }
    }

    fun resetChoosePos(
        list: ArrayList<ApplicationInfo>, app: ApplicationInfo, toolList: ArrayList<ApplicationInfo>
    ) {
        list.forEach continuing@{
            if (app == it) return@continuing
            it.orignX = it.posX
            it.orignY = it.posY
        }
        toolList.forEach continuing@{
            if (app == it) return@continuing
            it.orignX = it.posX
            it.orignY = it.posY
        }
        var currenPos = findCurrentCell(app.posX, app.posY)
        val prePos = findCurrentCell(app.orignX, app.orignY)
        LogUtils.e("cellIndex=${currenPos} preCell=${prePos}")

        if (app.position == LauncherConfig.POSITION_HOME) {
            if (currenPos == prePos) return
            if (currenPos <= -100) {
                currenPos = -currenPos - 100

                toolList.firstOrNull { it.cellPos == currenPos }?.let { destApp ->
                    LogUtils.e("1  ${app.dragInfo == destApp} dragInfo=${app.dragInfo}")
                    val appCell = destApp.cellPos
                    if (destApp == app.dragInfo) {
                        return
                    } else {
                        val dragInfo = app.dragInfo
                        if (dragInfo != null) {
                            destApp.needMoveX = -dragInfo.orignX + destApp.posX
                            destApp.needMoveY = -dragInfo.orignY + destApp.posY
                            destApp.orignX = dragInfo.orignX
                            destApp.orignY = dragInfo.orignY
                            destApp.cellPos = dragInfo.cellPos
                            destApp.showText = true

                            dragInfo.orignX = app.orignX
                            dragInfo.orignY = app.orignY
                            dragInfo.needMoveX = dragInfo.posX - app.orignX
                            dragInfo.needMoveY = dragInfo.posY - app.orignY
                            dragInfo.cellPos = app.cellPos
                            dragInfo.showText = false

                            app.orignX = destApp.posX
                            app.orignY = destApp.posY
                            app.needMoveX = app.orignX - app.posX
                            app.needMoveY = app.orignY - app.posY
                            app.dragInfo = destApp
                            app.cellPos = appCell
                            app.showText = false
                        } else {
                            destApp.needMoveX = -app.orignX + destApp.posX
                            destApp.needMoveY = -app.orignY + destApp.posY
                            destApp.orignX = app.orignX
                            destApp.orignY = app.orignY
                            destApp.cellPos = app.cellPos
                            destApp.showText = true

                            app.orignX = destApp.posX
                            app.orignY = destApp.posY
                            app.needMoveX = app.orignX - app.posX
                            app.needMoveY = app.orignY - app.posY
                            app.dragInfo = destApp
                            app.cellPos = appCell
                            app.showText = false

                        }
                    }
                }
                return
            }
            app.dragInfo?.let { dragInfo ->
                val cOrignX = dragInfo.orignX
                val cOrignY = dragInfo.orignY
                val appCell = dragInfo.cellPos

                dragInfo.orignX = app.orignX
                dragInfo.orignY = app.orignY
                dragInfo.needMoveX = dragInfo.posX - app.orignX
                dragInfo.needMoveY = dragInfo.posY - app.orignY
                dragInfo.cellPos = app.cellPos
                dragInfo.showText = false

                app.orignX = cOrignX
                app.orignY = cOrignY
                app.needMoveX = app.orignX - app.posX
                app.needMoveY = app.orignY - app.posY
                app.dragInfo = null
                app.cellPos = appCell
                app.showText = true
            }
            if (currenPos < 0) currenPos = 0
            else if (currenPos >= list.size) currenPos = list.size - 1

            app.cellPos = currenPos
            var mIndex = 0
            list.sortedBy { it.cellPos }.forEachIndexed { pos, ai ->
                val index = if (ai == app) currenPos
                else if (currenPos < prePos) {
                    if (mIndex < currenPos) mIndex else mIndex + 1
                } else {
                    if (mIndex >= currenPos) mIndex + 1 else mIndex
                }

                ai.orignX = LauncherConfig.HOME_DEFAULT_PADDING_LEFT + (index % 4) * ai.width
                ai.orignY =
                    index / 4 * LauncherConfig.HOME_CELL_HEIGHT + LauncherConfig.DEFAULT_TOP_PADDING
                ai.needMoveX = ai.posX - ai.orignX
                ai.needMoveY = ai.posY - ai.orignY
                ai.cellPos = index
                if (ai != app) mIndex++
            }
        } else {
            if (currenPos == prePos || currenPos >= 0 || prePos >= 0) return
            currenPos = -currenPos - 100
            app.cellPos = currenPos
            var mIndex = 0
            toolList.sortedBy { it.cellPos }.forEachIndexed { _, ai ->
                val index = if (ai == app) currenPos
                else if (currenPos < prePos) {
                    if (mIndex < currenPos) mIndex else mIndex + 1
                } else {
                    if (mIndex >= currenPos) mIndex + 1 else mIndex
                }

                ai.orignX = LauncherConfig.HOME_DEFAULT_PADDING_LEFT + (index) * ai.width
                ai.orignY = LauncherConfig.HOME_HEIGHT - LauncherConfig.HOME_CELL_WIDTH
                ai.needMoveX = ai.posX - ai.orignX
                ai.needMoveY = ai.posY - ai.orignY
                ai.cellPos = index
                if (ai != app) mIndex++
            }
        }

    }

    fun findCurrentCellByPos(posX: Int, posY: Int): Int {
        if (posY < LauncherConfig.DEFAULT_TOP_PADDING) {
            return -1
        }
        if (posX <= LauncherConfig.HOME_DEFAULT_PADDING_LEFT) return LauncherConfig.CELL_POS_HOME_LEFT
        if (posX >= LauncherConfig.HOME_WIDTH - LauncherConfig.HOME_DEFAULT_PADDING_LEFT) return LauncherConfig.CELL_POS_HOME_RIGHT

        if (posY >= LauncherConfig.HOME_TOOLBAR_START - 40) {
            val pos = (posX + LauncherConfig.HOME_CELL_WIDTH / 2) / LauncherConfig.HOME_CELL_WIDTH
            return -pos - 100
        }

        val cellX =
            (posX - LauncherConfig.HOME_DEFAULT_PADDING_LEFT) / (LauncherConfig.HOME_CELL_WIDTH)


        val cellY = (posY - LauncherConfig.DEFAULT_TOP_PADDING) / LauncherConfig.HOME_CELL_HEIGHT

        LogUtils.e("cell=$cellX  cellY=$cellY de=${posX / (LauncherConfig.HOME_WIDTH / 8)}")

        return cellX + cellY * 4
    }

    fun findCurrentActorPix(list: List<ApplicationInfo>, pixX: Int, pixY: Int): ApplicationInfo? {
        val posX = DisplayUtils.pxToDp(pixX)
        val posY = DisplayUtils.pxToDp(pixY)
        list.forEach {
            if (posX >= it.posX && posX < it.posX + it.width && posY >= it.posY && posY < it.posY + it.height) {
                return it
            }
        }
        return null
    }

    fun findCurrentActorDp(list: List<ApplicationInfo>, dpX: Int, dpY: Int): ApplicationInfo? {
        val posX = dpX
        val posY = dpY
        list.forEach {
            if (posX >= it.posX && posX < it.posX + it.width && posY >= it.posY && posY < it.posY + it.height) {
                return it
            }
        }
        return null
    }


    fun findCurrentCell(posX: Int, posY: Int): Int {
        if (posY < LauncherConfig.DEFAULT_TOP_PADDING - LauncherConfig.CELL_ICON_WIDTH / 2) {
            return -1
        }

        if (posX <= -LauncherConfig.HOME_CELL_WIDTH / 3) {
            return LauncherConfig.CELL_POS_HOME_LEFT
        } else if (posX >= LauncherConfig.HOME_WIDTH - LauncherConfig.HOME_CELL_WIDTH * 2 / 3) {
            return LauncherConfig.CELL_POS_HOME_RIGHT
        }
        if (posY >= LauncherConfig.HOME_TOOLBAR_START - 40) {
            val pos = (posX + LauncherConfig.HOME_CELL_WIDTH / 2) / LauncherConfig.HOME_CELL_WIDTH
            return -pos - 100
        }

        val cellX = (posX + LauncherConfig.HOME_CELL_WIDTH / 2) / LauncherConfig.HOME_CELL_WIDTH


        val cellY =
            (posY - LauncherConfig.DEFAULT_TOP_PADDING + LauncherConfig.HOME_CELL_HEIGHT / 2) / LauncherConfig.HOME_CELL_HEIGHT

        return cellX + cellY * 4
    }

    private fun findPosByCell(currentCell: Int): Array<Int>? {
        if (currentCell in 101 downTo -1) return null
        val cellX = currentCell % 4
        val cellY = currentCell / 4
        val posX = cellX * LauncherConfig.HOME_CELL_WIDTH + LauncherConfig.HOME_DEFAULT_PADDING_LEFT
        val posY = cellY * LauncherConfig.HOME_CELL_HEIGHT + LauncherConfig.DEFAULT_TOP_PADDING
        return arrayOf(posX, posY)
    }

    fun swapChange(
        applist: ArrayList<ApplicationInfo>,
        toolList: ArrayList<ApplicationInfo>,
        app: ApplicationInfo
    ) {
        val app1 = app
        val app2 = app.dragInfo
        if (app2 != null) {
            val index = toolList.indexOf(app2)
            toolList.remove(app2)
            toolList.add(index, app1)
            applist.remove(app1)
            applist.add(app2)

            app.position = LauncherConfig.POSITION_TOOLBAR
            app2.position = LauncherConfig.POSITION_HOME
            LogUtils.e("swap")
        }

        app.dragInfo = null
    }

}
