package ru.sogaz.site.bff.service.enums

enum class InsuranceKind(
    val desc: String
) {
    OSAGO("Обязательное страхование гражданской ответственности владельцев транспортных средств"),
    CASCO("Добровольное страхование транспортного средства"),
    DMSFL("Добровольное медицинское страхование"),
    NSFL("Страхование от несчастных случаев"),
    VPMG("Страхование выезжающих за пределы постоянного места жительства"),
    IFL("Страхование имущества физических лиц"),
    MORTGAGE("Страхование объектов недвижимости"),
    NONE("Страхование физического лица");

    companion object {
        fun from(type: String?): InsuranceKind = InsuranceKind.entries.find { it.name == type?.uppercase() } ?: NONE
    }
}