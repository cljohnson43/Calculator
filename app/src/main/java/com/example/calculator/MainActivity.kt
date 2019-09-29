package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

const val MULTIPLY_CHAR = '*'
const val DIVIDE_CHAR = '/'
const val ADD_CHAR = '+'
const val MODULO_CHAR = '%'
const val SUBTRACT_CHAR = '-'

class MainActivity : AppCompatActivity() {

    private lateinit var model: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider(this,
            ViewModelProvider.NewInstanceFactory()).get(CalculatorViewModel::class.java)

        tvResults.text = model.results
        tvLastEvaluatedExpression.text = model.lastEvaluatedExpression
        tvInput.text = model.inputExpression
    }

    fun onClick(view: View) {
        // save pre button expression
        val lastExpr = model.inputExpression

        // calculate new expressions and results
        model.update(view.id)

        // set new view values
        tvResults.text = model.results
        tvInput.text = model.inputExpression

        if (view.id == R.id.btnEqual) {
            tvLastEvaluatedExpression.text = model.lastEvaluatedExpression
            tvResults.text = ""
        }
    }

}
