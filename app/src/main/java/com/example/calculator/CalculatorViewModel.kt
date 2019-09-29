package com.example.calculator

import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorViewModel : ViewModel() {
    var inputExpression: String = ""
    var results: String = ""
    var lastEvaluatedExpression: String = ""

    fun update(btnID: Int): Unit {
        when (btnID) {
            R.id.btnOne -> inputExpression = addDigit(inputExpression, "1")
            R.id.btnTwo -> inputExpression = addDigit(inputExpression, "2")
            R.id.btnThree -> inputExpression = addDigit(inputExpression, "3")
            R.id.btnFour -> inputExpression = addDigit(inputExpression, "4")
            R.id.btnFive -> inputExpression = addDigit(inputExpression, "5")
            R.id.btnSix -> inputExpression = addDigit(inputExpression, "6")
            R.id.btnSeven -> inputExpression = addDigit(inputExpression, "7")
            R.id.btnEight -> inputExpression = addDigit(inputExpression, "8")
            R.id.btnNine -> inputExpression = addDigit(inputExpression, "9")
            R.id.btnZero -> inputExpression = addDigit(inputExpression, "0") // TODO case where number starts w/ zero
            R.id.btnClear -> inputExpression = ""
            R.id.btnParentheses -> inputExpression = addParentheses(inputExpression)
            R.id.btnPercent -> inputExpression = addOperation(inputExpression, MODULO_CHAR)
            R.id.btnDivide -> inputExpression = addOperation(inputExpression, DIVIDE_CHAR)
            R.id.btnMultiply -> inputExpression = addOperation(inputExpression, MULTIPLY_CHAR)
            R.id.btnAdd -> inputExpression = addOperation(inputExpression, ADD_CHAR)
            R.id.btnSubtract -> inputExpression = addOperation(inputExpression, SUBTRACT_CHAR)
            R.id.btnDecimal -> inputExpression = addDecimal(inputExpression)
            R.id.btnSign -> inputExpression = changeSign(inputExpression)
            R.id.btnBackspace -> inputExpression = if (inputExpression.length == 0) inputExpression else "${inputExpression.dropLast(1)}"
            R.id.btnEqual -> {
                val lastExpr = autoCloseParentheses(inputExpression)
                results = evaluate(lastExpr)
                lastEvaluatedExpression = "$lastExpr = $results"
                inputExpression = evaluate(lastExpr)
            }
        }

        results = evaluate(autoCloseParentheses(inputExpression))

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

