package de.newsarea.homecockpit.fsuipc2net.net.domain;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetMessage {

    private static final String REGEX_MAIN = "([A-Z]+)\\s*\\[\\s*(.*)\\s*\\]";
    private static final String REGEX_ITEMS = "\\[\\s*(.*?)\\s*\\]";

    public enum Command {
        CHANGED,
        MONITOR,
        VALUE,
        READ,
        WRITE
    }

    private final Command type;
    private final Collection<NetMessageItem> items;
    private final int timeOfBlocking;

    public Command getCommand() {
        return type;
    }

    public Collection<NetMessageItem> getItems() {
        return items;
    }

    public OffsetItem[] getOffsetItems() {
        List<OffsetItem> offsetItems = new ArrayList<>();
        for(NetMessageItem item : items) {
            offsetItems.add(new OffsetItem(item.getOffsetIdent().getOffset(), item.getOffsetIdent().getSize(), item.getByteArray()));
        }
        return offsetItems.toArray(new OffsetItem[items.size()]);
    }

    public NetMessage(Command type, Collection<NetMessageItem> items, int timeOfBlocking) {
        this.type = type;
        this.items = items;
        this.timeOfBlocking = timeOfBlocking;
    }

    public NetMessage(Command type, OffsetItem offsetItem, int timeOfBlocking) {
        this(type, Arrays.asList(new NetMessageItem(new OffsetIdent(offsetItem.getOffset(), offsetItem.getSize()), offsetItem.getValue())), timeOfBlocking);
    }

    public NetMessage(Command type, OffsetItem offsetItem) {
        this(type, offsetItem, 0);
    }

    public static NetMessage fromJson(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        // get items
        Collection<NetMessageItem> items = new ArrayList<>();
        JsonArray jsonItems = jsonObject.getAsJsonArray("items");
        for(int i=0; i < jsonItems.size(); i++) {
            JsonObject jsonItem = jsonItems.get(i).getAsJsonObject();
            items.add(NetMessageItem.fromJsonObject(jsonItem));
        }
        // ~
        Command command = Command.valueOf(jsonObject.get("cmd").getAsString());
        int timeOfBlocking = 0;
        if(jsonObject.has("timeOfBlocking")) {
            timeOfBlocking = jsonObject.get("timeOfBlocking").getAsInt();
        }
        return new NetMessage(command, items, timeOfBlocking);
    }

    public static NetMessage fromString(String message) {
        Pattern p = Pattern.compile(REGEX_MAIN);
        Matcher m = p.matcher(message);
        if(m.find()) {
            Command command = Command.valueOf(m.group(1));
            // ~
            List<NetMessageItem> items = new ArrayList<>();
            Pattern pOffsetObj = Pattern.compile(REGEX_ITEMS);
            Matcher mOffsetObj = pOffsetObj.matcher(m.group(2));
            while(mOffsetObj.find()) {
                items.add(NetMessageItem.fromString(mOffsetObj.group()));
            }
            return new NetMessage(command, items, 0);
        }
        throw new IllegalArgumentException("invalid input - " + message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetMessage)) return false;
        NetMessage that = (NetMessage) o;
        return items.equals(that.items) && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + items.hashCode();
        return result;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cmd", type.toString());
        if(timeOfBlocking > 0) {
            jsonObject.addProperty("timeOfBlocking", timeOfBlocking);
        }
        JsonArray jsonArray = new JsonArray();
        for(NetMessageItem netMessageItem : items) {
            jsonArray.add(netMessageItem.toJson());
        }
        jsonObject.add("items", jsonArray);
        return jsonObject;
    }

    public String toJsonString() {
        return toJson().toString();
    }

    public String toString() {
        StringBuilder strBld = new StringBuilder();
        strBld.append(type);
        strBld.append("[");
        for(NetMessageItem item : items) {
            strBld.append("[");
            strBld.append(item.toString());
            strBld.append("]");
        }
        strBld.append("]");
        return strBld.toString();
    }

}
