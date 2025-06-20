package com.example.calculator.utils

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import kotlin.math.*

object CalculatorUtils {
    fun evaluate(expression: String, isDegrees: Boolean): Double {
        val replaced = expression
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
            .replace("√", "sqrt")
            .replace("ln", "log")
            .replace("−", "-")

        return ExpressionBuilder(replaced)
            .function(object : Function("sqrt", 1) {
                override fun apply(vararg args: Double) = sqrt(args[0])
            })
            .function(object : Function("log", 1) {
                override fun apply(vararg args: Double) = ln(args[0])
            })
            .function(object : Function("sin", 1) {
                override fun apply(vararg args: Double) = if (isDegrees) sin(Math.toRadians(args[0])) else sin(args[0])
            })
            .function(object : Function("cos", 1) {
                override fun apply(vararg args: Double) = if (isDegrees) cos(Math.toRadians(args[0])) else cos(args[0])
            })
            .function(object : Function("tan", 1) {
                override fun apply(vararg args: Double) = if (isDegrees) tan(Math.toRadians(args[0])) else tan(args[0])
            })
            .build()
            .evaluate()
    }

    fun formatResult(value: Double): String {
        return if (value == floor(value)) value.toLong().toString()
        else "%.${10}g".format(value)
    }
}