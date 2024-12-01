package com.example.subeasy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.subeasy.R
import com.example.subeasy.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var subscriptionAdapter: SubscriptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscriptionAdapter = SubscriptionAdapter()
        binding.mySubscriptions.adapter = subscriptionAdapter
        binding.mySubscriptions.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.fetchData()

        homeViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.userName.text = user?.let { "${it.firstName} ${it.lastName}" } ?: "Гость"
        }

        homeViewModel.subscriptionsWithServices.observe(viewLifecycleOwner) { subscriptionsWithServices ->
            subscriptionAdapter.submitList(subscriptionsWithServices)
        }

        homeViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.emptyState.observe(viewLifecycleOwner) { isEmpty ->
            binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }

        homeViewModel.totalCostForCurrentMonth.observe(viewLifecycleOwner) { totalCost ->
            binding.totalAmount.text = totalCost.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}