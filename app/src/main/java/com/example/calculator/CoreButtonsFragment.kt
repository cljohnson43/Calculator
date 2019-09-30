package com.example.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class CoreButtonsFragment : Fragment() {

    private lateinit var model: CalculatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.core_buttons, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model = activity?.run {
            ViewModelProvider(this,
                ViewModelProvider.NewInstanceFactory()).get(CalculatorViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    fun onClick(view: View) {
        model.update(view.id)

    }
}