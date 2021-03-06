package com.depromeet.fragraph.feature.recommendation.incense_select.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class IncenseRecyclerViewScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val incenseAdapter: IncenseRecyclerViewAdapter,
    private val visibleCount: Int,
    private val showPositionCallback: (position: Int) -> Unit
): RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val firstPosition = layoutManager.findFirstVisibleItemPosition()
        val lastPosition = layoutManager.findLastVisibleItemPosition()
        val itemCount = layoutManager.itemCount

        // firstPosition == 0 &  lastPosition == 1 (첫번째인 경우)
        if (firstPosition == 0 && lastPosition == 1) {
            // 첫번째를 보여준다. (뒤를 가린다)
            incenseAdapter.changeCenterValue(firstPosition, isCenter = true)
            incenseAdapter.changeCenterValue(lastPosition, isCenter = false)
            showPositionCallback(0)
            return
        }

        if (visibleCount == 2) {

            // 첫번째 것이 보이는 경우
            if (lastPosition - firstPosition == 1) {
                // 새로 생기는 부분 보여줌
                incenseAdapter.changeCenterValue(firstPosition - 1, isCenter = false)
                incenseAdapter.changeCenterValue(firstPosition, isCenter = true)
                incenseAdapter.changeCenterValue(lastPosition, isCenter = false)
                showPositionCallback(firstPosition)
                return
            }

            // 마지막인 경우
            if (firstPosition == lastPosition) {
                incenseAdapter.changeCenterValue(firstPosition, isCenter = false)
                incenseAdapter.changeCenterValue(lastPosition, isCenter = true)
                showPositionCallback(lastPosition)
                return
            }
        }

        if (visibleCount == 3) {
            // 양쪽이 보이는 경우
            if (lastPosition - firstPosition == 2) {
                // 가운데를 보여준다 (양쪽을 가린다.)
                incenseAdapter.changeCenterValue(firstPosition, isCenter = false)
                incenseAdapter.changeCenterValue(firstPosition + 1, isCenter = true)
                incenseAdapter.changeCenterValue(lastPosition, isCenter = false)
                showPositionCallback(firstPosition + 1)
                return
            }

            // (마지막 경우)
            if (firstPosition == itemCount -2 && lastPosition == itemCount -1) {
                // 마지막을 보여준다. (앞을 가린다)
                incenseAdapter.changeCenterValue(firstPosition, isCenter = false)
                incenseAdapter.changeCenterValue(lastPosition, isCenter = true)
                showPositionCallback(lastPosition)
                return
            }

            // 마지막에서 슬라이드 하는 경우
            if (firstPosition == lastPosition) {
                // 아무것도 하지 않는다.
                return
            }
        }
    }
}