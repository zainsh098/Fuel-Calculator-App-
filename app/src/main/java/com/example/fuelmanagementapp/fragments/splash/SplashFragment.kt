package com.example.fuelmanagementapp.fragments.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fuelmanagementapp.databinding.SplashFragmentBinding

class SplashFragment : Fragment() {


    private lateinit var binding: SplashFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = SplashFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


//        lifecycleScope.launch {
//            delay(900)  // Delay for 900 milliseconds
//            findNavController().navigate(R.id.action_splashFragment_to_nav_home)
//
//
//        }
    }

}