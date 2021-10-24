package com.anonymous.appilogue.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonymous.appilogue.model.ReviewedApp
import com.anonymous.appilogue.repository.AppRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val appRepository: AppRepository) : ViewModel() {

    private val _starFocused = MutableLiveData(Focus.None)
    val starFocused: LiveData<Focus> = _starFocused

    private val _bottomSheetState = MutableLiveData(BottomSheetBehavior.STATE_HIDDEN)
    val bottomSheetState: LiveData<Int> = _bottomSheetState

    private val _bottomSheetHideable = MutableLiveData(true)
    val bottomSheetHideable: LiveData<Boolean> = _bottomSheetHideable

    private val _apps = MutableLiveData(listOf<ReviewedApp>())
    val apps: LiveData<List<ReviewedApp>> = _apps

    fun changeBottomSheetState(newState: Int) {
        _bottomSheetState.value = newState
    }

    fun changeFocus(focus: Focus) {
        if (focus == Focus.None) {
            starFocused.value?.let {
                _starFocused.value = Focus.toOffFocus(it)
            }
        }
        _starFocused.value = focus
        if (Focus.isOnFocus(focus)) {
            changeBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)
            viewModelScope.launch {
                delay(10)
                _bottomSheetHideable.value = false
            }
        } else {
            _bottomSheetHideable.value = true
            changeBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    fun fetchBlackHoleApps() {
        _apps.value = appRepository.getBlackHoleApps()
    }

    fun fetchWhiteHoleApps() {
        _apps.value = appRepository.getWhiteHoleApps()
    }
}