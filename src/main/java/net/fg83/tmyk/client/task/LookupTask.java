package net.fg83.tmyk.client.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LookupTask implements Runnable{
    String address;
    //MinecraftClient client = MinecraftClient.getInstance();
    boolean multi;
    List<MutableText> messages = new ArrayList<>();

    public LookupTask(String address, boolean multi){
        this.address = address;
        this.multi = multi;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        String response = search(address);
        JsonObject json = JsonParser.parseString(response).getAsJsonObject().get("query").getAsJsonObject();
        JsonArray arr = json.getAsJsonArray("search");

        int max = multi ? 5 : 1;

        for (int i = 0; i < max; i++){
            String url = "https://minecraft.wiki/w/" + arr.get(i).getAsJsonObject().get("title").getAsString().replace(" ", "_");
            MutableText messageText = Text.literal("● " + url.replace("https://minecraft.wiki/w/", "").replace("_", " "));
            Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create(url))).withUnderline(true).withColor(Formatting.GOLD);
            messages.add(messageText);
        }
    }

    private String search(String address) {
        String response = "";
        try {
            URL url = URI.create(address).toURL();

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str = in.readLine();
            in.close();
            if (str != null) {
                response = str;
            }
        }
        catch (IOException e1) {
            response = e1.getMessage();
        }
        return response;
    }
}
