package se.treehou.openhabconnector.connector.openhab.request;

public class StateDescriptionData {

    private String pattern;

    private boolean readOnly;

    public StateDescriptionData() {}

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
