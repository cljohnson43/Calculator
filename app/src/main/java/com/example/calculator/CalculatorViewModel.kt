package com.example.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

const val MULTIPLY_CHAR = '*'
const val DIVIDE_CHAR = '/'
const val ADD_CHAR = '+'
const val MODULO_CHAR = '%'
const val SUBTRACT_CHAR = '-'

class CalculatorViewModel : ViewModel() {
    val inputExpression: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val results: MutableLiveData<String> by lazy {
        MutableLiveData<String>()

    }

    val lastEvaluatedExpression: MutableLiveData<String> by lazy {
        MutableLiveData<String>()

    }

    fun update(btnID: Int): Unit {
        var newExp: String = ""
        var oldExp: String = inputExpression.value ?: ""
        val closedOldExp = autoCloseParentheses(oldExp)
        when (btnID) {
            R.id.btnOne -> newExp = (addDigit(oldExp, "1"))
            R.id.btnTwo -> newExp = addDigit(oldExp, "2")
            R.id.btnThree -> newExp = addDigit(oldExp, "3")
            R.id.btnFour -> newExp = addDigit(oldExp, "4")
            R.id.btnFive -> newExp = addDigit(oldExp, "5")
            R.id.btnSix -> newExp = addDigit(oldExp, "6")
            R.id.btnSeven -> newExp = addDigit(oldExp, "7")
            R.id.btnEight -> newExp = addDigit(oldExp, "8")
            R.id.btnNine -> newExp = addDigit(oldExp, "9")
            R.id.btnZero -> newExp = addDigit(oldExp, "0") // TODO case where number starts w/ zero
            R.id.btnClear -> newExp = ""
            R.id.btnParentheses -> newExp = addParentheses(oldExp)
            R.id.btnPercent -> newExp = addOperation(oldExp, MODULO_CHAR)
            R.id.btnDivide -> newExp = addOperation(oldExp, DIVIDE_CHAR)
            R.id.btnMultiply -> newExp = addOperation(oldExp, MULTIPLY_CHAR)
            R.id.btnAdd -> newExp = addOperation(oldExp, ADD_CHAR)
            R.id.btnSubtract -> newExp = (addOperation(oldExp, SUBTRACT_CHAR))
            R.id.btnDecimal -> newExp = (addDecimal(oldExp))
            R.id.btnBackspace -> newExp = if (oldExp.length == 0) {
                    oldExp
                } else {
                    "${oldExp.dropLast(1)}"
                }
        }

        if (btnID != R.id.btnEqual) {
            results.postValue(evaluate(autoCloseParentheses(newExp)))
            inputExpression.postValue(newExp)
        } else {
            val res = evaluate(closedOldExp)
            results.postValue("")
            lastEvaluatedExpression.postValue("$closedOldExp = $res")
            inputExpression.postValue(res)
        }

    return
    }

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

