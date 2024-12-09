package com.example.subeasy.ui.addSubscription

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.subeasy.R
import com.example.subeasy.data.local.entities.Cycle
import com.example.subeasy.data.local.entities.Remind
import com.example.subeasy.data.local.entities.Service
import com.example.subeasy.data.local.entities.Subscription
import com.example.subeasy.databinding.FragmentAddSubscriptionBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.lifecycle.Observer
import java.text.DecimalFormat

class AddSubscription: Fragment(R.layout.fragment_add_subscription) {
    private var _binding: FragmentAddSubscriptionBinding? = null

    private val binding get() = _binding!!

    private val args: AddSubscriptionArgs by navArgs()

    private val viewModel: AddSubscriptionViewModel by viewModels()

    private var selectedDateInMillis: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentAddSubscriptionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.serviceId == -1) {
            binding.customServiceNameLabel.append(" *")
        }
        binding.startedOnLabel.append(" *")
        binding.cycleLabel.append(" *")
        binding.costLabel.append(" *")

        if (args.serviceId == -1) {
            binding.customSubscriptionBlock.visibility = View.VISIBLE
            binding.existSubscriptionBlock.visibility = View.GONE
        } else {
            binding.customSubscriptionBlock.visibility = View.GONE
            binding.existSubscriptionBlock.visibility = View.VISIBLE

            viewModel.getServiceById(args.serviceId) { service ->
                if (service != null) {
                    try {
                        val assetManager = context?.assets
                        val inputStream = assetManager?.open("serviceIcons/" + service.iconPath)
                        val drawable = Drawable.createFromStream(inputStream, null)
                        binding.serviceIcon.setImageDrawable(drawable)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    binding.serviceName.text = service.name
                }
            }
        }

        binding.startedOn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val date = Calendar.getInstance()
                    date.set(year, month, dayOfMonth)
                    binding.startedOn.setText(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date.time))

                    selectedDateInMillis = date.timeInMillis
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.cost.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return

                isEditing = true
                s?.let {
                    val input = it.toString()
                    // Убираем все лишние символы, оставляя только цифры и точку
                    val cleanString = input.replace("[^\\d.]".toRegex(), "")

                    // Форматируем строку в формат ###.##
                    val parts = cleanString.split(".")
                    val formatted = when {
                        parts.size == 1 -> parts[0] // Только целая часть
                        parts.size > 1 -> {
                            val whole = parts[0]
                            val decimal = parts[1].take(2) // Ограничиваем до 2 знаков после точки
                            "$whole.$decimal"
                        }
                        else -> cleanString
                    }

                    binding.cost.setText(formatted)
                    binding.cost.setSelection(formatted.length) // Устанавливаем курсор в конец
                }
                isEditing = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setupCycleSpinner()

        binding.addSubscriptionButton.setOnClickListener {
            if (
                    binding.cost.text.toString().toDoubleOrNull() == null ||
                    binding.startedOn.text.toString().isNullOrEmpty() ||
                    binding.cycle.selectedItem?.toString() == null ||
                    (args.serviceId == -1 && binding.customServiceName.text.isNullOrEmpty())
                )
            {
                Toast.makeText(context, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
            } else {
                val subscription = createSubscription()
                if (args.serviceId == -1) {
                    viewModel.addServiceAndSubscription(binding.customServiceName.text.toString(), subscription)
                } else {
                    viewModel.addSubscription(subscription)
                }

                viewModel.isSubscriptionCreated.observe(viewLifecycleOwner, Observer { isCreated ->
                    if (isCreated) {
                        findNavController().navigate(R.id.action_navigation_add_subscription_to_navigation_home)
                    } else {
                        Toast.makeText(context, "Ошибка создания подписки", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun setupCycleSpinner() {
        val cycleAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item,
            Cycle.entries.toTypedArray()
        )
        cycleAdapter.setDropDownViewResource(R.layout.spinner_item)
        binding.cycle.adapter = cycleAdapter
    }

    private fun createSubscription(): Subscription {
        val cycle = binding.cycle.selectedItem as Cycle
        val remind = Remind.NEVER
        val costValue = DecimalFormat("#.##").format(binding.cost.text.toString().toDoubleOrNull() ?: 0.0).toDouble()
        val noteText = binding.note.text.toString()

        return Subscription(
            serviceId = if (args.serviceId != -1) args.serviceId else 0,
            startedOn = selectedDateInMillis,
            cycle = cycle,
            remind = remind,
            cost = costValue,
            description = noteText,
            isActive = true
        )
    }
}