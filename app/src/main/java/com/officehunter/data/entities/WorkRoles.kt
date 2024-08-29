package com.officehunter.data.entities

enum class WorkRoles {
    HR,
    MARKETING,
    FE_DEV,
    BE_DEV,
    FULL_STACK_DEV,
    DESIGNER,
    QA,
    DEV_OPS,
    MANAGER,
}

fun getRoleName(role: WorkRoles):String{
    return  when(role){
        WorkRoles.HR -> "HR"
        WorkRoles.MARKETING -> "Marketing"
        WorkRoles.FE_DEV -> "Frontend Developer"
        WorkRoles.BE_DEV -> "Backend Developer"
        WorkRoles.FULL_STACK_DEV -> "Fullstack developer"
        WorkRoles.DESIGNER -> "Designer"
        WorkRoles.QA -> "QA"
        WorkRoles.DEV_OPS -> "Devops"
        WorkRoles.MANAGER -> "Manager"
    }
}


fun getRoleFromName(name: String?): WorkRoles {
    return  when(name){
        "HR" -> WorkRoles.HR
        "Marketing" -> WorkRoles.MARKETING
        "Frontend Developer" -> WorkRoles.FE_DEV
        "Backend Developer" -> WorkRoles.BE_DEV
        "Fullstack developer" -> WorkRoles.FULL_STACK_DEV
        "Designer" -> WorkRoles.DESIGNER
        "QA" -> WorkRoles.QA
        "Devops" -> WorkRoles.DEV_OPS
        "Manager" -> WorkRoles.MANAGER
        else -> WorkRoles.HR
    }
}