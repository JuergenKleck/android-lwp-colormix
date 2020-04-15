package info.simplyapps.lwp.colormix.rendering;

public class Quads {

    public static final float[] quad1target = new float[]{
            // X, Y, Z, R, G, B, A
            // base quad
            -1.0f, 1.0f, 0.0f, Colors.C3_1, Colors.C3_2, Colors.C3_3, Colors.C3_4,
            1.0f, 1.0f, 0.0f, Colors.C4_1, Colors.C4_2, Colors.C4_3, Colors.C4_4,
            -1.0f, -1.0f, 0.0f, Colors.C1_1, Colors.C1_2, Colors.C1_3, Colors.C1_4,
            -1.0f, -1.0f, 0.0f, Colors.C1_1, Colors.C1_2, Colors.C1_3, Colors.C1_4,
            1.0f, 1.0f, 0.0f, Colors.C4_1, Colors.C4_2, Colors.C4_3, Colors.C4_4,
            1.0f, -1.0f, 0.0f, Colors.C2_1, Colors.C2_2, Colors.C2_3, Colors.C2_4
    };

    public static final float[] quad1 = new float[]{
            // X, Y, Z, R, G, B, A
            // base quad
            -1.0f, 1.0f, 0.0f, Colors.C1_1, Colors.C1_2, Colors.C1_3, Colors.C1_4,
            1.0f, 1.0f, 0.0f, Colors.C3_1, Colors.C3_2, Colors.C3_3, Colors.C3_4,
            -1.0f, -1.0f, 0.0f, Colors.C2_1, Colors.C2_2, Colors.C2_3, Colors.C2_4,
            -1.0f, -1.0f, 0.0f, Colors.C2_1, Colors.C2_2, Colors.C2_3, Colors.C2_4,
            1.0f, 1.0f, 0.0f, Colors.C3_1, Colors.C3_2, Colors.C3_3, Colors.C3_4,
            1.0f, -1.0f, 0.0f, Colors.C4_1, Colors.C4_2, Colors.C4_3, Colors.C4_4
    };

}
