package com.quicksolveplus.covid.covid_tracking.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.quicksolveplus.covid.covid_tracking.booster.BoosterFragment
import com.quicksolveplus.covid.covid_tracking.vaccination.VaccinationFragment


class CovidPagerAdapter(fm: FragmentManager,var totalTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return VaccinationFragment()
            }
            1 -> {
                return BoosterFragment()
            }
        }
        throw IllegalStateException("position $position is invalid for this viewpager")
    }
    override fun getCount(): Int {
        return totalTabs
    }

}
