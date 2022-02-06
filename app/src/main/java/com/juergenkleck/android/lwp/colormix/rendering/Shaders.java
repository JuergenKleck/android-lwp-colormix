package com.juergenkleck.android.lwp.colormix.rendering;

/**
 * Android App - ColorMix LWP
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class Shaders {

    // Calculate movement
    public static final String quadBasicVertex =
            "attribute vec4 a_Position;" +
                    "attribute vec4 a_Color;" +
                    "varying vec4 v_color;" +
                    "void main() {" +
                    "v_color = a_Color;" +
                    "gl_Position = a_Position;" +
//    			"gl_Position.w = 1.0;" +
                    "}";

    // update color
    public static final String quadFragment =
            "precision mediump float;" +
                    "varying vec4 v_color;" +
                    "void main()" +
                    "{" +
                    "gl_FragColor = v_color;" +
                    "}";

}
