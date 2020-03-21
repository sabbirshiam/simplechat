package com.gatech.fabbadgetest.presentation.viewutils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun addFragmentToActivity(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
    val transaction = fragmentManager.beginTransaction()
    transaction.add(frameId, fragment)
    transaction.commit()
}