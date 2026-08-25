package com.payyoupayroll;

public class Deductions {

    private static final double SSS_RATE = 0.045;
    private static final double PHILHEALTH_RATE = 0.05;
    private static final double MAX_PAGIBIG = 200.00;

    public static double calculateSSS(double grossPay) {
        if (grossPay < 4250) return 180.00;
        if (grossPay >= 29750) return 1350.00;
        return grossPay * SSS_RATE;
    }

    public static double calculatePhilHealth(double grossPay) {
        if (grossPay <= 10000) return 500.00 / 2;
        if (grossPay >= 100000) return 5000.00 / 2;
        return (grossPay * PHILHEALTH_RATE) / 2;
    }

    public static double calculatePagIbig(double grossPay) {
        if (grossPay <= 1500) {
            return grossPay * 0.01;
        }
        return Math.min(grossPay * 0.02, MAX_PAGIBIG);
    }

    public static double calculateWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20833) return 0.0;
        if (taxableIncome <= 33333) return (taxableIncome - 20833) * 0.15;
        if (taxableIncome <= 66667) return 1875.00 + ((taxableIncome - 33333) * 0.20);
        if (taxableIncome <= 166667) return 8541.67 + ((taxableIncome - 66667) * 0.25);
        return 33541.67 + ((taxableIncome - 166667) * 0.30);
    }

    public static double[] calculateAll(double grossPay) {
        double sss = calculateSSS(grossPay);
        double philhealth = calculatePhilHealth(grossPay);
        double pagIbig = calculatePagIbig(grossPay);
        double taxableIncome = grossPay - (sss + philhealth + pagIbig);
        double tax = calculateWithholdingTax(taxableIncome);
        double totalDeductions = sss + philhealth + pagIbig + tax;
        double netPay = grossPay - totalDeductions;

        return new double[] { sss, philhealth, pagIbig, tax, totalDeductions, netPay };
    }
}
