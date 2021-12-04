package com.anonymous.appilogue.features.home.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.anonymous.appilogue.R
import com.anonymous.appilogue.databinding.FragmentMyDecorationBinding
import com.anonymous.appilogue.features.base.BaseFragment
import com.anonymous.appilogue.features.home.HomeViewModel
import com.anonymous.appilogue.features.home.SpaceDustItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyDecorationFragment :
    BaseFragment<FragmentMyDecorationBinding, HomeViewModel>(R.layout.fragment_my_decoration) {
    override val viewModel: HomeViewModel by activityViewModels()
    private val _myDecorationViewModel: MyDecorationViewModel by viewModels()
    private val spaceDustItemAdapter: SpaceDustItemAdapter by lazy {
        SpaceDustItemAdapter {
            _myDecorationViewModel.selectItem(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            myDecorationViewModel = _myDecorationViewModel
            rvSpaceDustItems.apply {
                adapter = spaceDustItemAdapter
                addItemDecoration(SpaceDustItemDecoration(context))
            }
        }
        _myDecorationViewModel.fetchSpaceDustItems()
    }

    companion object {
        fun newInstance(): Fragment {
            return MyDecorationFragment()
        }
    }
}