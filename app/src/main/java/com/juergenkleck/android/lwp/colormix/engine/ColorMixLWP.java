package com.juergenkleck.android.lwp.colormix.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import com.juergenkleck.android.lwp.colormix.ColorConstants;
import com.juergenkleck.android.lwp.colormix.rendering.Colors;
import com.juergenkleck.android.lwp.colormix.rendering.ValueContainer;

/**
 * Android App - ColorMix LWP
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class ColorMixLWP extends GLWallpaperService {

    private static class ContextFactory implements EGLContextFactory {
        private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
            EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
            return context;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    private static class ConfigChooser implements EGLConfigChooser {

        public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
            mRedSize = r;
            mGreenSize = g;
            mBlueSize = b;
            mAlphaSize = a;
            mDepthSize = depth;
            mStencilSize = stencil;
        }

        /* This EGL config specification is used to specify 2.0 rendering.
         * We use a minimum size of 4 bits for red/green/blue, but will
         * perform actual matching in chooseConfig() below.
         */
        private static int EGL_OPENGL_ES2_BIT = 4;
        private static int[] s_configAttribs2 =
                {
                        EGL10.EGL_RED_SIZE, 4,
                        EGL10.EGL_GREEN_SIZE, 4,
                        EGL10.EGL_BLUE_SIZE, 4,
                        EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                        EGL10.EGL_NONE
                };

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {

            /* Get the number of minimally matching EGL configurations
             */
            int[] num_config = new int[1];
            egl.eglChooseConfig(display, s_configAttribs2, null, 0, num_config);

            int numConfigs = num_config[0];

            if (numConfigs <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }

            /* Allocate then read the array of minimally matching EGL configs
             */
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, s_configAttribs2, configs, numConfigs, num_config);

            /* Now return the "best" one
             */
            return chooseConfig(egl, display, configs);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
                                      EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config,
                        EGL10.EGL_DEPTH_SIZE, 0);
                int s = findConfigAttrib(egl, display, config,
                        EGL10.EGL_STENCIL_SIZE, 0);

                // We need at least mDepthSize and mStencilSize bits
                if (d < mDepthSize || s < mStencilSize)
                    continue;

                // We want an *exact* match for red/green/blue/alpha
                int r = findConfigAttrib(egl, display, config,
                        EGL10.EGL_RED_SIZE, 0);
                int g = findConfigAttrib(egl, display, config,
                        EGL10.EGL_GREEN_SIZE, 0);
                int b = findConfigAttrib(egl, display, config,
                        EGL10.EGL_BLUE_SIZE, 0);
                int a = findConfigAttrib(egl, display, config,
                        EGL10.EGL_ALPHA_SIZE, 0);

                if (r == mRedSize && g == mGreenSize && b == mBlueSize && a == mAlphaSize)
                    return config;
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                     EGLConfig config, int attribute, int defaultValue) {

            if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
                return mValue[0];
            }
            return defaultValue;
        }

        // Subclasses can adjust these values:
        protected int mRedSize;
        protected int mGreenSize;
        protected int mBlueSize;
        protected int mAlphaSize;
        protected int mDepthSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];
    }


    class ColorMixEngine extends GLWallpaperService.GLEngine
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences mPreferences;
        private ValueContainer mValueContainer;

        public ColorMixEngine() {
            super();
            mValueContainer = new ValueContainer();
            mPreferences = ColorMixLWP.this.getSharedPreferences(ColorConstants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            mPreferences.registerOnSharedPreferenceChangeListener(this);

            // synchronise preferences and value container
            initSharedPrefs(mPreferences);
            onSharedPreferenceChanged(mPreferences, null);

            setEGLContextFactory(new ContextFactory());
            setEGLConfigChooser(new ConfigChooser(5, 6, 5, 0, 16, 0));

            ColorMixGLRenderer renderer = new ColorMixGLRenderer(getApplicationContext(), mValueContainer);
            setRenderer(renderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        private void initSharedPrefs(SharedPreferences prefs) {
            Editor editor = prefs.edit();

            mValueContainer.C1_1 = Colors.C1_1;
            mValueContainer.C1_2 = Colors.C1_2;
            mValueContainer.C1_3 = Colors.C1_3;
            mValueContainer.C1_4 = Colors.C1_4;
            editor.putInt(ColorConstants.TOP_LEFT_RED, new Float(Colors.C1_1 * 255).intValue());
            editor.putInt(ColorConstants.TOP_LEFT_GREEN, new Float(Colors.C1_2 * 255).intValue());
            editor.putInt(ColorConstants.TOP_LEFT_BLUE, new Float(Colors.C1_3 * 255).intValue());
            editor.putInt(ColorConstants.TOP_LEFT_ALPHA, new Float(Colors.C1_4 * 255).intValue());
            mValueContainer.C2_1 = Colors.C2_1;
            mValueContainer.C2_2 = Colors.C2_2;
            mValueContainer.C2_3 = Colors.C2_3;
            mValueContainer.C2_4 = Colors.C2_4;
            editor.putInt(ColorConstants.BOTTOM_LEFT_RED, new Float(Colors.C2_1 * 255).intValue());
            editor.putInt(ColorConstants.BOTTOM_LEFT_GREEN, new Float(Colors.C2_2 * 255).intValue());
            editor.putInt(ColorConstants.BOTTOM_LEFT_BLUE, new Float(Colors.C2_3 * 255).intValue());
            editor.putInt(ColorConstants.BOTTOM_LEFT_ALPHA, new Float(Colors.C2_4 * 255).intValue());
            mValueContainer.C3_1 = Colors.C3_1;
            mValueContainer.C3_2 = Colors.C3_2;
            mValueContainer.C3_3 = Colors.C3_3;
            mValueContainer.C3_4 = Colors.C3_4;
            editor.putInt(ColorConstants.TOP_RIGHT_RED, new Float(Colors.C3_1 * 255).intValue());
            editor.putInt(ColorConstants.TOP_RIGHT_GREEN, new Float(Colors.C3_2 * 255).intValue());
            editor.putInt(ColorConstants.TOP_RIGHT_BLUE, new Float(Colors.C3_3 * 255).intValue());
            editor.putInt(ColorConstants.TOP_RIGHT_ALPHA, new Float(Colors.C3_4 * 255).intValue());
            mValueContainer.C4_1 = Colors.C4_1;
            mValueContainer.C4_2 = Colors.C4_2;
            mValueContainer.C4_3 = Colors.C4_3;
            mValueContainer.C4_4 = Colors.C4_4;
            editor.putInt(ColorConstants.BOTTOM_RIGHT_RED, new Float(Colors.C4_1 * 255).intValue());
            editor.putInt(ColorConstants.BOTTOM_RIGHT_GREEN, new Float(Colors.C4_2 * 255).intValue());
            editor.putInt(ColorConstants.BOTTOM_RIGHT_BLUE, new Float(Colors.C4_3 * 255).intValue());
            editor.putInt(ColorConstants.BOTTOM_RIGHT_ALPHA, new Float(Colors.C4_4 * 255).intValue());

            editor.commit();
        }

        /**
         * Read the shared preferences into the container
         */
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            mValueContainer.C1_1 = prefs.getInt(ColorConstants.TOP_LEFT_RED, new Float(Colors.C1_1 * 255).intValue()) / 255.0f;
            mValueContainer.C1_2 = prefs.getInt(ColorConstants.TOP_LEFT_GREEN, new Float(Colors.C1_2 * 255).intValue()) / 255.0f;
            mValueContainer.C1_3 = prefs.getInt(ColorConstants.TOP_LEFT_BLUE, new Float(Colors.C1_3 * 255).intValue()) / 255.0f;
            mValueContainer.C1_4 = prefs.getInt(ColorConstants.TOP_LEFT_ALPHA, new Float(Colors.C1_4 * 255).intValue()) / 255.0f;
            mValueContainer.C2_1 = prefs.getInt(ColorConstants.BOTTOM_LEFT_RED, new Float(Colors.C2_1 * 255).intValue()) / 255.0f;
            mValueContainer.C2_2 = prefs.getInt(ColorConstants.BOTTOM_LEFT_GREEN, new Float(Colors.C2_2 * 255).intValue()) / 255.0f;
            mValueContainer.C2_3 = prefs.getInt(ColorConstants.BOTTOM_LEFT_BLUE, new Float(Colors.C2_3 * 255).intValue()) / 255.0f;
            mValueContainer.C2_4 = prefs.getInt(ColorConstants.BOTTOM_LEFT_ALPHA, new Float(Colors.C2_4 * 255).intValue()) / 255.0f;
            mValueContainer.C3_1 = prefs.getInt(ColorConstants.TOP_RIGHT_RED, new Float(Colors.C3_1 * 255).intValue()) / 255.0f;
            mValueContainer.C3_2 = prefs.getInt(ColorConstants.TOP_RIGHT_GREEN, new Float(Colors.C3_2 * 255).intValue()) / 255.0f;
            mValueContainer.C3_3 = prefs.getInt(ColorConstants.TOP_RIGHT_BLUE, new Float(Colors.C3_3 * 255).intValue()) / 255.0f;
            mValueContainer.C3_4 = prefs.getInt(ColorConstants.TOP_RIGHT_ALPHA, new Float(Colors.C3_4 * 255).intValue()) / 255.0f;
            mValueContainer.C4_1 = prefs.getInt(ColorConstants.BOTTOM_RIGHT_RED, new Float(Colors.C4_1 * 255).intValue()) / 255.0f;
            mValueContainer.C4_2 = prefs.getInt(ColorConstants.BOTTOM_RIGHT_GREEN, new Float(Colors.C4_2 * 255).intValue()) / 255.0f;
            mValueContainer.C4_3 = prefs.getInt(ColorConstants.BOTTOM_RIGHT_BLUE, new Float(Colors.C4_3 * 255).intValue()) / 255.0f;
            mValueContainer.C4_4 = prefs.getInt(ColorConstants.BOTTOM_RIGHT_ALPHA, new Float(Colors.C4_4 * 255).intValue()) / 255.0f;
            mValueContainer.modified = true;
        }
    }

    @Override
    public Engine onCreateEngine() {
        return new ColorMixEngine();
    }

}