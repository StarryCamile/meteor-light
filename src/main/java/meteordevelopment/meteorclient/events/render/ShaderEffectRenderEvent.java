/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.events.render;

public class ShaderEffectRenderEvent {
    private static final ShaderEffectRenderEvent INSTANCE = new ShaderEffectRenderEvent();

    public float tickDelta;

    public static ShaderEffectRenderEvent get(float tickDelta) {
        INSTANCE.tickDelta = tickDelta;
        return INSTANCE;
    }
}
