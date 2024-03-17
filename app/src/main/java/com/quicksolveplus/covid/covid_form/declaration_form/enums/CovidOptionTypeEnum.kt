package com.quicksolveplus.covid.covid_form.declaration_form.enums

/**
 * 10/04/23.
 *
 * @author hardkgosai.
 */
enum class CovidOptionTypeEnum(id: Int, name: String) {
    Pfizer(1, "Pfizer"),
    Moderna(2, "Moderna"),
    JohnsonAndJohnson(3, "Johnson & Johnson"),
    Exemption(4, "Exempt"),
    Uncertain(5, "Uncertain"),
    Covid19Vaccine(6, "Covid19Vaccine");
}