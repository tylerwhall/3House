package se.treehou.openhabconnector.connector.openhab.request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LinkedPageData {

    private String id;
    private String link;
    private String title;
    private boolean leaf;
    private List<WidgetData> widget;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getActionbarTitle(){
        return title.replaceAll("(\\[)(.*)(\\])", "$2");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBaseUrl(){
        try {
            URL url = new URL(link);
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), "", null).toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPath(){
        try {
            URL url = new URL(link);
            if(url.getPath().length() > 0) {
                return url.getPath().substring(1);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<WidgetData> getWidget() {
        return widget!=null?widget:new ArrayList<WidgetData>();
    }

    public void setWidgets(List<WidgetData> widget) {
        this.widget = widget;
    }
}
