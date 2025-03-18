package net.fg83.tmyk.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TmykClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(new WikiCommandCallback());
    }

    public static String buildSearchUrl(String query, boolean fetchMultiple) {
        String url = "https://minecraft.wiki/api.php?action=query&format=json&list=search&srsort=just_match&srnamespace=0%7C4%7C14%7C10010&srlimit=";
        if (fetchMultiple) {
            url += 5;
        }
        else {
            url += 1;
        }
        url += "&srsearch=" + query;

        return url;
    }
    public static void sendMessages(List<MutableText> messages) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        player.sendMessage(buildHeader(), false);
        messages.forEach(message -> player.sendMessage(message, false));


    }

    public static MutableText buildHeader(){
        MutableText message = Text.literal("Results:");
        message.formatted(Formatting.GREEN, Formatting.ITALIC);

        return message;
    }
}
