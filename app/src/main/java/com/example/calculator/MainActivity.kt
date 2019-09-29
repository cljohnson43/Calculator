package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

const val MULTIPLY_CHAR = '*'
const val DIVIDE_CHAR = '/'
const val ADD_CHAR = '+'
const val MODULO_CHAR = '%'
const val SUBTRACT_CHAR = '\u2212'

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        tvInput.text = getStringExpression(tvInput.text.toString(), view.id)
    }

    fun getStringExpression(str: String, btnID: Int): String {
        when (btnID) {
            R.id.btnOne -> return addDigit(str, "1")
            R.id.btnTwo -> return addDigit(str, "2")
            R.id.btnThree -> return addDigit(str, "3")
            R.id.btnFour -> return addDigit(str, "4")
            R.id.btnFive -> return addDigit(str, "5")
            R.id.btnSix -> return addDigit(str, "6")
            R.id.btnSeven -> return addDigit(str, "7")
            R.id.btnEight -> return addDigit(str, "8")
            R.id.btnNine -> return addDigit(str, "9")
            R.id.btnZero -> return addDigit(str, "0")
            R.id.btnClear -> return ""
            R.id.btnParentheses -> return addParentheses(str)
            R.id.btnPercent -> return addOperation(str, MODULO_CHAR)
            R.id.btnDivide -> return addOperation(str, DIVIDE_CHAR)
            R.id.btnMultiply -> return addOperation(str, MULTIPLY_CHAR)
            R.id.btnAdd -> return addOperation(str, ADD_CHAR)
            R.id.btnSubtract -> return addOperation(str, SUBTRACT_CHAR)
            R.id.btnDecimal -> return addDecimal(str)
            R.id.btnSign -> return changeSign(str)
            R.id.btnBackspace -> return if (str.length == 0) str else "${str.dropLast(1)}"
            R.id.btnEqual -> return ""//TODO
        }
        return str
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

}
