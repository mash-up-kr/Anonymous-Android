package com.anonymous.appilogue.usecase

import com.anonymous.appilogue.features.base.UiState
import com.anonymous.appilogue.model.ReviewModel
import com.anonymous.appilogue.repository.ReviewRepository
import javax.inject.Inject

class FetchReviewListUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(hole: String, page: Int): UiState<List<ReviewModel>> {
        return reviewRepository.fetchReviews(hole, page)
    }
}