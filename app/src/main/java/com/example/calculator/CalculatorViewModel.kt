package com.example.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.random.Random

const val MULTIPLY_OP = "*"
const val DIVIDE_OP = "/"
const val ADD_OP = "+"
const val MODULO_OP = "%"
const val SUBTRACT_OP = "-"
const val SIN_OP = "sin("
const val COS_OP = "cos("
const val TAN_OP = "tan("
const val LOG_OP = "log("
const val ABS_OP = "abs("

enum class Ends {
    EMPTY, OPEN_PARENTHESES, CLOSED_PARENTHESES, INFIX_OPERATOR, PREFIX_OPERATOR, INT, REAL
}

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

    var endsWith: Ends = Ends.EMPTY

    fun update(btnID: Int): Unit {
        var newExp: String = ""
        var oldExp: String = inputExpression.value ?: ""
        val closedOldExp = autoCloseParentheses(oldExp)
        when (btnID) {
            R.id.btnOne -> this.addDigit("1")
            R.id.btnTwo -> this.addDigit("2")
            R.id.btnThree -> this.addDigit("3")
            R.id.btnFour -> this.addDigit("4")
            R.id.btnFive -> this.addDigit("5")
            R.id.btnSix -> this.addDigit("6")
            R.id.btnSeven -> this.addDigit("7")
            R.id.btnEight -> this.addDigit("8")
            R.id.btnNine -> this.addDigit("9")
            R.id.btnZero -> this.addDigit("0") // TODO case where number starts w/ zero
            R.id.btnClear -> this.clear()
            R.id.btnParentheses -> this.addParentheses()
            R.id.btnPercent -> this.addInfixOperation(MODULO_OP)
            R.id.btnDivide -> this.addInfixOperation(DIVIDE_OP)
            R.id.btnMultiply -> this.addInfixOperation( MULTIPLY_OP)
            R.id.btnAdd -> this.addInfixOperation(ADD_OP)
            R.id.btnSubtract -> this.addInfixOperation(SUBTRACT_OP)
            R.id.btnDecimal -> this.addDecimal()
            R.id.btnBackspace -> newExp = if (oldExp.length == 0) {
                    oldExp
                } else {
                    "${oldExp.dropLast(1)}"
                }
        }

        if (btnID != R.id.btnEqual) {
            results.postValue(evaluate(autoCloseParentheses(newExp)))
        } else {
            val res = evaluate(closedOldExp)
            results.postValue("")
            lastEvaluatedExpression.postValue("$closedOldExp = $res")
            inputExpression.postValue(res)
        }

    return
    }

    private fun addDigit(digit: String) {
        var str: String = this.inputExpression.value ?: ""
        when (this.endsWith) {
            Ends.CLOSED_PARENTHESES -> {
                this.inputExpression.postValue("$str$ MULTIPLY_OP$digit")
                this.endsWith = Ends.INT
            }
            Ends.REAL -> {
                this.inputExpression.postValue("$str$digit")
            }
            else -> {
                this.inputExpression.postValue("$str$digit")
                this.endsWith = Ends.INT
            }
        }
    }

    private fun addInfixOperation(op: String) {
        var str = this.inputExpression.value ?: ""

        when (this.endsWith) {
            Ends.REAL, Ends.INT, Ends.CLOSED_PARENTHESES -> {
                this.inputExpression.postValue("$str$op")
                this.endsWith = Ends.INFIX_OPERATOR
            }
        }
    }

    private fun addParentheses() {
        var str = this.inputExpression.value ?: ""

        when (this.endsWith) {
            Ends.EMPTY -> {
                this.inputExpression.postValue("(")
                this.endsWith = Ends.OPEN_PARENTHESES
            }
            Ends.OPEN_PARENTHESES, Ends.INFIX_OPERATOR, Ends.PREFIX_OPERATOR -> {
                this.inputExpression.postValue("$str(")
                this.endsWith = Ends.OPEN_PARENTHESES
            }
            Ends.CLOSED_PARENTHESES, Ends.REAL, Ends.INT -> {
                if (hasUnclosedParentheses(str)) {
                    this.inputExpression.postValue("$str)")
                    this.endsWith = Ends.CLOSED_PARENTHESES
                } else {
                    this.inputExpression.postValue("$str$ MULTIPLY_OP(")
                    this.endsWith = Ends.OPEN_PARENTHESES
                }
            }
        }
    }

    private fun addDecimal() {
        var str = this.inputExpression.value ?: ""

        when (this.endsWith) {
            Ends.EMPTY -> {
                this.inputExpression.postValue("0.")
                this.endsWith = Ends.REAL
            }
            Ends.REAL -> {
                return
            }
            Ends.INT -> {
                this.inputExpression.postValue("$str.")
                this.endsWith = Ends.REAL
            }
            Ends.OPEN_PARENTHESES, Ends.INFIX_OPERATOR, Ends.PREFIX_OPERATOR -> {
                this.inputExpression.postValue("${str}0.")
                this.endsWith = Ends.REAL
            }
            Ends.CLOSED_PARENTHESES -> {
                this.inputExpression.postValue("${str}${ MULTIPLY_OP}0.")
                this.endsWith = Ends.REAL
            }

        }
    }

    private fun clear() {
        var str = this.inputExpression.value ?: ""
        if (str.length > 0) {
            this.inputExpression.postValue("")
        }
        this.endsWith = Ends.EMPTY
    }
}

fun addDigit(str: String, digit: String): String {
    return if (endsWith(str, ')')) "$str$ MULTIPLY_OP$digit" else "$str$digit"
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
        return "$str$ MULTIPLY_OP("
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
    if (endsWith(str, ')')) return "$str${ MULTIPLY_OP}0."
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

