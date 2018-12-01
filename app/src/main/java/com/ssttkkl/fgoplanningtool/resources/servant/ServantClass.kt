package com.ssttkkl.fgoplanningtool.resources.servant

import com.ssttkkl.fgoplanningtool.utils.Localizable

enum class ServantClass : Localizable {
    Saber,
    Archer,
    Lancer,
    Rider,
    Caster,
    Assassin,
    Berserker,
    Ruler,
    Avenger,
    Shielder,
    Alterego,
    MoonCancer,
    Foreigner;

    override val localizedName: String
        get() = name
}