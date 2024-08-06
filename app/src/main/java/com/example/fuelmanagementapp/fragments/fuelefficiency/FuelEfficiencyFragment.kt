package com.example.fuelmanagementapp.fragments.fuelefficiency

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
import com.example.fuelmanagementapp.R
import com.example.fuelmanagementapp.databinding.FragmentFuelEfficiencyBinding

/**
 * A Fragment that handles fuel efficiency calculations and displays the results.
 */
class FuelEfficiencyFragment : Fragment() {

    private lateinit var binding: FragmentFuelEfficiencyBinding
    private val spinnerItems = listOf("Kilometers (km)", "Miles (miles)")
    private val spinnerItemsFuelConsumed =
        listOf("Litres (L)", "Gallon (US gal)", "Gallon (UK gal)")
    private val spinnerItemsFuelPrice = listOf("Litres (L)", "Gallon (US gal)", "Gallon (UK gal)")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFuelEfficiencyBinding.inflate(inflater, container, false)
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
        setupSpinners()

        binding.buttonCalculateEff.setOnClickListener {
            calculateAndDisplayResultSingleTrip()
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
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerEFF.adapter = distanceAdapter

        val fuelConsumedAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItemsFuelConsumed
        )
        fuelConsumedAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerFuelConsEFF.adapter = fuelConsumedAdapter

        val fuelPriceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItemsFuelPrice
        )
        fuelPriceAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        binding.spinnerFuelPriceEFF.adapter = fuelPriceAdapter
    }

    /**
     * Calculates and displays the fuel efficiency and cost for a single trip.
     * Handles any input errors and shows a toast message if input is invalid.
     */
    @SuppressLint("DefaultLocale")
    private fun calculateAndDisplayResultSingleTrip() {
        try {
            binding.apply {
                // Get user inputs
                val distance = EditTextTaskFuelDist.text.toString().toDouble()
                val fuelConsumed = EditTextTaskFuelConsum.text.toString().toDouble()
                val fuelPrice = EditTextTaskFuelPriceEff.text.toString().toDouble()

                // Get selected units
                val distanceUnit = spinnerEFF.selectedItem.toString()
                val fuelConsumedUnit = spinnerFuelConsEFF.selectedItem.toString()
                val fuelPriceUnit = spinnerFuelPriceEFF.selectedItem.toString()

                // Convert units to standard units
                val convertedDistance = convertDistance(distance, distanceUnit)
                val convertedFuelConsumed = convertFuelConsumed(fuelConsumed, fuelConsumedUnit)

                // Calculate fuel efficiency and cost
                val fuelEfficiencyKmPerGal =
                    calculateFuelEfficiencyKmPerGal(convertedDistance, convertedFuelConsumed)
                val fuelEfficiencyUSMPG = convertKmPerGalToUSMPG(fuelEfficiencyKmPerGal)
                val fuelEfficiencyKmPerLiter =
                    calculateFuelEfficiencyKmPerLiter(convertedDistance, convertedFuelConsumed)
                val totalCost = calculateTotalCost(convertedFuelConsumed, fuelPrice, fuelPriceUnit)

                // Display fuel efficiency based on the selected unit
                val fuelEfficiencyDisplay = if (fuelPriceUnit == "Litres (L)") {
                    String.format("%.2f km/L", fuelEfficiencyKmPerLiter)
                } else if (fuelPriceUnit == "Gallon (US gal)") {
                    String.format("%.2f US MPG", fuelEfficiencyKmPerGal, fuelEfficiencyUSMPG)
                } else if (fuelPriceUnit == "Gallon (UK gal)") {
                    val fuelEfficiencyUKMPG = convertKmPerGalToUKMPG(fuelEfficiencyKmPerGal)
                    String.format("%.2f UK MPG", fuelEfficiencyKmPerGal, fuelEfficiencyUKMPG)
                } else {
                    ""

                }

                // Display results
                textViewFuelEffCost.text = String.format("%.2f PKR", totalCost)
                textViewFuelEffPrice.text = fuelEfficiencyDisplay
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Converts the given distance to kilometers based on the selected unit.
     *
     * @param distance The distance to convert.
     * @param unit The unit of the distance.
     * @return The distance converted to kilometers.
     */
    private fun convertDistance(distance: Double, unit: String): Double {
        return when (unit) {
            "Miles (miles)" -> distance * Constants.KM_TO_MILES // Convert miles to kilometers
            else -> distance // Already in kilometers
        }
    }

    /**
     * Converts the given fuel consumed to liters based on the selected unit.
     *
     * @param fuelConsumed The fuel consumed to convert.
     * @param unit The unit of the fuel consumed.
     * @return The fuel consumed converted to liters.
     */
    private fun convertFuelConsumed(fuelConsumed: Double, unit: String): Double {
        return when (unit) {
            "Gallon (US gal)" -> fuelConsumed * Constants.US_GALLON_TO_LITER // Convert US gallons to liters
            "Gallon (UK gal)" -> fuelConsumed * Constants.UK_GALLON_TO_LITER // Convert UK gallons to liters
            else -> fuelConsumed // Already in liters
        }
    }

    /**
     * Calculates the fuel efficiency in kilometers per US gallon.
     *
     * @param distance The distance traveled in kilometers.
     * @param fuelConsumed The fuel consumed in liters.
     * @return The fuel efficiency in kilometers per US gallon.
     */
    private fun calculateFuelEfficiencyKmPerGal(distance: Double, fuelConsumed: Double): Double {
        val fuelConsumedInGallons = fuelConsumed * Constants.LITRE_TO_US_GALLON // Convert liters to US gallons
        return distance / fuelConsumedInGallons // km per US gallon
    }

    /**
     * Converts the fuel efficiency from kilometers per US gallon to miles per US gallon.
     *
     * @param fuelEfficiencyKmPerGal The fuel efficiency in kilometers per US gallon.
     * @return The fuel efficiency in miles per US gallon.
     */
    private fun convertKmPerGalToUSMPG(fuelEfficiencyKmPerGal: Double): Double {
        return fuelEfficiencyKmPerGal * Constants.MILES_TO_KM // Convert km per US gallon to miles per US gallon
    }

    /**
     * Calculates the fuel efficiency in kilometers per liter.
     *
     * @param distance The distance traveled in kilometers.
     * @param fuelConsumed The fuel consumed in liters.
     * @return The fuel efficiency in kilometers per liter.
     */
    private fun calculateFuelEfficiencyKmPerLiter(distance: Double, fuelConsumed: Double): Double {
        return distance / fuelConsumed // km per liter
    }

    /**
     * Converts the fuel efficiency from kilometers per US gallon to miles per UK gallon.
     *
     * @param fuelEfficiencyKmPerGal The fuel efficiency in kilometers per US gallon.
     * @return The fuel efficiency in miles per UK gallon.
     */
    private fun convertKmPerGalToUKMPG(fuelEfficiencyKmPerGal: Double): Double {
        return fuelEfficiencyKmPerGal * Constants.UKMPG // Convert km per UK gallon to miles per UK gallon
    }

    /**
     * Calculates the total cost of fuel based on the fuel consumed and fuel price.
     *
     * @param fuelConsumed The fuel consumed in liters.
     * @param fuelPrice The price of fuel.
     * @param unit The unit of the fuel price.
     * @return The total cost of fuel.
     */
    private fun calculateTotalCost(fuelConsumed: Double, fuelPrice: Double, unit: String): Double {
        val fuelConsumedInGallons = fuelConsumed * Constants.LITRE_TO_US_GALLON // Convert liters to US gallons
        val pricePerUSGallon = when (unit) {
            "Gallon (US gal)" -> fuelPrice // Already in price per US gallon
            "Gallon (UK gal)" -> fuelPrice / Constants.UK_GALLON_TO_US_GALLON // Convert UK gallon price to US gallon price
            else -> fuelPrice * Constants.US_GALLON_TO_LITER // Convert price per liter to price per US gallon
        }
        return fuelConsumedInGallons * pricePerUSGallon // Total cost
    }
}
