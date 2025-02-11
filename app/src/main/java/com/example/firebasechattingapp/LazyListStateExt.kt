package com.example.firebasechattingapp

import androidx.compose.foundation.lazy.LazyListState

/**
 * @Created_by: Shishir
 * @Created_on: 10,February,2025
 */

fun LazyListState.reachedToEnd(): Boolean {
    if (layoutInfo.totalItemsCount == 0) return false

    val visibleItemInfo = layoutInfo.visibleItemsInfo
    val lastVisibleItem = visibleItemInfo.last()

    val viewPortHeight = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset
    return (lastVisibleItem.index + 1) == layoutInfo.totalItemsCount
            && (lastVisibleItem.offset + lastVisibleItem.size) <= viewPortHeight
}