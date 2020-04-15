package info.simplyapps.lwp.colormix.rendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class RenderUtil {

    public static int loadShader(String strSource, int iType) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            return 0;
        }
        return iShader;
    }

    public static int loadProgram(String vertexShaderSource, String fragmentShaderSource) {
        int iVertexShader;
        int iFragmentShader;
        int iProgram;
        int[] link = new int[1];
        iVertexShader = loadShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER);
        if (iVertexShader == 0) {
            return 0;
        }
        iFragmentShader = loadShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFragmentShader == 0) {
            return 0;
        }

        // create empty OpenGL Program
        iProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(iProgram, iVertexShader);
        // add the fragment shader to program
        GLES20.glAttachShader(iProgram, iFragmentShader);

        GLES20.glLinkProgram(iProgram);

        GLES20.glGetProgramiv(iProgram, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            return 0;
        }
        GLES20.glDeleteShader(iVertexShader);
        GLES20.glDeleteShader(iFragmentShader);
        return iProgram;
    }

    public static float rnd(float min, float max) {
        float fRandNum = (float) Math.random();
        return min + (max - min) * fRandNum;
    }

    public static int loadTexture2(Context mContext, int resource) {
        InputStream is = mContext.getResources().openRawResource(resource);
        int[] textureId = new int[1];
        Bitmap bitmap;
//	        bitmap = BitmapFactory.decodeStream(is);
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore.
            }
        }

        byte[] buffer = new byte[bitmap.getWidth() * bitmap.getHeight() * 3];

        for (int y = 0; y < bitmap.getHeight(); y++)
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);
                buffer[(y * bitmap.getWidth() + x) * 3 + 0] = (byte) ((pixel >> 16) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 3 + 1] = (byte) ((pixel >> 8) & 0xFF);
                buffer[(y * bitmap.getWidth() + x) * 3 + 2] = (byte) ((pixel >> 0) & 0xFF);
            }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 3);
        byteBuffer.put(buffer).position(0);

        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, bitmap.getWidth(), bitmap.getHeight(), 0,
                GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        if (bitmap != null) bitmap.recycle();

        return textureId[0];

    }

    public static int loadTextureNotWorking(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
//	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            InputStream is = context.getResources().openRawResource(resourceId);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // Ignore.
                }
            }

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
//	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
}
