package com.ssttkkl.fgoplanningtool.ui.requirementlist

import java.io.File

data class RequirementListEntity(val servantID: Int,
                                 val name: String,
                                 val requirement: Long,
                                 val avatarFile: File?)