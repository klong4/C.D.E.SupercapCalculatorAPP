@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.klong.cdesupercapcalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klong.cdesupercapcalculatorapp.ui.theme.CDESupercapCalculatorAPPTheme
import kotlin.math.pow
import kotlin.math.PI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            CDESupercapCalculatorAPPTheme {
                NavHost(navController, startDestination = "inputScreen") {
                    composable("inputScreen") { InputScreen(navController) }
                    composable("resultScreen/{resultString}") { backStackEntry ->
                        val resultString = backStackEntry.arguments?.getString("resultString") ?: "No results"
                        ResultScreen(resultString, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun InputScreen(navController: NavController) {
    var numCells by remember { mutableStateOf("") }
    var numCircuits by remember { mutableStateOf("") }
    var capacitanceOfOneCell by remember { mutableStateOf("") }
    var maxVoltageOneCell by remember { mutableStateOf("") }
    var minVoltageOfOneCell by remember { mutableStateOf("") }
    var currentDraw by remember { mutableStateOf("") }
    var massOneCell by remember { mutableStateOf("") }
    var volumeOfOneCellMm3 by remember { mutableStateOf("") }
    var diameterOfOneCell by remember { mutableStateOf("") }
    var heightOfOneCell by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalculatorTextField(label = "Number of Cells", value = numCells, onValueChange = { numCells = it })
        CalculatorTextField(label = "Number of Circuits", value = numCircuits, onValueChange = { numCircuits = it })
        CalculatorTextField(label = "Capacitance of One Cell", value = capacitanceOfOneCell, onValueChange = { capacitanceOfOneCell = it })
        CalculatorTextField(label = "Max Voltage of One Cell", value = maxVoltageOneCell, onValueChange = { maxVoltageOneCell = it })
        CalculatorTextField(label = "Min Voltage of One Cell", value = minVoltageOfOneCell, onValueChange = { minVoltageOfOneCell = it })
        CalculatorTextField(label = "Current Draw", value = currentDraw, onValueChange = { currentDraw = it })
        CalculatorTextField(label = "Mass of One Cell", value = massOneCell, onValueChange = { massOneCell = it })
        CalculatorTextField(label = "Volume of One Cell (mm3)", value = volumeOfOneCellMm3, onValueChange = { volumeOfOneCellMm3 = it })
        CalculatorTextField(label = "Diameter of One Cell", value = diameterOfOneCell, onValueChange = { diameterOfOneCell = it })
        CalculatorTextField(label = "Height of One Cell", value = heightOfOneCell, onValueChange = { heightOfOneCell = it })


        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Calculation logic
            try {
                val nCellsLocal = numCells.toFloat()
                val nCircuitsLocal = numCircuits.toFloat()
                val capOneCellLocal = capacitanceOfOneCell.toFloat()
                val maxVoltageOneCellLocal = maxVoltageOneCell.toFloat()
                val minVoltageOneCellLocal = minVoltageOfOneCell.toFloat()
                val currentDrawLocal = currentDraw.toFloat()
                val massOneCellLocal = massOneCell.toFloat()
                val volumeOfOneCellMm3Local: Float? = volumeOfOneCellMm3?.toFloatOrNull()
                val diameterOfOneCellLocal: Float? = diameterOfOneCell?.toFloatOrNull()
                val heightOfOneCellLocal: Float? = heightOfOneCell?.toFloatOrNull()

                val calculatedVolumeOfOneCellMm3Local: Float = volumeOfOneCellMm3Local ?: run {
                    val radius = diameterOfOneCellLocal!! / 2
                    val height = heightOfOneCellLocal!!
                    (PI * (radius.toDouble().pow(2)) * height).toFloat()
                }

                // Electrical calculations
                val totalCapacitanceLocal = (capOneCellLocal / nCellsLocal) * nCircuitsLocal
                val V0Local = maxVoltageOneCellLocal * nCellsLocal
                val V1Local = minVoltageOneCellLocal * nCellsLocal

                // Mechanical calculations
                val totalMassLocal = massOneCellLocal * nCellsLocal
                val totalVolumeMm3Local = calculatedVolumeOfOneCellMm3Local * nCellsLocal
                val totalVolumeMlLocal = totalVolumeMm3Local / 1000  // Convert mm3 to ml

                // Energy calculations
                val energyPotentialLocal = 0.5f * totalCapacitanceLocal * (V0Local.pow(2) - V1Local.pow(2))
                val energyDensityLocal = energyPotentialLocal / totalMassLocal
                val volumetricEnergyLocal = energyPotentialLocal / totalVolumeMlLocal

                // Charge calculations
                val chargeLocal = totalCapacitanceLocal * V0Local
                val charge_AhLocal = chargeLocal / 3600
                val charge_mAhLocal = charge_AhLocal * 1000

                // Power calculations
                val powerDensityLocal = energyDensityLocal * currentDrawLocal
                val volumetricPowerDensityLocal = volumetricEnergyLocal * currentDrawLocal
                val powerPotentialLocal = energyPotentialLocal * currentDrawLocal
                val powerPotential_mWhLocal = powerPotentialLocal * 1000

                // Constant Current Discharge Time calculations
                val dischargeTimeSecondsLocal = chargeLocal / currentDrawLocal
                val dischargeTimeMinutesLocal = dischargeTimeSecondsLocal / 60
                val dischargeTimeHoursLocal = dischargeTimeMinutesLocal / 60
                val dischargeTimeDaysLocal = dischargeTimeHoursLocal / 24

                // Create a result map
                val result = mapOf(
                    "total_capacitance" to totalCapacitanceLocal,
                    "V0" to V0Local,
                    "V1" to V1Local,
                    "total_mass" to totalMassLocal,
                    "total_volume_mm3" to totalVolumeMm3Local,
                    "total_volume_ml" to totalVolumeMlLocal,
                    "energy_potential" to energyPotentialLocal,
                    "energy_density" to energyDensityLocal,
                    "volumetric_energy" to volumetricEnergyLocal,
                    "charge" to chargeLocal,
                    "charge_Ah" to charge_AhLocal,
                    "charge_mAh" to charge_mAhLocal,
                    "power_density" to powerDensityLocal,
                    "volumetric_power_density" to volumetricPowerDensityLocal,
                    "power_potential" to powerPotentialLocal,
                    "power_potential_mWh" to powerPotential_mWhLocal,
                    "discharge_time_seconds" to dischargeTimeSecondsLocal,
                    "discharge_time_minutes" to dischargeTimeMinutesLocal,
                    "discharge_time_hours" to dischargeTimeHoursLocal,
                    "discharge_time_days" to dischargeTimeDaysLocal
                )

                // Navigate to the results screen and pass the data
                val resultString = result.entries.joinToString("\n") { "${it.key}: ${it.value}" }
                navController.navigate("resultScreen/$resultString")

            } catch (e: Exception) {
                // Handle the error, maybe navigate to an error screen or show a Snackbar
            }
        })
        {
            Text(text = "Calculate")
        }
    }
}

@Composable
fun ResultScreen(resultString: String, navController: NavController) {
    // Map to hold the units for each parameter
    val unitsMap = mapOf(
        "total_capacitance" to "F",
        "V0" to "V",
        "V1" to "V",
        "total_mass" to "g",
        "total_volume_mm3" to "mm³",
        "total_volume_ml" to "ml",
        "energy_potential" to "J",
        "energy_density" to "J/g",
        "volumetric_energy" to "J/ml",
        "charge" to "C",
        "charge_Ah" to "Ah",
        "charge_mAh" to "mAh",
        "power_density" to "W/g",
        "volumetric_power_density" to "W/ml",
        "power_potential" to "W",
        "power_potential_mWh" to "mWh",
        "discharge_time_seconds" to "s",
        "discharge_time_minutes" to "min",
        "discharge_time_hours" to "h",
        "discharge_time_days" to "d"
    )

    var isWhite = true  // For alternating row colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Convert the resultString back to a Map
        val resultMap = resultString.split("\n")
            .associate {
                val (key, value) = it.split(": ")
                key to String.format("%.5f", value.toFloat())
            }

        // Table Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Parameter", modifier = Modifier.weight(2f), style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "Value", modifier = Modifier.weight(1f), style = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
        }

        // Table Rows
        resultMap.forEach { (key, value) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isWhite) Color.White else Color.LightGray)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = key, modifier = Modifier.weight(2f))
                    // Append the unit, if available, and left-justify the text
                    Text(text = "$value ${unitsMap[key] ?: ""}", modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                }
            }
            isWhite = !isWhite  // Alternate color for the next row
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Navigate back to the input screen
            navController.navigate("inputScreen")
        }) {
            Text(text = "Back")
        }
    }
}


@Composable
fun CalculatorTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        textStyle = TextStyle(fontSize = 14.sp),  // Adjust the font size here for the text inside the TextField
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        maxLines = 1
    )
    Spacer(modifier = Modifier.height(8.dp))
}


