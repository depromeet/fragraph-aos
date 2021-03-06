package com.depromeet.fragraph.feature.recommendation.incense_select

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.depromeet.fragraph.R
import com.depromeet.fragraph.base.SharedViewModel
import com.depromeet.fragraph.core.event.EventObserver
import com.depromeet.fragraph.databinding.FragmentIncenseSelectBinding
import com.depromeet.fragraph.feature.recommendation.incense_select.adapter.IncenseRecyclerViewAdapter
import com.depromeet.fragraph.feature.recommendation.incense_select.adapter.IncenseRecyclerViewDecoration
import com.depromeet.fragraph.feature.recommendation.incense_select.adapter.IncenseRecyclerViewScrollListener
import com.depromeet.fragraph.feature.recommendation.incense_select.adapter.IncenseRecyclerViewSnapHelper
import com.depromeet.fragraph.feature.recommendation.incense_select.viewmodel.IncenseSelectViewModel
import com.depromeet.fragraph.feature.recommendation.incense_select.viewmodel.PlaytimePickerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncenseSelectFragment: Fragment(R.layout.fragment_incense_select) {

    private val incenseSelectViewModel: IncenseSelectViewModel by viewModels()

    private val playtimePickerViewModel: PlaytimePickerViewModel by viewModels()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var backType = BackType.POP_BACK_STACK

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mainBinding = FragmentIncenseSelectBinding.bind(view)
            .apply {
                vm = incenseSelectViewModel
                lifecycleOwner = this@IncenseSelectFragment
            }

        mainBinding.viewIncensePlaytimePicker.apply {
            vm = playtimePickerViewModel
            lifecycleOwner = this@IncenseSelectFragment
        }

        mainBinding.rvIncensesRecommendation.apply {
            val snapHelper = IncenseRecyclerViewSnapHelper(this)
            val incenseAdapter = IncenseRecyclerViewAdapter(viewLifecycleOwner) {
                mainBinding.indicatorIncensesRecommendation.attachToRecyclerView(this, snapHelper)
            }
            val linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            val scrollListener = IncenseRecyclerViewScrollListener(linearLayoutManager, incenseAdapter, 2) {
                incenseSelectViewModel.changeSelectedIncense(it)
            }

            adapter = incenseAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(IncenseRecyclerViewDecoration())
            addOnScrollListener(scrollListener)
            snapHelper.attachToRecyclerView(this)
        }

        incenseSelectViewModel.incenseSelectToastMessageEvent.observe(viewLifecycleOwner, EventObserver {
            sharedViewModel.showToastMessage(it)
        })

        incenseSelectViewModel.backClickEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        incenseSelectViewModel.openMeditationEvent.observe(viewLifecycleOwner, EventObserver {
            goMeditation()
        })

        incenseSelectViewModel.playtimePickerVisible.observe(viewLifecycleOwner, {
            backType = if (it) BackType.CLOSE_TIME_PICKER else BackType.POP_BACK_STACK
        })

        playtimePickerViewModel.playtimePickEvent.observe(viewLifecycleOwner, EventObserver {
            incenseSelectViewModel.setPlaytime(it)
        })

        playtimePickerViewModel.playtimePickerToastMessageEvent.observe(viewLifecycleOwner, EventObserver {
            sharedViewModel.showToastMessage(it)
        })
    }

    private fun goMeditation() {
        findNavController().navigate(R.id.action_incenseSelectFragment_to_meditationFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (backType) {
                    BackType.POP_BACK_STACK -> {
                        findNavController().popBackStack()
                    }
                    BackType.CLOSE_TIME_PICKER -> {
                        incenseSelectViewModel.onPlaytimeBackgroundClick()
                    }
                }
            }
        })
    }

    enum class BackType() {
        CLOSE_TIME_PICKER,
        POP_BACK_STACK,
    }
}