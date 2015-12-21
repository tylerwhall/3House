package se.treehou.openhabconnector.connector.openhab.core;

import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import se.treehou.openhabconnector.connector.ConnectorUtil;
import se.treehou.openhabconnector.connector.openhab.request.WidgetMappingData;

public class Widget {

    public static final String TAG                  = "WidgetData";

    // TODO convert to enum
    public static final String TYPE_DUMMY           = "Dummy";
    public static final String TYPE_FRAME           = "Frame";
    public static final String TYPE_SWITCH          = "Switch";
    public static final String TYPE_COLORPICKER     = "Colorpicker";
    public static final String TYPE_SELECTION       = "Selection";
    public static final String TYPE_CHART           = "Chart";
    public static final String TYPE_IMAGE           = "Image";
    public static final String TYPE_VIDEO           = "Video";
    public static final String TYPE_WEB             = "Webview";
    public static final String TYPE_TEXT            = "Text";
    public static final String TYPE_SLIDER          = "Slider";
    public static final String TYPE_GROUP           = "Group";
    public static final String TYPE_SETPOINT        = "Setpoint";

    private String widgetId;
    private String type;
    private String icon;
    private String label;

    // Used for charts
    private String period;
    private String service;

    private int minValue=0;
    private int maxValue=100;
    private float step=1;

    private String url;
    private Item item;
    private List<Widget> widget;
    private List<WidgetMappingData> mapping = new ArrayList<>();
    private LinkedPage linkedPage;

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return (icon == null || icon.equals("none") || icon.equals("image") || icon.equals("")) ? null : icon;
    }

    public String getIconPath() {
        String icon = getIcon();
        return (icon != null) ? "/images/"+icon+".png" : null;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label != null ? label : "";
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public List<Widget> getWidget() {
        if(widget == null) return new ArrayList<>();
        return widget;
    }

    public String getUrl() {
        return url;
    }

    public String getBaseUrl(){
        try {
            return new URL(ConnectorUtil.getBaseUrl(item.getLink())).toString();
        } catch (Exception e) {
            Log.e(TAG, "Failed to generate base url", e);
        }
        return "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWidget(List<Widget> widget) {
        this.widget = widget;
    }

    public LinkedPage getLinkedPage() {
        return linkedPage;
    }

    public void setLinkedPage(LinkedPage linkedPage) {
        this.linkedPage = linkedPage;
    }

    public List<WidgetMappingData> getMapping() {
        return mapping;
    }

    public void setMapping(List<WidgetMappingData> mapping) {
        this.mapping = mapping;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public boolean needUpdate(Widget widget){

        if(getWidget().size() != widget.getWidget().size()){
            Log.d(TAG, "needUpdate1 " + getWidget().size() + " : " + widget.getWidget().size());
            return true;
        }
        for(int i=0; i < getWidget().size(); i++){
            if (getWidget().get(i).needUpdate(widget.getWidget().get(i))){
                Log.d(TAG, "needUpdate2 " + getWidget().size() + " : " + widget.getWidget().size());
                return true;
            }
        }

        boolean needUpdate = (!getType().equals(widget.getType()));

        if(needUpdate){
            Log.d(TAG, "needUpdate3 " + getType() + ":" + widget.getType() + ":" + getType().equals(widget.getType()) + " id " + getWidgetId() + ":" + widget.getWidgetId() + ":" + getWidgetId().equals(widget.getWidgetId()));
        }

        return needUpdate;
    }
}
