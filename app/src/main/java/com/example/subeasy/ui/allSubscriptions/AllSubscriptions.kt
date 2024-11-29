package com.example.subeasy.ui.allSubscriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.subeasy.R
import com.example.subeasy.databinding.FragmentAllSubscriptionsBinding
import com.example.subeasy.databinding.FragmentHomeBinding
import com.example.subeasy.ui.home.HomeViewModel

class AllSubscriptions: Fragment(R.layout.fragment_all_subscriptions) {
    private var _binding: FragmentAllSubscriptionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentAllSubscriptionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}