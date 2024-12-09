package com.example.subeasy.ui.subscriptionDetail

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.subeasy.R
import com.example.subeasy.databinding.FragmentSubscriptionDetailBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class SubscriptionDetail: Fragment(R.layout.fragment_subscription_detail) {
    private var _binding: FragmentSubscriptionDetailBinding? = null

    private val binding get() = _binding!!

    private val args: SubscriptionDetailArgs by navArgs()

    private val viewModel: SubscriptionDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentSubscriptionDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subscriptionId = args.subscriptionId

        viewModel.loadSubscription(subscriptionId)

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.content.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.subscriptionDetail.observe(viewLifecycleOwner) { detail ->
            binding.name.text = detail.service.name
            binding.totalAmount.text = "\u20bd${detail.subscription.calculateTotalAmount()}"
            binding.due.text = detail.subscription.getDue(requireContext())
            val startedOnDate = Date(detail.subscription.startedOn)
            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            binding.startedOn.setText( dateFormat.format(startedOnDate))
            binding.cycle.setText(detail.subscription.cycle.toString())
            binding.cost.setText(detail.subscription.cost.toString())
            binding.note.setText(detail.subscription.description)

            try {
                val assetManager = requireContext().assets
                val inputStream = assetManager.open("serviceIcons/" + detail.service.iconPath)
                val drawable = Drawable.createFromStream(inputStream, null)
                binding.subscriptionIcon.setImageDrawable(drawable)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        binding.removeSubscriptionButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(requireContext().getString(R.string.subscription_delete_confirm))
                .setPositiveButton(requireContext().getString(R.string.yes)) { _, _ ->
                    viewModel.deleteSubscription(viewModel.subscriptionDetail.value?.subscription ?: return@setPositiveButton, {
                        Toast.makeText(requireContext(), requireContext().getString(R.string.subscription_deleted), Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }, {
                        Toast.makeText(requireContext(), requireContext().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                    })
                }
                .setNegativeButton(requireContext().getString(R.string.no), null)
                .show()
        }
    }
}