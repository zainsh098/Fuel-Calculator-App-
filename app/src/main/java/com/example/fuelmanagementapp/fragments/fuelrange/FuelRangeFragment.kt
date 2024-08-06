package com.example.fuelmanagementapp.fragments.fuelrange

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.constants.Constants
import com.example.fuelmanagementapp.databinding.FragmentFuelRangeBinding

/**
 * A Fragment that handles fuel range calculations and displays the results.
 */
class FuelRangeFragment : Fragment() {

    private lateinit var binding: FragmentFuelRangeBinding
    private val spinnerItemsFuel = listOf("Litres (L)", "Gallon(US gal)", "Gallon(UK gal)")
    private val spinnerItemsFuelEfficiency = listOf("km/L", "L/100km", "US mpg", "UK mpg)")
    private val spinnerItemsFuelPrice = listOf("Litres (L)", "Gallon(US gal)", "Gallon(UK gal)")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFuelRangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            imageViewDrawerOpen.setOnClickListener {
                openCloseNavigationDrawer()
            }
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                com.example.fuelmanagementapp.R.id.nav_home -> {
                    // Handle Home item click
                    findNavController().navigate(com.example.fuelmanagementapp.R.id.nav_home)
                }
                com.example.fuelmanagementapp.R.id.nav_gallery -> {
                    // Handle Gallery item click
                    findNavController().navigate(com.example.fuelmanagementapp.R.id.nav_gallery)
                }
                com.example.fuelmanagementapp.R.id.nav_slideshow -> {
                    // Handle Slideshow item click
                    findNavController().navigate(com.example.fuelmanagementapp.R.id.nav_slideshow)
                }
            }
            // Close the drawer after an item is selected
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        setupSpinners()
        binding.buttonCalculate.setOnClickListener {
            calculateAndDisplayResult()
        }
    }

    /**
     * Opens or closes the navigation drawer based on its current state.
     */
    fun openCloseNavigationDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * Sets up the spinners with their respective items.
     */
    private fun setupSpinners() {
        val distanceAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerItemsFuel)
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerFuel.adapter = distanceAdapter

        val fuelEfficiencyAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerItemsFuelEfficiency)
        fuelEfficiencyAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerFuelEfficiency.adapter = fuelEfficiencyAdapter

        val fuelPriceAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerItemsFuelPrice)
        fuelPriceAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerFuelPrice.adapter = fuelPriceAdapter
    }

    /**
     * Calculates and displays the fuel range and total cost based on user inputs.
     * Handles any input errors and shows a toast message if input is invalid.
     */
    @SuppressLint("DefaultLocale")
    private fun calculateAndDisplayResult() {
        try {
            // Get user inputs
            val fuel = binding.EditTextTaskFuel.text.toString().toDouble()
            val fuelEfficiency = binding.EditTextTaskFuelEfficiency.text.toString().toDouble()
            val fuelPrice = binding.EditTextTaskFuelPrice.text.toString().toDouble()

            // Get selected units
            val fuelSpinner = binding.spinnerFuel.selectedItem.toString()
            val fuelSpinnerEfficiency = binding.spinnerFuelEfficiency.selectedItem.toString()
            val fuelSpinnerFuelPrice = binding.spinnerFuelPrice.selectedItem.toString()

            // Convert units to standard units
            val convertedFuel = convertFuel(fuel, fuelSpinner)
            val convertedFuelEfficiency =
                convertFuelEfficiency(fuelEfficiency, fuelSpinnerEfficiency)
            val convertedFuelPrice = convertFuelPrice(fuelPrice, fuelSpinnerFuelPrice)

            // Calculate total cost and fuel range
            val totalCost = convertedFuel * convertedFuelPrice
            val fuelRange = convertedFuel * convertedFuelEfficiency

            // Determine the display unit for fuel range
            val fuelRangeShow = if (fuelSpinnerFuelPrice == "Litres (L)") {
                fuelRange
            } else {
                fuelRange / Constants.KM_TO_MILES_CONVERSION // Convert km to miles
            }
            val rangeUnit = if (fuelSpinnerFuelPrice == "Litres (L)") "km" else "mi"

            // Display results
            binding.textViewFuelCost.text = String.format("%.2f", totalCost) + " PKR"
            binding.textViewFuelRange.text = String.format("%.2f", fuelRangeShow) + " " + rangeUnit
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error in input: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /**
     * Converts the given fuel quantity to liters based on the selected unit.
     *
     * @param fuelL The fuel quantity to convert.
     * @param unit The unit of the fuel quantity.
     * @return The fuel quantity converted to liters.
     */
    private fun convertFuel(fuelL: Double, unit: String): Double {
        return when (unit) {
            "Gallon(US gal)" -> fuelL * Constants.LITRE_TO_US_GALLON // Convert liters to US gallons
            "Gallon(UK gal)" -> fuelL * Constants.LITRE_TO_UK_GALLON // Convert liters to UK gallons
            else -> fuelL // Already in liters
        }
    }

    /**
     * Converts the given fuel efficiency to kilometers per liter based on the selected unit.
     *
     * @param fuelEfficiency The fuel efficiency to convert.
     * @param unit The unit of the fuel efficiency.
     * @return The fuel efficiency converted to kilometers per liter.
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
     * Converts the given fuel price to price per liter based on the selected unit.
     *
     * @param fuelPrice The fuel price to convert.
     * @param unit The unit of the fuel price.
     * @return The fuel price converted to price per liter.
     */
    private fun convertFuelPrice(fuelPrice: Double, unit: String): Double {
        return when (unit) {
            "Gallon(US gal)" -> fuelPrice / Constants.US_GALLON_TO_LITER // Convert price per US gallon to price per liter
            "Gallon(UK gal)" -> fuelPrice / Constants.UK_GALLON_TO_LITER // Convert price per UK gallon to price per liter
            else -> fuelPrice // Already in price per liter
        }
    }
}
