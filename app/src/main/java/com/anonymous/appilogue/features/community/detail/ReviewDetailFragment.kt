package com.anonymous.appilogue.features.community.detail

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.anonymous.appilogue.R
import com.anonymous.appilogue.databinding.FragmentReviewDetailBinding
import com.anonymous.appilogue.features.base.BaseFragment
import com.anonymous.appilogue.features.main.MainActivity
import com.anonymous.appilogue.model.CommentModel
import com.anonymous.appilogue.persistence.PreferencesManager
import com.anonymous.appilogue.utils.hideKeyboardDown
import com.anonymous.appilogue.utils.showToast
import com.anonymous.appilogue.widget.BottomSheetMenuDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewDetailFragment
    : BaseFragment<FragmentReviewDetailBinding, ReviewDetailViewModel>(R.layout.fragment_review_detail) {

    override val viewModel: ReviewDetailViewModel by viewModels()

    private val commentAdapter: CommentAdapter by lazy {
        CommentAdapter(this::navigateToCommentDetail, this::showBottomSheetMenu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboardDown()

        bind {
            vm = viewModel
        }

        initView()
        initRecyclerView()
        initObservers()
    }

    private fun initView() {
        bind {
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.fetchReviews()
                swipeRefreshLayout.isRefreshing = false
            }

            toolbarRightIconView.setOnClickListener {
                val isMyReview = PreferencesManager.getUserId() == viewModel.getAuthorId()
                val bottomSheetMenu = BottomSheetMenuDialog(isMyReview) {
                    if (isMyReview) {
                        viewModel.removeReviewEvent()
                    } else {
                        viewModel.reportReviewEvent()
                    }
                }
                (activity as MainActivity).showBottomSheetDialog(bottomSheetMenu)
            }
        }
    }

    private fun initRecyclerView() {
        bind {
            commentRecyclerView.apply {
                adapter = commentAdapter
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.reviewInfo.collect {
                commentAdapter.submitList(it.comments.filter { comment -> comment.parentId == null })
            }
        }
        lifecycleScope.launch {
            viewModel.event.collect {
                handleEvent(it)
            }
        }
    }

    private fun showBottomSheetMenu(commentModel: CommentModel) {
        val isMyComment = PreferencesManager.getUserId() == commentModel.user.id
        val bottomSheetMenu = BottomSheetMenuDialog(isMyComment) {
            if (isMyComment) {
                viewModel.removeCommentEvent(commentModel.id)
            } else {
                viewModel.reportCommentEvent(commentModel.id)
            }
        }
        (activity as MainActivity).showBottomSheetDialog(bottomSheetMenu)
    }

    private fun handleEvent(event: ReviewDetailViewModel.Event) {
        when (event) {
            is ReviewDetailViewModel.Event.AddComment -> {
                viewModel.registerComment(event.commentText)
                binding.root.hideKeyboardDown()
            }
            is ReviewDetailViewModel.Event.RemoveComment -> {
                viewModel.removeComment(event.commentId)
            }
            is ReviewDetailViewModel.Event.ReportComment -> {
                viewModel.reportComment(event.commentId)
            }
            is ReviewDetailViewModel.Event.RemoveReview -> {
                viewModel.removeReview()
            }
            is ReviewDetailViewModel.Event.ReportReview -> {
                viewModel.reportReview()
            }
            is ReviewDetailViewModel.Event.PressBackButton -> {
                activity?.onBackPressed()
            }
            is ReviewDetailViewModel.Event.ShowToastForResult -> {
                val message = if (event.isMine) {
                    getString(R.string.remove_result_message)
                } else {
                    getString(R.string.report_result_message)
                }
                context?.showToast(message)
            }
        }
    }

    private fun navigateToCommentDetail(commentId: Int) {
        val action = ReviewDetailFragmentDirections.actionReviewDetailFragmentToCommentDetailFragment(viewModel.reviewId, commentId)
        (activity as MainActivity).navigateTo(action)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        super.onDetach()

        (activity as MainActivity).showBottomNavigation()
    }
}