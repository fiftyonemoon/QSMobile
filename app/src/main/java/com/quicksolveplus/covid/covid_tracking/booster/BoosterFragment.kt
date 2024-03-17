package com.quicksolveplus.covid.covid_tracking.booster

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.quicksolveplus.api.Api
import com.quicksolveplus.api.ResponseStatus
import com.quicksolveplus.covid.covid_tracking.CovidTrackingActivity
import com.quicksolveplus.covid.covid_tracking.adapter.BoosterAdapter
import com.quicksolveplus.covid.covid_tracking.booster.add_booster.AddBoosterActivity
import com.quicksolveplus.covid.covid_tracking.models.CovidItem
import com.quicksolveplus.covid.covid_tracking.viewmodel.CovidViewModel
import com.quicksolveplus.modifiers.Actifiers.openActivity
import com.quicksolveplus.modifiers.Actifiers.saveBitmapToCached
import com.quicksolveplus.qspmobile.databinding.FragmentBoosterBinding
import com.quicksolveplus.utils.dismissQSProgress
import com.quicksolveplus.utils.showQSProgress
import com.quicksolveplus.utils.toast
import okhttp3.ResponseBody


class BoosterFragment() : Fragment() {

    lateinit var binding: FragmentBoosterBinding
    private var boosterAdapter: BoosterAdapter? = null
    private var boosterList: ArrayList<CovidItem> = arrayListOf()
    private val viewModel by lazy {
        ViewModelProvider(this)[CovidViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBoosterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setObservers()
    }


    private fun setObservers() {
        viewModel.responseStatus().observe(requireActivity()) {
            when (it) {
                is ResponseStatus.Running -> {
                    if (it.apiName != Api.getImageFile) {
                        showQSProgress(requireActivity())
                    }
                }
                is ResponseStatus.Success -> {
                    proceedResponse(it)
                    dismissQSProgress()
                }
                is ResponseStatus.Failed -> {
                    toast(requireActivity(), it.msg)
                    dismissQSProgress()
                }
                else -> {
                    toast(requireActivity(), "Something goes wrong")
                    dismissQSProgress()
                }
            }
        }
    }

    private fun proceedResponse(success: ResponseStatus.Success) {
        when (success.apiName) {
            Api.getImageFile -> {
                if (success.data is ResponseBody) {
                    if (success.other is Pair<*, *>) {
                        val array = success.data.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                        val position = success.other.first as Int
                        val client = success.other.second as String
                        if (bitmap!=null) {
                            requireActivity().saveBitmapToCached(client, bitmap)
                        }
                        boosterAdapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    private fun initUI() {

        boosterList = CovidTrackingActivity.boosterList

        binding.apply {

            if (boosterList.size == 0 || boosterList.isEmpty()) {
                rvBoosterDose.isVisible = false
                tvNoBooster.isVisible = true
            } else {
                setBoosterAdapter()
            }

            clAddBooster.setOnClickListener {
                requireActivity().openActivity(AddBoosterActivity::class.java)
            }
        }

    }


    private var onClickListener = View.OnClickListener {
        when (it.id) {

        }
    }

    private fun setBoosterAdapter() {

        binding.apply {
            boosterAdapter =
                BoosterAdapter(requireContext(), boosterList,viewModel) { boosterModel ->

                }
            binding.rvBoosterDose.adapter = boosterAdapter
        }

    }

}