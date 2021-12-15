package com.anonymous.appilogue.features.profile

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.anonymous.appilogue.R
import com.anonymous.appilogue.databinding.FragmentProfileBinding
import com.anonymous.appilogue.features.base.BaseFragment
import com.anonymous.appilogue.features.home.SpaceAnimator
import com.anonymous.appilogue.features.main.MainActivity
import com.anonymous.appilogue.features.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding, ProfileViewModel>(R.layout.fragment_profile) {
    override val viewModel: ProfileViewModel by viewModels()
    private val _mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind {
            mainViewModel = _mainViewModel
            profileViewModel = viewModel
        }
        with(binding) {
            SpaceAnimator.animateSpace(ivSpace)
            tvSettingAccount.setOnClickListener {
                (activity as MainActivity).navigateTo(R.id.profileSettingFragment)
            }
            tvRevise.setOnClickListener {
                childFragmentManager.commit {
                    add<NicknameEditFragment>(R.id.fcv_profile)
                    setReorderingAllowed(true)
                }
            }
        }
        fetchApplicationVersion()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMyReviewCount()
    }

    private fun fetchApplicationVersion() {
        try {
            val packageInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName, 0
            )
            binding.tvVersionValue.text = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.d(e)
            binding.tvVersionValue.text = getString(R.string.profile_not_found_version)
        }
    }

}