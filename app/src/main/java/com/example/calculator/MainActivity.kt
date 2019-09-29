package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

const val MULTIPLY_CHAR = '*'
const val DIVIDE_CHAR = '/'
const val ADD_CHAR = '+'
const val MODULO_CHAR = '%'
const val SUBTRACT_CHAR = '-'

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        val newExpr = getNewExpression(tvInput.text.toString(), view.id)
        tvResults.text = evaluate(autoCloseParentheses(newExpr))
        tvInput.text = newExpr
    }

    fun getNewExpression(str: String, btnID: Int): String {
        var newExp: String = ""
        when (btnID) {
            R.id.btnOne -> newExp = addDigit(str, "1")
            R.id.btnTwo -> newExp = addDigit(str, "2")
            R.id.btnThree -> newExp = addDigit(str, "3")
            R.id.btnFour -> newExp = addDigit(str, "4")
            R.id.btnFive -> newExp = addDigit(str, "5")
            R.id.btnSix -> newExp = addDigit(str, "6")
            R.id.btnSeven -> newExp = addDigit(str, "7")
            R.id.btnEight -> newExp = addDigit(str, "8")
            R.id.btnNine -> newExp = addDigit(str, "9")
            R.id.btnZero -> newExp = addDigit(str, "0") // TODO case where number starts w/ zero
            R.id.btnClear -> newExp = ""
            R.id.btnParentheses -> newExp = addParentheses(str)
            R.id.btnPercent -> newExp = addOperation(str, MODULO_CHAR)
            R.id.btnDivide -> newExp = addOperation(str, DIVIDE_CHAR)
            R.id.btnMultiply -> newExp = addOperation(str, MULTIPLY_CHAR)
            R.id.btnAdd -> newExp = addOperation(str, ADD_CHAR)
            R.id.btnSubtract -> newExp = addOperation(str, SUBTRACT_CHAR)
            R.id.btnDecimal -> newExp = addDecimal(str)
            R.id.btnSign -> newExp = changeSign(str)
            R.id.btnBackspace -> newExp = if (str.length == 0) str else "${str.dropLast(1)}"
            R.id.btnEqual -> newExp = evaluate(autoCloseParentheses(str))
        }

        return newExp
    }

    fun addDigit(str: String, digit: String): String {
        return if (endsWith(str, ')')) "$str$MULTIPLY_CHAR$digit" else "$str$digit"
    }

    fun addOperation(str: String, op: Char): String {
        if (str.length == 0) return ""
        if (endsWithOperation(str)) return "${str.substring(0, str.length-1)}$op"
        if (endsWithOperand(str)) return "$str$op"
        return str
    }

    fun addParentheses(str: String): String {
        if (endsWithOperation(str)) return "$str("
        if (endsWithOperand(str)) {
            if (hasUnclosedParentheses(str)) return "$str)"
            return "$str$MULTIPLY_CHAR("
        }

        // must end with (
        return "$str("
    }

    fun addDecimal(str: String): String {
        if (endsWith(str, '.')) return str
        if (endsWithDigit(str)) {
            // TODO handle this case
            return "$str."
        }
        if (endsWithOperation(str) || endsWith(str, '(')) return "${str}0."
        if (endsWith(str, ')')) return "$str${MULTIPLY_CHAR}0."
        return str
    }

    fun endsWith(str: String, char: Char): Boolean {
        if (str.length == 0) return false
        return str.get(str.lastIndex) == char
    }

    fun endsWithOperation(str: String): Boolean {
        val lastChar: Char? = str.lastOrNull()
        if (lastChar == null) return false
        return !(lastChar.isDigit() || lastChar == '.' || endsWith(str, '(') || endsWith(str, ')'))
    }

    fun endsWithDigit(str: String): Boolean {
        val lastChar: Char? = str.lastOrNull()
        if (lastChar == null) return false

        return lastChar.isDigit() || lastChar == '.'
    }

    fun endsWithOperand(str: String): Boolean {
        return endsWithDigit(str) || endsWith(str, ')')
    }

    fun hasUnclosedParentheses(str: String): Boolean {
        return str.count { it == '(' } > str.count { it == ')' }
    }

    fun isValidExpression(str: String): Boolean {
       return false // TODO
    }

    fun changeSign(str: String): String {
        if (str.length == 0) return "($SUBTRACT_CHAR"
        if (endsWithDigit(str)) {
            // handle turning negative to positive
            // handle turning pos to neg
        }
        return "" //TODO

    }

    fun getNumbers(str: String): List<Long> {
        val charArr = str.toCharArray()
        var nums: List<Long>

        var startOfNum: Int? = null
        for (i in charArr.indices) {
            if (charArr[i].isDigit()) {
               startOfNum = startOfNum ?: i
            } else {}

        }
        return listOf()
    }

    fun evaluate(str: String): String {
        val exp: Expression?
        var results: String
        try {
            exp = ExpressionBuilder(str).build()
            results = exp.evaluate().toString()

        } catch (e: Throwable) {
            results = ""
        }

        return formatResults(results)
    }

    fun formatResults(str: String): String {
        if (str.length < 3) return str
        val lastTwo = str.takeLast(2)
        if (lastTwo.equals(".0")) return str.dropLast(2)
        return str
    }

    fun autoCloseParentheses(str: String): String {
        val numOpenParentheses = str.count{it == '('}
        val numClosedParentheses = str.count{it == ')'}
        if (numOpenParentheses > numClosedParentheses) {
            val diff = numOpenParentheses - numClosedParentheses
            val closeExpression = ")".repeat(diff)
            return str.plus(closeExpression)
        }
        return str
    }

}
