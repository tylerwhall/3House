package treehou.se.habit.core.db;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import treehou.se.habit.R;

@Table(name = "Server")
public class ServerDB extends Model {

    @Column(name = "name")
    private String name = "";

    @Column(name = "username")
    private String username = "";

    @Column(name = "password")
    private String password = "";

    @Column(name = "localUrl")
    private String localUrl = "";

    @Column(name = "remoteUrl")
    private String remoteUrl = "";

    @Column(name = "majorVersion")
    private int majorVersion = 0;

    private boolean autodiscovered = false;

    public ServerDB(){}

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

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getUrl(){
        return localUrl!=null?localUrl:remoteUrl;
    }

    public boolean requiresAuth() {
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }

    public boolean isAutodiscovered() {
        return autodiscovered;
    }

    public void setAutodiscovered(boolean autodiscovered) {
        this.autodiscovered = autodiscovered;
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

    public static List<ServerDB> getServers(){
        return new Select()
                .from(ServerDB.class)
                .execute();
    }

    public static int numServers() {
        return new Select()
                .from(ServerDB.class)
                .count();
    }

    public boolean haveRemote(){
        return (getRemoteUrl()!=null && !getRemoteUrl().trim().equals(""));
    }

    public boolean haveLocal(){
        return (getLocalUrl()!=null && !getLocalUrl().trim().equals(""));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ServerDB){
            ServerDB server = (ServerDB) obj;

            return ((getUsername() != null || this.getPassword().equals(server.getUsername())) &&
                    (getPassword() == null || getPassword().equals(server.getPassword())) &&
                    (getLocalUrl() == null || getLocalUrl().equals(server.getLocalUrl())) &&
                    (getRemoteUrl() == null || getRemoteUrl().equals(server.getRemoteUrl())));
        }

        return super.equals(obj);
    }
}
