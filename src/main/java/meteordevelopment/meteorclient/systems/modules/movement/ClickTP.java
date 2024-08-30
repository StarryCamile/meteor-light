/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;

public class ClickTP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private ArrayList<Double> xList;
    private ArrayList<Double> yList;
    private ArrayList<Double> zList;
    private final Setting<Double> maxDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-distance")
        .description("The maximum distance you can teleport.")
        .defaultValue(5)
        .build()
    );
    private final Setting<Boolean> checkPos = sgGeneral.add(new BoolSetting.Builder()
        .name("check-pos")
        .description("Check the pos after teleporting, re-teleport if not in the same.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Double> diff = sgGeneral.add(new DoubleSetting.Builder()
        .name("check-pos-diff")
        .description("The difference of player pos and target pos")
        .defaultValue(0.5)
        .visible(checkPos::get)
        .build()
    );
    private final Setting<Double> diffBlock = sgGeneral.add(new DoubleSetting.Builder()
        .name("check-pos-diffBlock")
        .description("idk")
        .defaultValue(5)
            .min(1)
            .max(maxDistance.get())
        .visible(checkPos::get)
        .build()
    );



    public ClickTP() {
        super(Categories.Movement, "click-tp", "Teleports you to the block you click on.");
    }

    public boolean checkPos(double x1, double y1, double z1, double x2, double y2, double z2){
        if (x1 == x2 && y1 == y2 && z1 == z2){
            return true;
        } else {
            double _diff = diff.get();
            return Math.abs(x1 - x2) <=  _diff && Math.abs(y1 - y2) <= _diff && Math.abs(z1 - z2) <= _diff;
        }
    }
    private void teleport(double x, double y, double z){
        if (mc.player != null) {
            mc.player.setPosition(x, y, z);
        }
    }
    private void resetPathList(double x1, double y1, double z1, double x2, double y2, double z2){

        for (double sx = x1; sx < x2; sx+=diffBlock.get()) {
            xList.add(sx);
        }
        for (double sy = y1; sy < y2; sy+=diffBlock.get()) {
            yList.add(sy);
        }
        for (double sz = z1; sz < z2; sz+=diffBlock.get()) {
            zList.add(sz);
        }

    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player.isUsingItem()) return;

        if (mc.options.useKey.isPressed()) {
            HitResult hitResult = mc.player.raycast(maxDistance.get(), 1f / 20f, false);

            if (hitResult.getType() == HitResult.Type.ENTITY && mc.player.interact(((EntityHitResult) hitResult).getEntity(), Hand.MAIN_HAND) != ActionResult.PASS) return;

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                Direction side = ((BlockHitResult) hitResult).getSide();

                if (mc.world.getBlockState(pos).onUse(mc.world, mc.player, (BlockHitResult) hitResult) != ActionResult.PASS) return;

                BlockState state = mc.world.getBlockState(pos);

                VoxelShape shape = state.getCollisionShape(mc.world, pos);
                if (shape.isEmpty()) shape = state.getOutlineShape(mc.world, pos);

                double height = shape.isEmpty() ? 1 : shape.getMax(Direction.Axis.Y);
                double shouldX = pos.getX() + 0.5 + side.getOffsetX();
                double shouldY = pos.getY() + height;
                double shouldZ = pos.getZ() + 0.5 + side.getOffsetZ();
                teleport(shouldX, shouldY, shouldZ);
                if (checkPos.get()){
                    if (!checkPos(mc.player.getX(), mc.player.getY(), mc.player.getZ(), shouldX, shouldY, shouldZ)) {
                        resetPathList(mc.player.getX(), mc.player.getY(), mc.player.getZ(),shouldX, shouldY, shouldZ);
                        int i = 0;
                        while (i < xList.size()){
                            teleport(xList.get(i), yList.get(i), zList.get(i));
                        }
                    }
                }

            }
        }
    }
}
