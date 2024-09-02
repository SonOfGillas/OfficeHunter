package com.officehunter.utils

import android.util.Log
import com.officehunter.data.entities.WorkRoles
import com.officehunter.data.entities.getRoleFromName
import com.officehunter.data.entities.getRoleName
import com.officehunter.data.remote.firestore.entities.User
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random

enum class QuestionType(val questionString:String){
    // NAME("Guess the name"),
    BIRTHDAY("Guess the birthday"),
    WORK_ROLE("Guess the role"),
    HIRE_DATE("Guess the years of work in the company"),
}

data class Answer(
    val description:String,
    val isCorretAnsware: Boolean = false,
)

data class Question(
    val questionType: QuestionType,
    val answers: List<Answer>
)

fun getRandomQuestionType(): QuestionType {
    val questions = QuestionType.entries.toList()
    return questions[Random.nextInt(questions.size)]
}

fun getRandomQuestion(user: User):Question{
    val question = getRandomQuestionType()
    val answers:List<Answer> = when(question){
        QuestionType.HIRE_DATE -> getPossibleHireYears(user.hireDate)
        QuestionType.WORK_ROLE -> getPossibleWorkRoles(user.workRole)
        QuestionType.BIRTHDAY -> getPossibleBirthday(user.birthdate)
    }
    return Question(
        questionType = question,
        answers = answers.shuffled()
    )
}

fun getPossibleBirthday(corretAnswer:Date?):List<Answer>{
    if(corretAnswer!=null){
        val possibleAnswers = mutableListOf<Answer>()
        possibleAnswers.add(Answer(
            description = Formatter.date2Birthday(corretAnswer),
            isCorretAnsware = true
        ))
        //generate 3 random dates
        for (i in 0..2){
            val randomDate = Date(Random.nextLong(0, Calendar.getInstance().timeInMillis))
            possibleAnswers.add(Answer(
                description = Formatter.date2Birthday(randomDate)
            ))
        }
        return possibleAnswers
    } else {
        return emptyList()
    }
}

fun getPossibleHireYears(corretAnswer: Date?):List<Answer>{
    if (corretAnswer != null){
        val possibleAnswers = mutableListOf<Answer>()
        possibleAnswers.add(Answer(
            description = Formatter.date2TimePassed(corretAnswer),
            isCorretAnsware = true
        ))
        //generate 3 random dates from -3 to 3 years from corretAnswer
        val treeYears = TimeUnit.DAYS.toMillis(365*3)
        val timePassed = Date().time - corretAnswer.time
        val maxRange = if (timePassed > treeYears) treeYears else timePassed
        for (i in 0..2){
            val randomDate = Date(
                Random.nextLong(
                    corretAnswer.time - treeYears,
                    corretAnswer.time + maxRange))
            possibleAnswers.add(Answer(
                description = Formatter.date2TimePassed(randomDate)
            ))
        }
        return possibleAnswers
    } else {
        return  emptyList()
    }
}

fun getPossibleWorkRoles(corretAnswer: WorkRoles?):List<Answer>{
    if(corretAnswer!=null){
        val possibleAnswers = mutableListOf<Answer>()
        possibleAnswers.add(Answer(
            description = getRoleName(corretAnswer),
            isCorretAnsware = true
        ))
        val otherPossibleRole = mutableSetOf<WorkRoles>()
        val roles = WorkRoles.entries.toTypedArray()
        while (otherPossibleRole.size < 3){
            val randomWorkRole = roles[Random.nextInt(roles.size)]
            if(randomWorkRole != corretAnswer &&  !otherPossibleRole.contains(randomWorkRole)){
                otherPossibleRole.add(randomWorkRole)
                possibleAnswers.add(Answer(
                    description = getRoleName(randomWorkRole)
                ))
            }
        }
        return possibleAnswers
    } else {
        return emptyList()
    }
}
