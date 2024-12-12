package com.example.subeasy.ui.addSubscription

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.subeasy.R
import com.example.subeasy.data.local.entities.Cycle
import com.example.subeasy.data.local.entities.Remind
import com.example.subeasy.data.local.entities.Subscription
import com.example.subeasy.databinding.FragmentAddSubscriptionBinding
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class RecognitionResult(val text: String)

class AddSubscription: Fragment(R.layout.fragment_add_subscription) {
    private var _binding: FragmentAddSubscriptionBinding? = null

    private val binding get() = _binding!!

    private val args: AddSubscriptionArgs by navArgs()

    private val viewModel: AddSubscriptionViewModel by viewModels()

    private var selectedDateInMillis: Long = System.currentTimeMillis()

    private lateinit var pLauncher: ActivityResultLauncher<String>

    private var mediaRecorder: MediaRecorder? = null

    private var isRecording = false

    private var model: Model? = null;

    private lateinit var speechService: SpeechService;

    private lateinit var recognizer: Recognizer;

    private val audioFilePath: String
        get() = File(requireContext().filesDir, "text_to_speech.wav").absolutePath

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

        initModel()
        registerPermissionListener()
        checkMicroPermission()

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
                Toast.makeText(context, requireContext().getString(R.string.fill_required_fields), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, requireContext().getString(R.string.create_subscription_error), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        binding.textToSpeechButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
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
        println(binding.cost.text.toString())
        println(binding.cost.text.toString().replace(',', '.'))
        println(binding.cost.text.toString().replace(',', '.').toDoubleOrNull())
        println(DecimalFormat("#.##").format(
            (binding.cost.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0)
        ).replace(',', '.'))
        val costValue = DecimalFormat("#.##").format(
            (binding.cost.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0)
        ).replace(',', '.').toDouble()
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

    private fun checkMicroPermission() {
        when{
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED -> {
                enableTextToSpeechButton()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(requireContext(),requireContext().getString(R.string.allow_micro), Toast.LENGTH_LONG).show()
            }

            else -> {
                pLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun registerPermissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                enableTextToSpeechButton()
            }
        }
    }

    private fun startRecording() {
        Toast.makeText(requireContext(),requireContext().getString(R.string.start_record), Toast.LENGTH_LONG).show()
        this.recognizer = Recognizer(model, 16000.0f)
        speechService = SpeechService(recognizer, 16000.0f)

        val listener = object : RecognitionListener {
            override fun onPartialResult(partialResult: String) {
                Log.d("Vosk", "Partial result: $partialResult")
            }

            override fun onResult(result: String) {
                Log.d("Vosk", "Result text: $result")
                val result = Json.decodeFromString<RecognitionResult>(result)
                binding.note.append(" " + result.text)
                binding.note.setSelection(binding.note.text.length)
            }

            override fun onError(exception: Exception) {
                Log.e("Vosk", "Error recognition: ${exception.message}")
            }

            override fun onTimeout() {
                Log.d("Vosk", "Timeout")
            }

            override fun onFinalResult(hypothesis: String) {
                Log.d("Vosk", "Final result: $hypothesis")
            }
        }

        try {
            speechService.startListening(listener)
            isRecording = true
            binding.textToSpeechButton.text = requireContext().getString(R.string.stop_record)
            Log.d("Vosk", "Start recording")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Vosk", "Recording error: ${e.message}")
        }
    }

    private fun stopRecording() {
        speechService.stop()
        recognizer.close()
        Log.d("Vosk", "Recording stopped")
        isRecording = false
        binding.textToSpeechButton.text = requireContext().getString(R.string.enter_note_by_voice)
    }

    private fun enableTextToSpeechButton() {
        binding.textToSpeechButton.isEnabled = true
        binding.textToSpeechButton.alpha = 1.0f
    }

    private fun initModel() {
        val assetModelPath = "models/vosk-model-small-ru-0.22"
        val targetModelPath = "models"

        StorageService.unpack(
            requireContext(),
            assetModelPath,
            targetModelPath,
            { model: Model ->
                Log.d("Vosk", "Model successfully loaded")
                this.model = model
            },
            { error: IOException ->
                Log.e("Vosk", "Model loadgin error: ${error.message}")
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaRecorder?.release()
        mediaRecorder = null
        _binding = null
    }
}