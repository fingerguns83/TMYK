package net.fg83.tmyk.client.task;

import net.fg83.tmyk.client.TmykClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;


public class TraceSearchTask implements Runnable {
    MinecraftClient client = MinecraftClient.getInstance();

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        Fluid targetFluid = getTargetedFluid();
        Block targetBlock = getTargetedBlock();
        Entity targetEntity;

        boolean isSomethingTargeted = false;

        String queryString;

        List<MutableText> messages = new ArrayList<>();


        if (targetFluid != null){
            isSomethingTargeted = true;
            queryString = Registries.FLUID.getId(targetFluid).getPath().toLowerCase().replace("_", "+");
            LookupTask fluidTask = new LookupTask(TmykClient.buildSearchUrl(queryString, false), false);
            fluidTask.run();
            messages.addAll(fluidTask.messages);
        }

        if (targetBlock != null){
            isSomethingTargeted = true;
            queryString = Registries.BLOCK.getId(targetBlock).getPath().toLowerCase().replace("_", "+");
            LookupTask blockLookup = new LookupTask(TmykClient.buildSearchUrl(queryString, false), false);
            blockLookup.run();
            messages.addAll(blockLookup.messages);
        }

        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY){
            isSomethingTargeted = true;
            targetEntity = ((EntityHitResult) client.crosshairTarget).getEntity();
            queryString = Registries.ENTITY_TYPE.getId(targetEntity.getType()).getPath().toLowerCase().replace("_", "+");
            LookupTask entityLookup = new LookupTask(TmykClient.buildSearchUrl(queryString, false), false);
            entityLookup.run();
            messages.addAll(entityLookup.messages);
        }

        if (isSomethingTargeted){
            TmykClient.sendMessages(messages.reversed());
        }
        else {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendMessage(Text.literal("You must either specify a search term (e.g. /wiki creeper) or look at something while running this command.").formatted(Formatting.RED), false);
        }

    }

    private Block getTargetedBlock() {
        if (client.player == null || client.world == null) {
            return null;
        }

        // Get the player's position and look direction
        Vec3d cameraPos = client.player.getCameraPosVec(1.0F);
        Vec3d lookVec = client.player.getRotationVec(1.0F);
        Vec3d reachVec = cameraPos.add(lookVec.multiply(5.0)); // Adjust for desired reach distance

        // Perform the ray trace with fluids included
        BlockHitResult blockHitResult = client.world.raycast(new RaycastContext(
                cameraPos,
                reachVec,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                client.player
        ));

        // Check if the hit result is valid and includes a fluid
        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            BlockState blockState = client.player.getWorld().getBlockState(blockHitResult.getBlockPos());

            if (!blockState.isAir()) {
                return blockState.getBlock();
            }
        }

        return null; // No fluid hit
    }

    private Fluid getTargetedFluid() {
        if (client.player == null || client.world == null) {
            return null;
        }

        // Get the player's position and look direction
        Vec3d cameraPos = client.player.getCameraPosVec(1.0F);
        Vec3d lookVec = client.player.getRotationVec(1.0F);
        Vec3d reachVec = cameraPos.add(lookVec.multiply(5.0)); // Adjust for desired reach distance

        // Perform the ray trace with fluids included
        BlockHitResult blockHitResult = client.world.raycast(new RaycastContext(
                cameraPos,
                reachVec,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.ANY,
                client.player
        ));

        // Check if the hit result is valid and includes a fluid
        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            FluidState fluidState = client.world.getFluidState(blockHitResult.getBlockPos());

            // Return the fluid type, or null if no fluid is present
            if (!fluidState.isEmpty()) {
                return fluidState.getFluid();
            }
        }

        return null; // No fluid hit
    }
}
