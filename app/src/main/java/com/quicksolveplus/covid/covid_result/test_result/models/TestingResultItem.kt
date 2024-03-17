package com.quicksolveplus.covid.covid_result.test_result.models

data class TestingResultItem(
    val EmployeeId: Int,
    val TestingDate: String,
    val TestingResult: String,
    val TestingResultId: Int,
    val TestingResultImageNames: List<String>,
    val TestingStatus: String,
    val TestingStatusForecolor: String,
    val WeekEndDate: String,
    val WeekStartDate: String
)