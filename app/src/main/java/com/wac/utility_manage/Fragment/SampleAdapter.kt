package com.wac.utility_manage.Fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SampleAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> FragmentWaterinvoice.newInstance()
        1 -> FragmentOtherinvoice.newInstance()
        else -> null
    }!!

    override fun getPageTitle(position: Int): CharSequence =
        when (position) {
            0 -> "ค่าน้ำประปา"
            1 -> "ค่าบริการอื่นๆ"
            else -> ""
        }

    override fun getCount(): Int = 2
}