package com.officehunter.utils

import java.util.Calendar
import java.util.Date
import kotlin.random.Random

enum class QuestionType(questionString:String){
    NAME("Guess the name"),
    BIRTHDAY("Guess the birthday"),
    WORK_ROLE("Guess the role"),
    HIRE_DATE("Guess the years of work in the company"),
}

data class Answare<T>(
    val value:T,
    val description:String,
    val isCorretAnsware: Boolean = false,
)

data class Question<T>(
    val type: QuestionType,
    val corretAnswer: T
)

fun getPossibleBirthday(corretAnswer:Date):List<Answare<Date>>{
    val possibleAnswers = mutableListOf<Answare<Date>>()
    possibleAnswers.add(Answare(
        value = corretAnswer,
        description = Formatter.date2Birthday(corretAnswer),
        isCorretAnsware = true
    ))
    //generate 3 random dates
    for (i in 0..2){
        val randomDate = Date(Random.nextLong(0, Calendar.getInstance().timeInMillis))
        possibleAnswers.add(Answare(
            value = randomDate,
            description = Formatter.date2Birthday(randomDate)
        ))
    }
    return possibleAnswers.shuffled()
}

fun getPossibleHireYears(corretAnswer: Date):List<Answare<String>>{
    val possibleAnswers = mutableListOf<Answare<String>>()
    possibleAnswers.add(Answare(
        value = Formatter.date2TimePassed(corretAnswer),
        description = Formatter.date2TimePassed(corretAnswer),
        isCorretAnsware = true
    ))
    //generate 3 random dates from -3 to 3 years from corretAnswer
    for (i in 0..2){
        val randomDate = Date(
            Random.nextLong(
                corretAnswer.time - 94608000000,
                corretAnswer.time + 94608000000))
        possibleAnswers.add(Answare(
            value = Formatter.date2TimePassed(randomDate),
            description = Formatter.date2TimePassed(randomDate)
        ))
    }
    return possibleAnswers.shuffled()
}
