package se.treehou.openhabconnector.connector;

import java.util.HashSet;
import java.util.Set;

import se.treehou.openhabconnector.connector.openhab.request.ItemData;

public class Constants {

    public static final String CHART_URL = "/chart";
    public static final String QUERY_GROUPS = "groups";
    public static final String QUERY_ITEMS  = "items";

    public static final String COMMAND_ON       = "ON";
    public static final String COMMAND_OFF      = "OFF";
    public static final String COMMAND_UNINITIALIZED = "Uninitialized";

    public static final String COMMAND_OPEN     = "OPEN";
    public static final String COMMAND_CLOSE    = "CLOSE";

    public static final String COMMAND_UP       = "UP";
    public static final String COMMAND_DOWN     = "DOWN";
    public static final String COMMAND_STOP     = "STOP";
    public static final String COMMAND_MOVE     = "MOVE";

    public static final String COMMAND_INCREMENT = "INCREASE";
    public static final String COMMAND_DECREMENT = "DECREASE";

    public static final String COMMAND_COLOR    = "%d,%d,%d";

    public static final String ITEM_VOICE_COMMAND = "VoiceCommand";

    public static final String HEADER_AUTHENTICATION = "Authorization";

    public static final Set<String> SUPPORT_SWITCH = new HashSet<>();
    static {
        SUPPORT_SWITCH.add(ItemData.TYPE_GROUP);
        SUPPORT_SWITCH.add(ItemData.TYPE_SWITCH);
        SUPPORT_SWITCH.add(ItemData.TYPE_STRING);
        SUPPORT_SWITCH.add(ItemData.TYPE_NUMBER);
        SUPPORT_SWITCH.add(ItemData.TYPE_CONTACT);
        SUPPORT_SWITCH.add(ItemData.TYPE_COLOR);
    }

    public static final Set<String> SUPPORT_INC_DEC = new HashSet<>();
    static {
        SUPPORT_SWITCH.add(ItemData.TYPE_GROUP);
        SUPPORT_INC_DEC.add(ItemData.TYPE_NUMBER);
        SUPPORT_INC_DEC.add(ItemData.TYPE_DIMMER);
    }
}
