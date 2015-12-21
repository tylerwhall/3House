package se.treehou.openhabconnector.connector.openhab.request;

/**
 * Created by matti on 2015-12-21.
 */
public class WidgetMappingData {

    private String command;
    private String label;

    public WidgetMappingData(String command, String label) {
        this.command = command;
        this.label = label;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
