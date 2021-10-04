package com.anonymous.appilogue.features.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _starFocused = MutableLiveData(Focus.None)
    val starFocused: LiveData<Focus> = _starFocused

    private val _bottomSheetState = MutableLiveData(BottomSheetBehavior.STATE_HIDDEN)
    val bottomSheetState: LiveData<Int> = _bottomSheetState

    private val _bottomSheetHideable = MutableLiveData(true)
    val bottomSheetHideable: LiveData<Boolean> = _bottomSheetHideable

    fun changeBottomSheetState(newState: Int) {
        _bottomSheetState.value = newState
    }

    fun changeFocus(focus: Focus) {
        if (focus == Focus.None) {
            starFocused.value?.let {
                _starFocused.value = Focus.from(it.ordinal + Focus.STAR_NUM)
            }
        }
        _starFocused.value = focus
        when (focus) {
            Focus.None -> {
                _bottomSheetHideable.value = true
                _bottomSheetState.value = BottomSheetBehavior.STATE_HIDDEN
            }
            else -> {
                _bottomSheetHideable.value = false
                _bottomSheetState.value = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
}