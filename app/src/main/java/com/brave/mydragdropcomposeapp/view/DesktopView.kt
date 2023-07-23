package com.brave.mydragdropcomposeapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.brave.mydragdropcomposeapp.viewmodel.HomeViewModel
import com.brave.mydragdropcomposeapp.entity.AppInfoBaseBean
import com.brave.mydragdropcomposeapp.entity.ApplicationInfo
import com.brave.mydragdropcomposeapp.theme.MyBasicColumn
import com.brave.mydragdropcomposeapp.theme.pagerLazyFlingBehavior
import com.brave.mydragdropcomposeapp.util.LogUtils


@Composable
fun DesktopView(lists: AppInfoBaseBean, viewModel: HomeViewModel, version:MutableState<Int>) {
    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    val state = rememberLazyListState()
    val foldOpenState = remember{
        mutableStateOf<MutableList<ApplicationInfo>>(mutableListOf())
    }
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val coroutineAnimScope = rememberCoroutineScope()

    val dragInfoState = remember { mutableStateOf<ApplicationInfo?>(null) }
    val dragUpState = remember {
        mutableStateOf(false)
    }

    val offsetX = remember { mutableStateOf(0.dp) }
    val offsetY = remember { mutableStateOf(0.dp) }
    val currentSelect = remember { mutableStateOf(0) }
    val animFinish = remember { mutableStateOf(false) }

    val homeList = lists.homeList
    val toolBarList = lists.toobarList


    //draw dot
    val dotWidth = 8;
    val indicationDot = homeList.size * dotWidth + (homeList.size - 1) * 6
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .width(width = indicationDot.dp)
            .height(height = height.dp)
            .offset(
                (width.dp - indicationDot.dp) / 2, (height - 150).dp
            )
    ) {
        homeList.forEachIndexed { index, arrayList ->
            Box(
                modifier = Modifier
                    .size(dotWidth.dp)
                    .clip(CircleShape)
                    .background(Color(if (currentSelect.value == index) 0xccffffff else 0x66ffffff))
            )
        }
    }


    // draw toolbar
    lists.toobarList.let { applist ->
        var homelist = homeList.getOrNull(currentSelect.value) ?: ArrayList()
        MyBasicColumn(modifier = Modifier
            .zIndex(zIndex = 0f)
        )
        {
            applist?.forEachIndexed { index, it -> // so'roq bilan murojat qilinadi bo'lmasa sekinlashib ketadi
                IconView(
                    it = it,
                    dragUpState = dragUpState,
                    foldOpen = foldOpenState
                )
            }
        }
//
    }

    var pos = offsetX.value

    LazyRow(

        modifier = Modifier
            .offset(0.dp, 0.dp)
            .width(width = width.dp)
            .height(height = height.dp)
            .pointerInput(0) {
                detectLongPress(
                    context = context,
                    toolList = toolBarList!!,
                    homeList = homeList,
                    currentSel = currentSelect,
                    coroutineScope = coroutineScope,
                    coroutineAnimScope = coroutineAnimScope,
                    dragInfoState = dragInfoState,
                    animFinish = animFinish,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    dragUpState = dragUpState,
                    state = state,
                    version = version,
                    homeViewModel =viewModel
                )
            },
        state = state,
        flingBehavior = pagerLazyFlingBehavior(
            state,
            (lists.homeList?.size ?: 0)
        )
    ) {
        currentSelect.value = state.firstVisibleItemIndex
        lists.homeList?.let { homeList ->
            if (homeList.size == 0)
                return@let

            lists.homeList?.forEachIndexed { index, applist ->
                item {
                    Column(
                        modifier = Modifier
                            .width(width = width.dp)
                            .height(height = height.dp)
                            .offset(0.dp, 0.dp)

                    ) {
                        MyBasicColumn() {
                            applist.forEach {
                                IconView(
                                    it = it,
                                    dragUpState = dragUpState,
                                    foldOpen = foldOpenState
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    //draw fold
    if(foldOpenState.value.size>0){
        Box(modifier = Modifier
            .size(width.dp, height.dp)
            .clickable {
                foldOpenState.value = mutableListOf()
            })
        {
            Box(
                modifier = Modifier
                    .size(width.dp - 20.dp, 320.dp)
                    .offset(10.dp, (height.dp - 320.dp) / 2)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0.3f, 0.3f, 0.3f, 0.8f))
            ){
                foldOpenState.value.forEach {
                    LogUtils.e("foldSize=${it.posX}  ${it.posY}")

                    IconView(
                        it = it,
                        dragUpState = dragUpState,
                        foldOpen = foldOpenState
                    )
                }
            }
        }
    }


    if (dragUpState.value) {
        dragInfoState?.value?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(it.width.dp, it.height.dp)
                    .offset(it.posX.dp, it.posY.dp)
            ) {
//                LogUtils.e("dragUp = ${dragUpState.value}")
                IconViewDetail(it = it)
            }
        }
    }
//    LogUtils.e("usetime=${System.currentTimeMillis()-time1}")
//    var time = System.currentTimeMillis() - lastTime
//    if (time > 0) {
//        if (time > 30) {
//            Text(
//                text = "fps:30",
//                Modifier.offset(20.dp, 30.dp)
//            )
//        } else {
//            Text(
//                text = "fps:${1000 / time}",
//                Modifier.offset(20.dp, 30.dp)
//            )
//        }
//        lastTime = System.currentTimeMillis();
//    }
}
