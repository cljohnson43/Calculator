package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var model: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = ViewModelProvider(this,
            ViewModelProvider.NewInstanceFactory()).get(CalculatorViewModel::class.java)

        val resultsObserver = Observer<String> {results ->
            tvResults.text = results

        }

        val inputObserver = Observer<String> {input ->
            tvInput.text = input
        }

        val lastEvaluatedExpressionObserver = Observer<String> { lastEvaluatedExpression ->
            tvLastEvaluatedExpression.text = lastEvaluatedExpression
        }

        model.inputExpression.observe(this, inputObserver)
        model.lastEvaluatedExpression.observe(this, lastEvaluatedExpressionObserver)
        model.results.observe(this, resultsObserver)
    }

    fun onClick(view: View) {
        // calculate new expressions and results
        model.update(view.id)
    }
}
