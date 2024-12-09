package com.example.subeasy.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.subeasy.R
import com.example.subeasy.databinding.FragmentSettingsBinding
import java.io.File

class SettingsFragment: Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadUser()

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    saveImageToInternalStorage(selectedImageUri)
                } else {
                    Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // отображение аватарки при открытии фрагмента
        val file = File(requireContext().filesDir, "user_avatar")
        if (file.exists()) {
            binding.avatar.setImageURI(Uri.fromFile(file))
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.firstName.setText(user?.firstName)
            binding.lastName.setText(user?.lastName)
        }

        viewModel.isUpdateEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.updateButton.isEnabled = isEnabled
            binding.updateButton.alpha = if (isEnabled) 1.0f else 0.5f
        }

        binding.firstName.addTextChangedListener { text ->
            viewModel.firstName.value = text?.toString()
        }

        binding.lastName.addTextChangedListener { text ->
            viewModel.lastName.value = text?.toString()
        }

        binding.avatar.setOnClickListener {
            openImagePicker()
        }

        binding.updateButton.setOnClickListener {
            viewModel.saveChanges {
                Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
                binding.updateButton.isEnabled = false
                binding.updateButton.alpha = 0.5f

            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickImageLauncher.launch(intent)
    }

    private fun saveImageToInternalStorage(imageUri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val file = File(requireContext().filesDir, "user_avatar")
            val outputStream = file.outputStream()

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            binding.avatar.setImageURI(Uri.fromFile(file))
            Toast.makeText(requireContext(), "Аватар обновлен", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Что-то пошло не так", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}