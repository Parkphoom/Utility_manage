package com.wac.utility_manage.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.utility_manage.R


class FragmentOtherinvoice : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_otherinvoice, container, false)

        return root
    }

    companion object {
        fun newInstance(): FragmentOtherinvoice = FragmentOtherinvoice()
    }
}
