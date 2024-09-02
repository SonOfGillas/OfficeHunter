package com.officehunter.data.repositories

import com.officehunter.data.database.Achievement
import com.officehunter.data.database.AchievementDA0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.withIndex

class AchievementRepository(
    private val achievementDA0: AchievementDA0
) {
    val achievements: Flow<List<Achievement>> = achievementDA0.getAll()

    suspend fun getAchievement(achievement: Achievement){
        val updateAchievement = Achievement(
            id = achievement.id,
            name = achievement.name,
            description = achievement.description,
            pointValue = achievement.pointValue,
            imageName = achievement.imageName,
            numberOfTimesAchieved = achievement.numberOfTimesAchieved+1
        )
        achievementDA0.upsert(updateAchievement)
    }

    suspend fun setDefaultAchievement(){
        for (achievement in defaultAchievements){
            achievementDA0.upsert(achievement)
        }
    }

    suspend fun deleteAll(){
        for(achievement in defaultAchievements){
            achievementDA0.delete(achievement)
        }
    }
}

val defaultAchievements = listOf(
    Achievement(
        0,
        "good morning",
        "go to work",
        "good_morning.png",
        pointValue = 1,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        1,
        "common hunted",
        "catch common hunted",
        "common_hunted.png",
        pointValue = 2,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        2,
        "uncommon hunted",
        "catch uncommon hunted",
        "uncommon_hunted.png",
        pointValue = 2,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        3,
        "rare hunted",
        "catch rare hunted",
        "rare_hunted.png",
        pointValue = 4,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        4,
        "very rare hunted",
        "catch very rare hunted",
        "very_rare_hunted.png",
        pointValue = 8,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        5,
        "ultra rare hunted",
        "catch ultra rare hunted",
        "ultra_rare_hunted.png",
        pointValue = 10,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        6,
        "epic hunted",
        "catch epic hunted",
        "epic_hunted.png",
        pointValue = 12,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        7,
        "legendary hunted",
        "catch legendary hunted",
        "legendary_hunted.png",
        pointValue = 14,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        8,
        "undiscovered hunted",
        "catch undiscovered hunted",
        "undiscovered_hunted.png",
        pointValue = 16,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        9,
        "future generation",
        "enter in the company",
        "future_generation.png",
        pointValue = 50,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        10,
        "modern age",
        "work for 1 year",
        "modern_age.png",
        pointValue = 500,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        11,
        "reinassance",
        "work for 2 years",
        "renaissance.png",
        pointValue = 1000,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        12,
        "middle age",
        "work for 3 years",
        "middle_age.png",
        pointValue = 1500,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        13,
        "ancient history",
        "work for 4 years",
        "ancient_history.png",
        pointValue = 2000,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        14,
        "iron age",
        "work for 5 years",
        "iron_age.png",
        pointValue = 2500,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        15,
        "bronze age",
        "work for 7 years",
        "bronze_age.png",
        pointValue = 3000,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        16,
        "stone age",
        "work for 10 years",
        "stone_age.png",
        pointValue = 3500,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        17,
        "ice age",
        "work for 15 years",
        "ice_age.png",
        pointValue = 4000,
        numberOfTimesAchieved = 0
    ),
    Achievement(
        18,
        "antedeluvian",
        "work for 20 years",
        "antedeluvian.png",
        pointValue = 5000,
        numberOfTimesAchieved = 0
    ),
)