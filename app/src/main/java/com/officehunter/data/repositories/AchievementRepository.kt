package com.officehunter.data.repositories

import android.util.Log
import com.officehunter.data.database.Achievement
import com.officehunter.data.database.AchievementDA0
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.Rarity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.concurrent.TimeUnit

class AchievementRepository(
    private val achievementDA0: AchievementDA0
) {
    val achievements: Flow<List<Achievement>> = achievementDA0.getAll()

    /* return Achievement id */
    suspend fun getHuntedAchievement(catchedHunted: Hunted, onResult: (Result<Achievement>)->Unit){
        val achievementId = when(catchedHunted.rarity){
            Rarity.COMMON -> 1
            Rarity.UNCOMMON -> 2
            Rarity.RARE -> 3
            Rarity.VERY_RARE -> 4
            Rarity.ULTRA_RARE -> 5
            Rarity.EPIC -> 6
            Rarity.LEGENDARY -> 7
            Rarity.UNDISCOVERED -> 8
        }
        val currentAchievementsList = achievements.first()
        val achievement = currentAchievementsList.filter {a -> a.id == achievementId }[0]
        getAchievement(achievement)
        onResult(Result.success(achievement))
    }

    suspend fun getHireDateAchievement(hireDate: Date,onResult: (Result<List<Achievement>>)->Unit){
        val currentAchievementsList = achievements.first()
        val timeElapsed = Date().time - hireDate.time
        val yearsElapsed = timeElapsed/TimeUnit.DAYS.toMillis(365)
        val achievementsUnlocked = mutableListOf<Achievement>()

        val newLevy = currentAchievementsList[9]
        val oneYear = currentAchievementsList[10]
        val twoYears = currentAchievementsList[11]
        val threeYears = currentAchievementsList[12]
        val fourYears = currentAchievementsList[13]
        val fiveYears = currentAchievementsList[14]
        val sevenYears = currentAchievementsList[15]
        val tenYears = currentAchievementsList[16]
        val fifteenYears = currentAchievementsList[17]
        val twentyYears = currentAchievementsList[18]
        if(newLevy.numberOfTimesAchieved == 0){
            achievementsUnlocked.add(newLevy)
        }
        if (oneYear.numberOfTimesAchieved == 0 && yearsElapsed>=1){
            achievementsUnlocked.add(oneYear)
        }
        if (twoYears.numberOfTimesAchieved == 0 && yearsElapsed>=2){
            achievementsUnlocked.add(twoYears)
        }
        if (threeYears.numberOfTimesAchieved == 0 && yearsElapsed>=3){
            achievementsUnlocked.add(threeYears)
        }
        if (fourYears.numberOfTimesAchieved == 0 && yearsElapsed>=4){
            achievementsUnlocked.add(fourYears)
        }
        if (fiveYears.numberOfTimesAchieved == 0 && yearsElapsed>=5){
            achievementsUnlocked.add(fiveYears)
        }
        if (sevenYears.numberOfTimesAchieved == 0 && yearsElapsed>=7){
            achievementsUnlocked.add(sevenYears)
        }
        if (tenYears.numberOfTimesAchieved == 0 && yearsElapsed>=10){
            achievementsUnlocked.add(tenYears)
        }
        if (fifteenYears.numberOfTimesAchieved == 0 && yearsElapsed>=15){
            achievementsUnlocked.add(fifteenYears)
        }
        if (twentyYears.numberOfTimesAchieved == 0 && yearsElapsed>=20){
            achievementsUnlocked.add(twentyYears)
        }
        for (achievement in achievementsUnlocked){
            getAchievement(achievement)
        }
        Log.d("AchievementUnlocked",achievementsUnlocked.toString())
        onResult(Result.success(achievementsUnlocked))
    }

    private suspend fun getAchievement(achievement: Achievement):Achievement{
        val updateAchievement = Achievement(
            id = achievement.id,
            name = achievement.name,
            description = achievement.description,
            pointValue = achievement.pointValue,
            imageName = achievement.imageName,
            numberOfTimesAchieved = achievement.numberOfTimesAchieved+1
        )
        achievementDA0.upsert(updateAchievement)
        return updateAchievement
    }

    suspend fun setDefaultAchievement(){
        if (achievements.first().isEmpty()){
            for (achievement in defaultAchievements){
                achievementDA0.upsert(achievement)
            }
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