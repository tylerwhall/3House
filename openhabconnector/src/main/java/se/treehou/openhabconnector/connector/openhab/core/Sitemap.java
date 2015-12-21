package se.treehou.openhabconnector.connector.openhab.core;

import se.treehou.openhabconnector.connector.openhab.request.SitemapData;

public class Sitemap {

    private static final String TAG = "Sitemap";

    private String name;
    private String label;
    private String link;
    private LinkedPage homepage;
    private Server server;

    public Sitemap() {}

    public Sitemap(SitemapData data) {
        name = data.getName();
        label = data.getLabel();
        link = data.getLink();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LinkedPage getHomepage() {
        return homepage;
    }

    public void setHomepage(LinkedPage homepage) {
        this.homepage = homepage;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setServerId(Server server) {
        this.server = server;
    }

    public Server getServer(){
        return server;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Sitemap){
            Sitemap sitemap = (Sitemap) o;

            return (this.name.equals(sitemap.name) && this.getServer()==sitemap.getServer());
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return name.hashCode()+(name.hashCode()+this.getServer().getName().hashCode());
    }
}
