package com.payyoupayroll;

public class BasePayCalculator {

    public static final double STANDARD_HOURS = 8.0;

    public static final double BASIC_RATE_REGULAR_DAY = 1.00;
    public static final double BASIC_RATE_SPECIAL_DAY = 1.30;
    public static final double BASIC_RATE_HOLIDAY = 2.00;

    public static final double OT_RATE_REGULAR_DAY = 1.25;
    public static final double OT_RATE_SPECIAL_DAY = 1.69;
    public static final double OT_RATE_HOLIDAY = 2.60;

    private final double monthlySalary;
    private final double dailyRate;
    private final double hourlyRate;

    public BasePayCalculator(double monthlySalary, double workingDaysPerYear) {
        if (monthlySalary <= 0) {
            throw new IllegalArgumentException("Monthly salary must be greater than zero.");
        }
        if (workingDaysPerYear <= 0) {
            throw new IllegalArgumentException("Working days per year must be greater than zero.");
        }
        this.monthlySalary = monthlySalary;
        this.dailyRate = (monthlySalary * 12) / workingDaysPerYear;
        this.hourlyRate = this.dailyRate / STANDARD_HOURS;
    }

    public double computeBasicPay(int dayType, double hoursWorked) {
        double effectiveHours = Math.min(hoursWorked, STANDARD_HOURS);
        double prorateFactor = effectiveHours / STANDARD_HOURS;

        if (dayType == 1) return dailyRate * BASIC_RATE_REGULAR_DAY * prorateFactor;
        if (dayType == 2) return dailyRate * BASIC_RATE_SPECIAL_DAY * prorateFactor;
        return dailyRate * BASIC_RATE_HOLIDAY * prorateFactor;
    }

    public double computeOvertimePay(int dayType, double hoursWorked) {
        double overtimeHours = Math.max(0, hoursWorked - STANDARD_HOURS);

        if (overtimeHours == 0) return 0;

        if (dayType == 1) return hourlyRate * OT_RATE_REGULAR_DAY * overtimeHours;
        if (dayType == 2) return hourlyRate * OT_RATE_SPECIAL_DAY * overtimeHours;
        return hourlyRate * OT_RATE_HOLIDAY * overtimeHours;
    }

    public double computeDailyEarnings(int dayType, double hoursWorked) {
        return computeBasicPay(dayType, hoursWorked) + computeOvertimePay(dayType, hoursWorked);
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public String getDayTypeLabel(int dayType) {
        if (dayType == 1) return "Regular Day";
        if (dayType == 2) return "Special Day";
        return "Regular Holiday";
    }
}
