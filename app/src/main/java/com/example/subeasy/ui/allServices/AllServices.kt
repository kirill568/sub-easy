package com.example.subeasy.ui.allServices

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.subeasy.R
import com.example.subeasy.data.local.entities.Service
import com.example.subeasy.databinding.FragmentAllServicesBinding

class AllServices: Fragment(R.layout.fragment_all_services), OnItemClickListener {
    private var _binding: FragmentAllServicesBinding? = null

    private val binding get() = _binding!!

    private val viewModel: AllServicesViewModel by viewModels()

    private val adapter: ServiceAdapter = ServiceAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentAllServicesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.allServices.layoutManager = LinearLayoutManager(requireContext())
        binding.allServices.adapter = adapter

        viewModel.filteredServices.observe(viewLifecycleOwner) { services ->
            adapter.submitList(services)
        }

        viewModel.allServices.observe(viewLifecycleOwner) { services ->
            viewModel.filterServices("")
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterServices(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.customSubscription.setOnClickListener {
            val controller = findNavController()
            val action = AllServicesDirections.actionAllSubscriptionsToAddSubscription(-1)
            controller.navigate(action)
        }
    }

    override fun onItemClick(service: Service) {
        val controller = findNavController()
        val action = AllServicesDirections.actionAllSubscriptionsToAddSubscription(service.id)
        controller.navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}