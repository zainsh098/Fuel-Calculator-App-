package com.example.fuelmanagementapp.fragments.fuelconsumption

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.constants.Constants
import com.example.fuelmanagementapp.R
import com.example.fuelmanagementapp.databinding.FragmentFuelConsumptionHomeBinding
import com.example.fuelmanagementapp.databinding.FragmentHomeBinding

class FuelConsumptionFragmentHome : Fragment(R.layout.fragment_home) {

    // Spinner items for distance, fuel efficiency, and fuel price units
    private val spinnerItems = listOf("Kilometers(Km)", "Miles(miles)")
    private val spinnerItemsFuelEfficiency = listOf("km/L", "L/100km", "US mpg", "UK mpg")
    private val spinnerItemsFuelPrice = listOf("Litre(L)", "Gallon(US gal)", "Gallon(UK gal)")

    // Variables to store calculated fuel consumption and total fuel cost
    private var fuelConsumed = 0.0
    private var totalFuelCost = 0.0

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        // Setup spinners with adapters
        setupSpinners()
        binding.apply {
            imageViewDrawerOpen.setOnClickListener {
                openCloseNavigationDrawer()
            }
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Handle Home item click
                    findNavController().navigate(R.id.nav_home)
                }
                R.id.nav_gallery -> {

                    // Handle Gallery item click
                    findNavController().navigate(R.id.nav_gallery)
                }
                R.id.nav_slideshow -> {
                    // Handle Slideshow item click
                    findNavController().navigate(R.id.nav_slideshow)
                }
            }
            // Close the drawer after an item is selected
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Set listener for the switch to calculate single or round trip based on its state
        binding.materialSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                calculateAndDisplayRoundTrip()
            } else {
                calculateAndDisplaySingleTrip()
            }
        }

        // Set listener for the calculate button to perform the calculation
        binding.buttonCalculate.setOnClickListener {
            if (binding.materialSwitch.isChecked) {
                calculateAndDisplayRoundTrip()
            } else {
                calculateAndDisplaySingleTrip()
            }
        }
    }

    fun openCloseNavigationDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * Sets up the spinners with the appropriate adapters and data.
     */
    private fun setupSpinners() {
        val distanceAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinner.adapter = distanceAdapter

        val fuelEfficiencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItemsFuelEfficiency
        )
        fuelEfficiencyAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerFuelEfficiency.adapter = fuelEfficiencyAdapter

        val fuelPriceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItemsFuelPrice
        )
        fuelPriceAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerFuelPrice.adapter = fuelPriceAdapter
    }

    /**
     * Calculates and displays the fuel cost and consumption for a single trip.
     */
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun calculateAndDisplaySingleTrip() {
        try {

            binding.apply {
                val distance = this.EditTextTaskDistance.text.toString().toDouble()
                val fuelEfficiency = this.EditTextFuelEfficiency.text.toString().toDouble()
                val fuelPrice = this.EditTextFuelPrice.text.toString().toDouble()

                val distanceUnit = this.spinner.selectedItem.toString()
                val fuelEfficiencyUnit = this.spinnerFuelEfficiency.selectedItem.toString()
                val fuelPriceUnit = this.spinnerFuelPrice.selectedItem.toString()

                val convertedDistance = convertDistance(distance, distanceUnit)
                val convertedFuelEfficiency =
                    convertFuelEfficiency(fuelEfficiency, fuelEfficiencyUnit)
                val convertedFuelPricePerLiter =
                    convertFuelPriceToPricePerLiter(fuelPrice, fuelPriceUnit)

                calculateFuelCostAndConsumption(
                    convertedDistance,
                    convertedFuelEfficiency,
                    convertedFuelPricePerLiter,
                    fuelPriceUnit
                ).let {
                    binding.textViewCost.text = String.format("%.2f", totalFuelCost) + " PKR"
                    binding.textViewFuelConsumed.text = String.format(
                        "%.2f",
                        fuelConsumed
                    ) + " " + if (fuelPriceUnit == "Litre(L)") "L" else if (fuelPriceUnit == "Gallon(US gal)") "US gal" else "UK gal"
                }
            }
        } catch (e: NumberFormatException) {
            // Show error message if input is invalid
            Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Calculates and displays the fuel cost and consumption for a round trip.
     */
    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun calculateAndDisplayRoundTrip() {
        try {
            // Get user inputs
            val distance = binding.EditTextTaskDistance.text.toString().toDouble()
            val fuelEfficiency = binding.EditTextFuelEfficiency.text.toString().toDouble()
            val fuelPrice = binding.EditTextFuelPrice.text.toString().toDouble()

            // Get selected units
            val distanceUnit = binding.spinner.selectedItem.toString()
            val fuelEfficiencyUnit = binding.spinnerFuelEfficiency.selectedItem.toString()
            val fuelPriceUnit = binding.spinnerFuelPrice.selectedItem.toString()

            // Convert units to standard units
            val convertedDistance = convertDistance(distance, distanceUnit)
            val convertedFuelEfficiency = convertFuelEfficiency(fuelEfficiency, fuelEfficiencyUnit)
            val convertedFuelPricePerLiter =
                convertFuelPriceToPricePerLiter(fuelPrice, fuelPriceUnit)
            val roundTripDistance = convertedDistance * 2

            // Calculate fuel cost and consumption
            calculateFuelCostAndConsumption(
                roundTripDistance,
                convertedFuelEfficiency,
                convertedFuelPricePerLiter,
                fuelPriceUnit
            )

            // Display results
            binding.textViewCost.text = String.format("%.2f", totalFuelCost) + " PKR"
            binding.textViewFuelConsumed.text = String.format(
                "%.2f",
                fuelConsumed
            ) + " " + if (fuelPriceUnit == "Litre(L)") "L" else if (fuelPriceUnit == "Gallon(US gal)") "US gal" else "UK gal"

        } catch (e: NumberFormatException) {
            // Show error message if input is invalid
            Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Converts the distance to kilometers based on the selected unit.
     * @param distance The distance value to convert.
     * @param unit The unit of the distance value.
     * @return The converted distance in kilometers.
     */
    private fun convertDistance(distance: Double, unit: String): Double {
        return when (unit) {
            "Miles(miles)" -> distance * Constants.KM_TO_MILES // Convert miles to kilometers
            else -> distance // Already in kilometers
        }
    }

    /**
     * Converts the fuel efficiency to km/L based on the selected unit.
     * @param fuelEfficiency The fuel efficiency value to convert.
     * @param unit The unit of the fuel efficiency value.
     * @return The converted fuel efficiency in km/L.
     */
    private fun convertFuelEfficiency(fuelEfficiency: Double, unit: String): Double {
        return when (unit) {
            "L/100km" -> 100 / fuelEfficiency // Convert L/100km to km/L
            "US mpg" -> fuelEfficiency * Constants.USMPG_TO_KML // Convert US mpg to km/L
            "UK mpg" -> fuelEfficiency * Constants.UKMPG_TO_KML // Convert UK mpg to km/L
            else -> fuelEfficiency // Already in km/L
        }
    }

    /**
     * Converts the fuel price to price per liter based on the selected unit.
     * @param fuelPrice The fuel price value to convert.
     * @param unit The unit of the fuel price value.
     * @return The converted fuel price per liter.
     */
    private fun convertFuelPriceToPricePerLiter(fuelPrice: Double, unit: String): Double {
        return when (unit) {
            "Gallon(US gal)" -> fuelPrice / Constants.US_GALLON_TO_LITER // Convert US gallons to price per liter
            "Gallon(UK gal)" -> fuelPrice / Constants.UK_GALLON_TO_LITER // Convert UK gallons to price per liter
            else -> fuelPrice // Already in price per liter
        }
    }

    /**
     * Calculates the total fuel cost and fuel consumption based on the converted values.
     * @param distance The distance of the trip in kilometers.
     * @param fuelEfficiency The fuel efficiency in km/L.
     * @param fuelPricePerLiter The fuel price per liter.
     * @param fuelPriceUnit The unit of the fuel price.
     */
    private fun calculateFuelCostAndConsumption(
        distance: Double,
        fuelEfficiency: Double,
        fuelPricePerLiter: Double,
        fuelPriceUnit: String
    ) {
        if (fuelEfficiency == 0.0) {
            Toast.makeText(context, "Please enter a valid fuel efficiency", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Calculate the fuel needed for the trip in liters
        val fuelConsumedInLiters = distance / fuelEfficiency

        // Convert fuel consumption to the required unit if needed
        fuelConsumed = when (fuelPriceUnit) {
            "Gallon(US gal)" -> convertLitersToUsGallons(fuelConsumedInLiters)
            "Gallon(UK gal)" -> convertLitersToUkGallons(fuelConsumedInLiters)
            else -> fuelConsumedInLiters // Already in liters
        }

        // Calculate the total fuel cost
        totalFuelCost = fuelConsumedInLiters * fuelPricePerLiter
    }

    /**
     * Converts liters to US gallons.
     * @param liters The volume in liters to convert.
     * @return The converted volume in US gallons.
     */
    private fun convertLitersToUsGallons(liters: Double): Double {
        return liters / Constants.US_GALLON_TO_LITER // Convert liters to US gallons
    }

    /**
     * Converts liters to UK gallons.
     * @param liters The volume in liters to convert.
     * @return The converted volume in UK gallons.
     */
    private fun convertLitersToUkGallons(liters: Double): Double {
        return liters / Constants.UK_GALLON_TO_LITER // Convert liters to UK gallons
    }


}
