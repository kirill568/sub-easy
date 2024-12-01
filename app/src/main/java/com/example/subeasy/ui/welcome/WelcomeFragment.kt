package com.example.subeasy.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.subeasy.R
import com.example.subeasy.databinding.FragmentWelcomeBinding
import com.example.subeasy.ui.home.HomeViewModel

class WelcomeFragment: Fragment(R.layout.fragment_welcome) {
    private var _binding: FragmentWelcomeBinding? = null

    private val userViewModel: UserViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val controller = findNavController()

        userViewModel.isUserCreated.observe(viewLifecycleOwner, Observer { isCreated ->
            if (isCreated) {
                controller.navigate(R.id.action_navigation_welcome_to_navigation_home2)
            } else {
                Toast.makeText(context, "Ошибка создания пользователя", Toast.LENGTH_SHORT).show()
            }
        })

        binding.getStartedButton.setOnClickListener{
            if (binding.firstName.text.isNullOrBlank() || binding.lastName.text.isNullOrBlank()) {
                Toast.makeText(activity, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                userViewModel.addUser(binding.firstName.text.toString(), binding.lastName.text.toString())
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}