package info.simplyapps.lwp.colormix.engine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import info.simplyapps.lwp.colormix.rendering.ParticleManager;
import info.simplyapps.lwp.colormix.rendering.Quad;
import info.simplyapps.lwp.colormix.rendering.Quads;
import info.simplyapps.lwp.colormix.rendering.RenderUtil;
import info.simplyapps.lwp.colormix.rendering.Shaders;
import info.simplyapps.lwp.colormix.rendering.ValueContainer;

public class ColorMixGLRenderer implements GLWallpaperService.Renderer {

    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;

    /**
     * How many elements per vertex.
     */
    private final int mStrideBytes = 7 * mBytesPerFloat;

    // control positin of triangle

    // Surface camera
    private int mMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjMatrix = new float[16];

    public ParticleManager mgr;
    int iProgId;

    public Quad[] quads;
    public ValueContainer mValueContainer;
    public float[] quadColors;
    public float[] quadColorsTarget;

    Context mContext;

    float touchX;
    float touchY;

    public ColorMixGLRenderer(Context context, ValueContainer valueContainer) {
        this.mValueContainer = valueContainer;
        mgr = new ParticleManager(valueContainer);
        mContext = context;

        createQuadColors();

        mgr.start();
    }

    public void stop() {
        mgr.stop();
    }

    private void createQuadColors() {
        this.quadColors = new float[]{
                // X, Y, Z, R, G, B, A
                // base quad
                -1.0f, 1.0f, 0.0f, mValueContainer.C1_1, mValueContainer.C1_2, mValueContainer.C1_3, mValueContainer.C1_4,
                1.0f, 1.0f, 0.0f, mValueContainer.C3_1, mValueContainer.C3_2, mValueContainer.C3_3, mValueContainer.C3_4,
                -1.0f, -1.0f, 0.0f, mValueContainer.C2_1, mValueContainer.C2_2, mValueContainer.C2_3, mValueContainer.C2_4,
                -1.0f, -1.0f, 0.0f, mValueContainer.C2_1, mValueContainer.C2_2, mValueContainer.C2_3, mValueContainer.C2_4,
                1.0f, 1.0f, 0.0f, mValueContainer.C3_1, mValueContainer.C3_2, mValueContainer.C3_3, mValueContainer.C3_4,
                1.0f, -1.0f, 0.0f, mValueContainer.C4_1, mValueContainer.C4_2, mValueContainer.C4_3, mValueContainer.C4_4
        };
        this.quadColorsTarget = new float[]{
                // X, Y, Z, R, G, B, A
                // base quad
                -1.0f, 1.0f, 0.0f, mValueContainer.C3_1, mValueContainer.C3_2, mValueContainer.C3_3, mValueContainer.C3_4,
                1.0f, 1.0f, 0.0f, mValueContainer.C4_1, mValueContainer.C4_2, mValueContainer.C4_3, mValueContainer.C4_4,
                -1.0f, -1.0f, 0.0f, mValueContainer.C1_1, mValueContainer.C1_2, mValueContainer.C1_3, mValueContainer.C1_4,
                -1.0f, -1.0f, 0.0f, mValueContainer.C1_1, mValueContainer.C1_2, mValueContainer.C1_3, mValueContainer.C1_4,
                1.0f, 1.0f, 0.0f, mValueContainer.C4_1, mValueContainer.C4_2, mValueContainer.C4_3, mValueContainer.C4_4,
                1.0f, -1.0f, 0.0f, mValueContainer.C2_1, mValueContainer.C2_2, mValueContainer.C2_3, mValueContainer.C2_4
        };
    }

    public void onDrawFrame(GL10 gl) {

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(iProgId);

        Matrix.setIdentityM(mModelMatrix, 0);
//        Matrix.translateM(mModelMatrix, 0, 4.0f, 0.0f, -7.0f);
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);        
//        Matrix.setRotateM(mModelMatrix, 0, mAngle, 0, 0, 1.0f);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        for (Quad quad : quads) {
            if (quad != null) {
                drawQuad(quad);
            }
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // No culling of back faces
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        // No depth testing
//		GLES20.glDisable(GLES20.GL_DEPTH_TEST);


        // Enable blending
        GLES20.glEnable(GLES20.GL_BLEND);
        // Enable texture mapping
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 2.0f; // positive for frontside view, negative for backside view

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
// 		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //always link a program with VSH and FSH as a pair
        iProgId = RenderUtil.loadProgram(Shaders.quadBasicVertex, Shaders.quadFragment);

        quads = new Quad[1];
        quads[0] = createQuad(iProgId, Quads.quad1);
    }

    private Quad createQuad(int _program, float[] q) {
        Quad quad = new Quad();

        // create vertices
        quad.vertices = q;

        // create floatbuffer
        quad.mPositionBuffer = ByteBuffer.allocateDirect(quad.vertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        quad.mPositionBuffer.put(quad.vertices).position(0);

        // set handles
        quad.mPositionHandle = GLES20.glGetAttribLocation(_program, "a_Position");
        quad.mColorHandle = GLES20.glGetAttribLocation(_program, "a_Color");

        return quad;
    }

    private float colorInterpolation(float base, float target, float mod) {
        //values[id] + (mod / 500.0f)
        return base + ((target - base) * mod / 2.0f);
    }

    /**
     * Draws a triangle from the given vertex data.
     *
     * @param quad The buffer containing the vertex data.
     */
    private void drawQuad(Quad quad) {
        if (mValueContainer.modified) {
            createQuadColors();
            mValueContainer.modified = false;
        }

        // Pass in the position information
        quad.mPositionBuffer.position(0);
        GLES20.glVertexAttribPointer(quad.mPositionHandle, 3, GLES20.GL_FLOAT, false, mStrideBytes, quad.mPositionBuffer);
        GLES20.glEnableVertexAttribArray(quad.mPositionHandle);

        // calculate new color value based on a sinus wave and the time
        quad.mPositionBuffer.position(0);
        float[] values = new float[quad.mPositionBuffer.capacity()];
        quad.mPositionBuffer.get(values);
        // move slightly from c1 to c3 and c2 to c4
        float mod = new Double(Math.sin(mValueContainer.time)).floatValue();

        int i = 0;
        for (int id = 0; id < values.length; id++, i++) {
            if (i == 3 || i == 4 || i == 5) {
                values[id] = colorInterpolation(quadColors[id], quadColorsTarget[id], mod);
                quad.mPositionBuffer.put(id, values[id]);
            }
            if (i > 6) {
                i = 0;
            }
        }

        // set new colors
        quad.mPositionBuffer.position(3);
        GLES20.glVertexAttribPointer(quad.mColorHandle, 4, GLES20.GL_FLOAT, false, mStrideBytes, quad.mPositionBuffer);
        GLES20.glEnableVertexAttribArray(quad.mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // draw 6 vertices - 3 for each triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, quad.vertices.length / 7);
    }

    public void touchEvent(float touchX, float touchY) {
        this.touchX = touchX;
        this.touchY = touchY;
    }

}