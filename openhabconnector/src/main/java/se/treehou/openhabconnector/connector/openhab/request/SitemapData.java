package se.treehou.openhabconnector.connector.openhab.request;

import android.net.Uri;

public class SitemapData {

    private String name;
    private String label;
    private String link;
    private ServerData server;

    public SitemapData() {}

    public SitemapData(SitemapData sitemap) {
        name = sitemap.getName();
        label = sitemap.getLabel();
        link = sitemap.getLink();
        server = sitemap.getServer();
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

    public boolean isLocal(){
        ServerData server = getServer();
        Uri uri = Uri.parse(link);

        try{
            return uri.getHost().equals(Uri.parse(server.getUrl()).getHost());
        }catch (Exception e){}

        return false;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ServerData getServer() {
        return server;
    }

    public void setServer(ServerData server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof SitemapData){
            SitemapData sitemap = (SitemapData) o;

            return (this.name.equals(sitemap.name) && this.server.equals(sitemap.getServer()));
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return name.hashCode()+(name.hashCode()+this.getServer().getName().hashCode());
    }
}
