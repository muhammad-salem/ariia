package org.ariia.network;

/**
 * It had to be HTTP server - not HTTPS,
 * so ISP can redirect request if no Quota available for user,
 * --> so we can stop any future request
 *
 * @author salem
 */
public enum CheckServer {


    GoogleStatic(
            "Google Static Server",
            "http://www.gstatic.com/generate_204"),
    Firefox_Portal(
            "FireFox Detect Portal Server",
            "http://detectportal.firefox.com/success.txt"),
    Ubuntu_Connectivity(
            "Ubuntu Connectivity Check Server",
            "http://connectivity-check.ubuntu.com/");


    private String serverName;
    private String url;

    CheckServer(String serverName, String url) {
        this.serverName = serverName;
        this.url = url;
    }

    public String serverName() {
        return serverName;
    }

    public String url() {
        return url;
    }


}