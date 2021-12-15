package com.anonymous.appilogue.features.home.bottomsheet.hole

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anonymous.appilogue.R
import com.anonymous.appilogue.databinding.FragmentAppBinding
import com.anonymous.appilogue.features.base.BaseFragment
import com.anonymous.appilogue.features.home.HomeFragmentDirections
import com.anonymous.appilogue.features.main.MainActivity
import com.anonymous.appilogue.model.AppModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HoleFragment :
    BaseFragment<FragmentAppBinding, HoleViewModel>(R.layout.fragment_app) {
    override val viewModel: HoleViewModel by viewModels()
    private val bottomSheetHoleAppAdapter: BottomSheetHoleAppAdapter by lazy {
        BottomSheetHoleAppAdapter(this::navigateToAppInfo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            holeViewModel = viewModel
            rvBottomSheetApps.apply {
                adapter = bottomSheetHoleAppAdapter
                addItemDecoration(BottomSheetHoleAppDecoration(context))
            }
        }
    }

    private fun navigateToAppInfo(app: AppModel) {
        val action = HomeFragmentDirections.actionHomeFragmentToAppInfoFragment(app)
        (activity as MainActivity).navigateTo(R.id.appInfoFragment, action)
    }

    companion object {
        fun newInstance(hole: String): Fragment {
            val fragment = HoleFragment()
            fragment.arguments = Bundle().apply {
                putString(HOLE, hole)
            }
            return fragment
        }

        private const val HOLE = "HOLE"
        const val BLACK_HOLE = "black"
        const val WHITE_HOLE = "white"
    }
}