/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer.shader;



import static meteordevelopment.meteorclient.MeteorClient.mc;


public class ShaderManager {

    public float time = 0;

    // Post shaders


    public static PostShader MOTION_BLUR;



    public void renderMotionBlur(float blurAmount) {
        MOTION_BLUR.set("BlendFactor", blurAmount);
        MOTION_BLUR.render(mc.getRenderTickCounter().getTickDelta(false));
    }


}
