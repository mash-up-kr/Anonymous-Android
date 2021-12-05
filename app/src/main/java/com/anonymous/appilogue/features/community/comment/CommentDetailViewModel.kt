package com.anonymous.appilogue.features.community.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anonymous.appilogue.features.base.UiState
import com.anonymous.appilogue.features.base.isSuccessful
import com.anonymous.appilogue.features.base.successOr
import com.anonymous.appilogue.model.CommentDto
import com.anonymous.appilogue.model.CommentModel
import com.anonymous.appilogue.usecase.FetchCommentsUseCase
import com.anonymous.appilogue.usecase.RegisterCommentUseCase
import com.anonymous.appilogue.usecase.RemoveCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchCommentsUseCase: FetchCommentsUseCase,
    private val registerCommentUseCase: RegisterCommentUseCase,
    private val removeCommentUseCase: RemoveCommentUseCase
) : ViewModel() {
    private val reviewId: Int = savedStateHandle.get<Int>(REVIEW_ID_KEY)!!
    private val commentId: Int = savedStateHandle.get<Int>(COMMENT_ID_KEY)!!

    private val _uiState: MutableStateFlow<UiState<List<CommentModel>>> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<List<CommentModel>>> = _uiState

    init {
        fetchComments()
    }

    val comments: StateFlow<List<CommentModel>> = uiState.mapLatest { state ->
        state.successOr(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList()
    )

    val inputText: MutableLiveData<String?> = MutableLiveData()

    private fun fetchComments() {
        viewModelScope.launch {
            fetchCommentsUseCase(reviewId, commentId).collect {
                _uiState.value = it
            }
        }
    }

    fun registerNestedComment() {
        inputText.value?.let {
            viewModelScope.launch {
                val commentDto = CommentDto(reviewId, commentId, it)
                val result = registerCommentUseCase(commentDto)
                if (result.isSuccessful) {
                    fetchComments()
                }
                inputText.value = ""
            }
        }
    }

    companion object {
        private const val REVIEW_ID_KEY = "reviewId"
        private const val COMMENT_ID_KEY = "commentId"
    }
}