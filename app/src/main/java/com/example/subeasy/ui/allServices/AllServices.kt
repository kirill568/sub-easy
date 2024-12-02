package com.example.subeasy.ui.allServices

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.subeasy.R
import com.example.subeasy.databinding.FragmentAllServicesBinding

class AllServices: Fragment(R.layout.fragment_all_services) {
    private var _binding: FragmentAllServicesBinding? = null

    private val binding get() = _binding!!

    private val viewModel: AllServicesViewModel by viewModels()

    private val adapter: ServiceAdapter = ServiceAdapter()

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

        // Наблюдаем за всеми сервисами
        viewModel.filteredServices.observe(viewLifecycleOwner) { services ->
            adapter.submitList(services)
        }

        // Отображаем все сервисы при загрузке
        viewModel.allServices.observe(viewLifecycleOwner) { services ->
            viewModel.filterServices("") // Обновляем фильтр
        }

        // Добавляем обработку поиска
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterServices(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}