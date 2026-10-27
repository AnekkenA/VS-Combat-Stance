package com.vscombatstance.arts.client.animator;

public class Easing {
    public static float easeInOutQuad(float x) {
        return x < 0.5f ? 2f * x * x : 1f - (float) Math.pow(-2f * x + 2f, 2) / 2f;
    }

    public static float easeOutCubic(float x) {
        return 1f - (float) Math.pow(1f - x, 3);
    }

    public static float easeInCubic(float x) {
        return x * x * x;
    }

    public static float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(x - 1f, 3) + c1 * (float) Math.pow(x - 1f, 2);
    }

    public static float clamp01(float val) {
        return Math.max(0.0f, Math.min(1.0f, val));
    }
}
