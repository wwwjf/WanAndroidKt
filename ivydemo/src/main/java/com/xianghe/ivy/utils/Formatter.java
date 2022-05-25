package com.xianghe.ivy.utils;

import java.util.Locale;

public class Formatter {
    public static String formatNumber(long n) {
        if (n < 10000) {
            return String.format(Locale.US, "%d", n);
        } else {
            return String.format(Locale.US, "%.1fw", n * 1f / 10000);
        }
    }

    public static BytesResult formatBytes(long sizeBytes) {
        //final int unit = ((flags & FLAG_IEC_UNITS) != 0) ? 1024 : 1000;
        final int unit = 1024;
        final boolean isNegative = (sizeBytes < 0);
        float result = isNegative ? -sizeBytes : sizeBytes;
        String units = "B";
        long mult = 1;
        if (result > 900) {
            units = "KB";
            mult = unit;
            result = result / unit;
        }
        if (result > 900) {
            units = "MB";
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = "GB";
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = "TB";
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = "PB";
            mult *= unit;
            result = result / unit;
        }
        // Note we calculate the rounded long by ourselves, but still let String.format()
        // compute the rounded value. String.format("%f", 0.1) might not return "0.1" due to
        // floating point errors.
        final int roundFactor;
        final String roundFormat;
        if (mult == 1 || result >= 100) {
            roundFactor = 1;
            roundFormat = "%.0f";
        } else if (result < 1) {
            roundFactor = 100;
            roundFormat = "%.2f";
        } else if (result < 10) {
            roundFactor = 100;
            roundFormat = "%.2f";
        } else { // 10 <= result < 100
            roundFactor = 100;
            roundFormat = "%.2f";
        }

        if (isNegative) {
            result = -result;
        }
        final String roundedString = String.format(roundFormat, result);

        return new BytesResult(roundedString, units, 0);
    }

    public static class BytesResult {
        public final String value;
        public final String units;
        public final long roundedBytes;

        public BytesResult(String value, String units, long roundedBytes) {
            this.value = value;
            this.units = units;
            this.roundedBytes = roundedBytes;
        }
    }
}
