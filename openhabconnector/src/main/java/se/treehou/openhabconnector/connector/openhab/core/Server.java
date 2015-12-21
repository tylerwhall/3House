package se.treehou.openhabconnector.connector.openhab.core;

import android.content.Context;
import android.text.TextUtils;

import se.treehou.openhabconnector.R;

public class Server {

    private String name = "";
    private String username = "";
    private String password = "";
    private String url = "";
    private int majorVersion = 0;

    public Server(){}

    public String getName() {
        return name;
    }

    public String getDisplayName(Context context){
        return TextUtils.isEmpty(name) ? context.getString(R.string.home) : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

    public boolean requiresAuth() {
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }

    /**
     * Set the major version of server
     * @param majorVersion
     */
    public void setMajorVersion(int majorVersion){
        this.majorVersion = majorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Server){
            Server server = (Server) obj;

            return ((getUsername() != null || this.getPassword().equals(server.getUsername())) &&
                    (getPassword() == null || getPassword().equals(server.getPassword())) &&
                    (getUrl() == null || getUrl().equals(server.getUrl())));
        }

        return super.equals(obj);
    }
}
