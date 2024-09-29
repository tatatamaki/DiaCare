package com.thirdgroup.diabetictracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StepCounterViewModel : ViewModel() {
    private val _steps: MutableStateFlow<Long> = MutableStateFlow(0)
    val steps: StateFlow<Long> = _steps

    fun updateSteps(newSteps: Long) {
        _steps.value = newSteps
    }
}

class StepCounterActivity : ComponentActivity(), SensorEventListener {
    private val viewModel: StepCounterViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StepCounterScreen(viewModel)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        Log.d("StepCounterActivity", "Step sensor initialized as TYPE_STEP_COUNTER")

        if (stepSensor == null) {
            Log.e("StepCounterActivity", "Step sensor not available")
        }
    }

    override fun onResume() {
        super.onResume()
        registerSensorListener()
    }

    override fun onPause() {
        super.onPause()
        unregisterSensorListener()
    }

    private fun registerSensorListener() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun unregisterSensorListener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0].toLong()
            viewModel.updateSteps(steps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Optional: Handle accuracy changes
    }
}

@Composable
fun StepCounterScreen(viewModel: StepCounterViewModel) {
    val steps by viewModel.steps.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                progress = steps.toFloat() / 10000,
                modifier = Modifier.size(100.dp),
                strokeWidth = 10.dp,
                color = MaterialTheme.colors.primary
            )
            Text(
                text = "Steps Taken: $steps",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}


/**
class StepCounterViewModel : ViewModel() {
    private val _currentScreenState: MutableStateFlow<StepView> = MutableStateFlow(StepView.Loading)
    val currentScreenState: StateFlow<StepView> = _currentScreenState



    // Method to set steps and daily goal
    fun setSteps(steps: Long, dailyGoal: Long = 10000) {
        _currentScreenState.value = StepView.ViewContent(steps + 1, dailyGoal)
        Log.i("StepCounterViewModel", "Updated screen state to ViewContent with steps: $steps")
    }




    // Method to set error state
    fun setError() {
        _currentScreenState.value = StepView.Error
        Log.i("StepCounterViewModel", "Updated screen state to Error")
    }
}



class StepCounterActivity : AppCompatActivity(), SensorEventListener {
    private val viewModel: StepCounterViewModel by viewModels()
    private val sensorManager: SensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var stepSensor: Sensor? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize sensor
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Permission handling
        if (checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), REQUEST_ACTIVITY_RECOGNITION)
        } else {
            registerStepSensorListener()
        }

        setContent {

        }
    }

    private fun registerStepSensorListener() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)

        } ?: run {
            viewModel.setError() // Show error if sensor is not available
            Log.d("StepCounterActivity", "Step sensor not available")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ACTIVITY_RECOGNITION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("StepCounterActivity", "Permission granted")
            registerStepSensorListener() // Retry registering the sensor
        } else {
            Log.d("StepCounterActivity", "Permission denied")
            viewModel.setError() // Handle permission denied
        }
    }



    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        registerStepSensorListener()
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let { event ->
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val steps = event.values[0].toLong()
                Log.d("StepCounterActivity", "Step count: $steps")
                viewModel.setSteps(steps)
            } else {
                Log.d("StepCounterActivity", "Other sensor detected")
            }
        } ?: Log.d("StepCounterActivity", "SensorEvent is null")
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if necessary
    }

    companion object {
        private const val REQUEST_ACTIVITY_RECOGNITION = 1001
    }
}

sealed class StepView {
    data object Loading : StepView()
    data class ViewContent(val steps: Long, val dailyGoal: Long) : StepView()
    data object Error : StepView()
}

@Composable
fun StepCounterView(state: StepView) {
    Log.i("StepCounterView", "Current state: $state")

    Log.i("StepCounterView", "Current state: $state")

    when (state) {
        is StepView.Loading -> {
            Log.d("StepCounterView", "Loading state")
            LoadingView()
        }
        is StepView.ViewContent -> {
            ContentView(steps = state.steps, dailyGoal = state.dailyGoal)
            Log.d("StepCounterView", "ViewContent state with steps")
        }
        is StepView.Error -> {
            Log.d("StepCounterView", "Error state")
            ErrorView()
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ContentView(steps: Long, dailyGoal: Long) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Steps Taken",
            style = MaterialTheme.typography.h4
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$steps / $dailyGoal",
            fontSize = 32.sp,
            style = MaterialTheme.typography.h3
        )
    }
}

@Composable
fun ErrorView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Step counter sensor is not present on this device", color = MaterialTheme.colors.error)
    }
}
@Composable
fun StepCounterScreen(viewModel: StepCounterViewModel) {
    val state by viewModel.currentScreenState.collectAsState()
    StepCounterView(state = state)
}
**/

