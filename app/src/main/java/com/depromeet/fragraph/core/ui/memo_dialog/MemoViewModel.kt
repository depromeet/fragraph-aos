package com.depromeet.fragraph.core.ui.memo_dialog

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.depromeet.fragraph.R
import com.depromeet.fragraph.core.event.Event
import com.depromeet.fragraph.domain.model.Memo
import com.depromeet.fragraph.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class MemoViewModel @ViewModelInject constructor(
    private val historyRepository: HistoryRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var historyId: Int? = null
    private var memoId: Int? = null

    private val _memoToastMessageEvent = MutableLiveData<Event<Int>>()
    val memoToastMessageEvent: LiveData<Event<Int>>
        get() = _memoToastMessageEvent

    private val _memoCloseEvent = MutableLiveData<Event<Unit>>()
    val memoCloseEvent: LiveData<Event<Unit>>
        get() = _memoCloseEvent

    private val _savedMemoEvent = MutableLiveData<Event<Unit>>()
    val savedMemoEvent: LiveData<Event<Unit>>
        get() = _savedMemoEvent

    private val _createdAt = MutableLiveData(0L)
    val createdAt: LiveData<Long>
        get() = _createdAt

    private val _memoTitle = MutableLiveData("")
    val memoTitle: MutableLiveData<String>
        get() = _memoTitle

    private val _memoContent = MutableLiveData("")
    val memoContent: MutableLiveData<String>
        get() = _memoContent

    private val _memoContentsLength = MutableLiveData("0")
    val memoContentsLength: LiveData<String>
        get() = _memoContentsLength

    private val _hasContents = MutableLiveData(false)
    val hasContents: LiveData<Boolean>
        get() = _hasContents

    fun setMemoDefault(historyId: Int, createdAt: Long) {
        this.historyId = historyId
        _createdAt.postValue(createdAt)
        if (memoId != null) {
            Timber.d("memoId: $memoId")
            viewModelScope.launch(Dispatchers.IO) {
                historyRepository.getMemo(historyId, memoId!!)
                    .collect {
                        memoId = it.id
                        _memoTitle.postValue(it.title)
                        _memoContent.postValue(it.content)
                    }
            }
        } else {
            resetMemo()
        }
    }

    fun setMemoDefault(historyId: Int, createdAt: Long, memo: Memo) {
        this.historyId = historyId
        memoId = memo.id
        _createdAt.postValue(createdAt)
        _memoTitle.postValue(memo.title)
        _memoContent.postValue(memo.content)
    }

    fun onBgClick() {
        _memoCloseEvent.postValue(Event(Unit))
        if (hasContents.value!!) {
            saveMemo()
            return
        }

        if (memoId !== null) {
            deleteMemo(true)
            return
        }
    }
    
    fun saveMemo(forceSave: Boolean = false) {

        if (!hasContents.value!! && !forceSave) {
//            _memoToastMessageEvent.postValue(Event(R.string.memo_content_null))
            return
        }

        if (historyId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                memoId?.let {id ->
                    historyRepository.updateMemo(historyId!!, Memo(id, _memoTitle.value!!, _memoContent.value!!, null))
                        .catch {
                            Timber.d("memo 수정하는데 실패함")
                        }
                        .collect {
                             memoId = it
                            _memoToastMessageEvent.postValue(Event(R.string.memo_save_success))
                            _savedMemoEvent.postValue(Event(Unit))
                            _memoCloseEvent.postValue(Event(Unit))
                        }
                } ?: kotlin.run {
                    historyRepository.saveMemo(historyId!!, _memoTitle.value!!, _memoContent.value!!)
                        .catch {
                            Timber.d("memo 저장하는데 실패함")
                        }
                        .collect {
                            memoId = it
                            _memoToastMessageEvent.postValue(Event(R.string.memo_save_success))
                            _savedMemoEvent.postValue(Event(Unit))
                            _memoCloseEvent.postValue(Event(Unit))
                        }
                }
            }
        } else {
            _memoToastMessageEvent.postValue(Event(R.string.memo_save_failure))
        }
    }

    fun deleteMemo(forceDelete: Boolean = false) {
        if (!hasContents.value!! && !forceDelete) {
//            _memoToastMessageEvent.postValue(Event(R.string.memo_content_null))
            return
        }

        if (historyId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                memoId?.let {memoId ->
                    historyRepository.deleteMemo(historyId!!, memoId)
                        .catch {
                            Timber.d("memo 삭제하는데 실패함")
                        }
                        .collect {
                            _memoToastMessageEvent.postValue(Event(R.string.memo_delete_success))
                            _memoCloseEvent.postValue(Event(Unit))
                            resetMemo()
                        }
                } ?: kotlin.run {
                    _memoToastMessageEvent.postValue(Event(R.string.memo_delete_success))
                    _memoCloseEvent.postValue(Event(Unit))
                    resetMemo()
                }
            }
        } else {
            _memoToastMessageEvent.postValue(Event(R.string.memo_delete_failure))
        }
    }

    private fun resetMemo() {
        memoId = null
        _memoTitle.postValue("")
        _memoContent.postValue("")
    }

    fun onTitleChanged(s: CharSequence, start :Int, before : Int, count: Int) {
        if(s.isNotEmpty() || _memoContent.value!!.isNotEmpty()) {
            _hasContents.postValue(true)
        } else {
            _hasContents.postValue(false)
        }
    }

    fun onContentChanged(s: CharSequence, start :Int, before : Int, count: Int) {
        _memoContentsLength.postValue(s.length.toString())

        if(s.isNotEmpty() || _memoTitle.value!!.isNotEmpty()) {
            _hasContents.postValue(true)
        } else {
            _hasContents.postValue(false)
        }
    }
}