package com.depromeet.fragraph.feature.meditation.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.depromeet.fragraph.core.event.Event
import com.depromeet.fragraph.core.ext.*
import com.depromeet.fragraph.core.ui.select_dialog.SelectDialogType
import com.depromeet.fragraph.domain.model.Meditation
import com.depromeet.fragraph.domain.model.enums.IncenseTitle
import com.depromeet.fragraph.domain.repository.HistoryRepository
import com.depromeet.fragraph.domain.repository.MeditationRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class MeditationViewModel @ViewModelInject constructor(
    private val meditationRepository: MeditationRepository,
    private val historyRepository: HistoryRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _exitEvent = MutableLiveData<Event<Unit>>()
    val exitEvent: LiveData<Event<Unit>>
        get() = _exitEvent

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _errorEvent = MutableLiveData<Event<Unit>>()
    val errorEvent: LiveData<Event<Unit>>
        get() = _errorEvent

    private val _onBackgroundClickEvent = MutableLiveData<Event<Unit>>()
    val onBackgroundClickEvent: LiveData<Event<Unit>>
        get() = _onBackgroundClickEvent

    private val _onMemoWritingClickEvent = MutableLiveData<Event<Meditation>>()
    val onMemoWritingClickEvent: LiveData<Event<Meditation>>
        get() = _onMemoWritingClickEvent

    private val _onMemoBackgroundClickEvent = MutableLiveData<Event<SelectDialogType>>()
    val onMemoBackgroundClickEvent: LiveData<Event<SelectDialogType>>
        get() = _onMemoBackgroundClickEvent

    private val _selectDialogVisible = MutableLiveData(false)
    val selectDialogVisible: LiveData<Boolean>
        get() = _selectDialogVisible

    private val _memoDialogVisible = MutableLiveData(false)
    val memoDialogVisible: LiveData<Boolean>
        get() = _memoDialogVisible

    private val _meditation = MutableLiveData<Meditation>()
    val meditation: LiveData<Meditation>
        get() = _meditation

    private val _totalPlaytime = MutableLiveData(0)
    val totalPlaytime: LiveData<Int>
        get() = _totalPlaytime

    private val _remainingTime = MutableLiveData(0)
    val remainingTime: LiveData<Int>
        get() = _remainingTime

    private val _remainingTimeMin = MutableLiveData("")
    val remainingTimeMin: LiveData<String>
        get() = _remainingTimeMin

    private val _remainingTimeSeconds = MutableLiveData("")
    val remainingTimeSeconds: LiveData<String>
        get() = _remainingTimeSeconds

    private val _date = MutableLiveData("")
    val date: LiveData<String>
        get() = _date

    private val _day = MutableLiveData("")
    val day: LiveData<String>
        get() = _day

    private val _incenseTitle = MutableLiveData(IncenseTitle.EMPTY)
    val incenseTitle: LiveData<IncenseTitle>
        get() = _incenseTitle

    private val _videoUrl = MutableLiveData("")
    val videoUrl: LiveData<String>
        get() = _videoUrl

    private val _musicTitle = MutableLiveData("")
    val musicTitle: LiveData<String>
        get() = _musicTitle

    private val _isMusicPlaying = MutableLiveData(false)
    val isMusicPlaying: LiveData<Boolean>
        get() = _isMusicPlaying

    init {
        meditationRepository.getMeditation()?.let {
            _meditation.value = it
            _videoUrl.postValue(it.video.url)
            _musicTitle.postValue(it.music.title)
            _incenseTitle.postValue(it.incense.title)
            _date.postValue(it.date.miliSecondsToStringFormat(FRAGRAPH_HISTORY_FORMAT))
            _day.postValue("${it.date.miliSecondsToStringFormat(JUST_DAY)}요일")
            _totalPlaytime.postValue(it.playtime * 1000)
            this.setRemainingTime(it.playtime * 1000)
            _isMusicPlaying.postValue(true)
        } ?: kotlin.run {
            Timber.d("null???????")
            onErrorMeditation()
        }
    }

    fun setRemainingTime(remainingTime: Int) {
        Timber.tag(TAG).d("set remaining time, remainingTime: $remainingTime")
        _remainingTime.postValue(remainingTime)
        _remainingTimeMin.postValue(remainingTime.milliSecondsToMinutes())
        _remainingTimeSeconds.postValue(remainingTime.milliSecondsToSeconds())
    }

    fun openDialog(memoVisibility: Boolean, selectDialogVisibility: Boolean) {
        _memoDialogVisible.value = memoVisibility
        _selectDialogVisible.value = selectDialogVisibility
    }

    fun closeDialog() {
        _memoDialogVisible.value = false
        _selectDialogVisible.value = false
    }

    fun deleteHistory() {
        meditation.value?.let {
            viewModelScope.launch {
                historyRepository.deleteHistory(it.historyId)
                    .catch {
                        Timber.d("삭제중 오류 발생")
                    }
                    .collect {
                        Timber.d("삭제 완료")
                        _backEvent.postValue(Event(Unit))
                    }
            }
        }
    }

    fun onWritingMemoClick() {
        meditation.value?.let {
            _onMemoWritingClickEvent.value = Event(it)
        }
    }

    fun onDialogBackgroundClick() {
        meditation.value?.let {
            // 메모 dialog 가 올라가 있다면 메모를 저장하고 dialog 를 내림
            if (_memoDialogVisible.value!!) {
                _onMemoBackgroundClickEvent.value = Event(SelectDialogType.MEMO_ON_WRITING)
            } else {
                _onMemoBackgroundClickEvent.value = Event(SelectDialogType.HIDE_DIALOG)
            }
        }
    }

    fun onBackgroundClick() {
        _isMusicPlaying.postValue(!_isMusicPlaying.value!!)
        _onBackgroundClickEvent.postValue(Event(Unit))
    }

    fun onErrorMeditation() {
        _errorEvent.postValue(Event(Unit))
    }

    fun onExitClick() {
        _exitEvent.postValue(Event(Unit))
    }

    companion object {
        const val TAG = "MeditationViewModel"
    }
}