package net.fg83.tmyk.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fg83.tmyk.client.task.StringSearchTask;
import net.fg83.tmyk.client.task.TraceSearchTask;

import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;


public class WikiCommandCallback implements ClientCommandRegistrationCallback {

    //MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess) {
        commandDispatcher.register(ClientCommandManager.literal("wiki")
                .executes(context -> {
                    new Thread(new TraceSearchTask()).start();
                    return Command.SINGLE_SUCCESS;
                })
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            String query = StringArgumentType.getString(context, "message");
                            new Thread(new StringSearchTask(query)).start();
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }

}
