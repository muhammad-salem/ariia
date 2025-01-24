package org.ariia.args;

import org.terminal.console.log.Level;

import java.util.Arrays;


public enum TerminalArgument {
    Url("-u", "--url"),
    InputFile("-i", "--input-file"),
    MetaLink("-m", "--meta-link"),
    Referer("-r", "--http-referer"),

    UserAgent("-ua", "--user-agent"),
    Header("-H", "--header"),
    CookieFile("-cf", "--cookie-file"),

    FileName("-o", "--file-name"),
    SavePath("-sp", "--save-path"),

    Tries("-t", "--tries"),
    Connection("-c", "--max-connection"),
    MaxItem("-n", "--num-download"),

    Insecure("-k", "--insecure"),

    Proxy("-p", "--proxy"),

    HttpProxy("-http", "--http-proxy"),
    HttpsProxy("-https", "--https-proxy"),

    SocksProxy("-socks", "--socks-proxy"),
    Socks4Proxy("-socks4", "--socks4-proxy"),
    Socks5Proxy("-socks5", "--socks5-proxy"),

    ProxyUsername("-pu", "--proxy-user"),
    ProxyPassword("-pp", "--proxy-password"),

    SSH("-s", "--ssh"),
    SSHUser("-su", "--ssh-user"),
    SSHPass("-sp", "--ssh-pass"),

    CheckFile("-ch", "--check-file"),
    ChunkSize("-cs", "--chunk-size"),
    DownloadPieces("-dp", "--download-pieces"),

    Stream("-st", "--stream"),

    DaemonService("-ds", "--daemon-service"),


    ServerPort("-port", "--server-port"),
    ServerHost("-host", "--server-host"),
    ServerResourceLocation("-rl", "--resource-location"),

    Help("-h", "--help"),
    Debug("-d", "--debug-level"),
    Version("-v", "--version"),
    Chrome("-ch", "--chrome");


    private final String full;
    private final String mini;

    TerminalArgument(String mini, String full) {
        this.mini = mini;
        this.full = full;
    }

    public static TerminalArgument argument(String line) {
        for (TerminalArgument arg : TerminalArgument.values()) {
            if (line.contentEquals(arg.mini) || line.contentEquals(arg.full)) {
                return arg;
            }
        }
        return null;
    }

    public static String GetStringArgument(String line) {
        for (TerminalArgument arg : TerminalArgument.values()) {
            if (line.contentEquals(arg.mini)) {
                return arg.mini;
            } else if (line.contentEquals(arg.full)) {
                return arg.full;
            }
        }
        return null;
    }

    public static String mini(String line) {
        for (TerminalArgument arg : TerminalArgument.values()) {
            if (line.contentEquals(arg.mini)) {
                return arg.mini;
            }
        }
        return null;
    }

    public static String full(String line) {
        for (TerminalArgument arg : TerminalArgument.values()) {
            if (line.contentEquals(arg.full)) {
                return arg.full;
            }
        }
        return null;
    }

    public static String help() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n ariia commend line download manager\n");
        builder.append("\n java - jar ariia.jar [-u] URL\n");
        for (TerminalArgument argument : TerminalArgument.values()) {
            if (argument == TerminalArgument.Chrome) continue;
            builder.append('\t');
            builder.append(argument.mini);
            builder.append("\t");
            builder.append(argument.full);
            if (argument.full.length() > 14)
                builder.append("\t");
            else if (argument.full.length() > 7)
                builder.append("\t\t");
            else
                builder.append("\t\t\t");
            builder.append(argument.doc());
            builder.append('\n');
        }
        return builder.toString();
    }

    public String getMini() {
        return mini;
    }

    public String getFull() {
        return full;
    }

    @Override
    public String toString() {
        return full;
    }

    public boolean isPair() {
        switch (this) {
            case DaemonService:
            case Help:
            case Version:
            case Insecure:
                return false;
            default:
                return true;
        }
    }

    public boolean isOne() {
        return !isPair();
    }

    public String doc() {
        switch (this) {
            case Url:
                return ("[-u] add new link/url to download manager");
            case InputFile:
                return ("download from text file - list of urls");
            case MetaLink:
                return ("download from  metal ink text/xml file - list of urls on deficient servers for the same downloadable file");

            case Referer:
                return ("set referer header for that link");
            case UserAgent:
                return ("set user-agent header while download");
            case Header:
                return ("set one/more different header(s) for that link\n\t\t\tadd cookie(s) while download");
            case CookieFile:
                return ("add cookie(s) from standard cookie file");

            case FileName:
                return ("save download link to file on hard-disk");
            case SavePath:
                return ("set directory of download process");

            case Tries:
                return ("number of tries on failed state, then give-up (0 for keep-try forever)");
            case Connection:
                return ("max connection for current session for each link");
            case MaxItem:
                return ("number of download links in queue, if more links, will be in waiting list");

            case Insecure:
                return ("TLS By default, every secure connection ariia makes\n\t\t\tis verified to be secure before the transfer takes place.\n\t\t\tThis option makes curl skip the verification step and proceed without checking");

            case Proxy:
                return ("set proxy to http://host:port[8080]/, support protocols http, https ans socks4/5");
            case HttpProxy:
                return ("use http proxy [host:port] format");
            case HttpsProxy:
                return ("use https proxy [host:port] format");
            case SocksProxy:
                return ("use socks proxy [host:port] format");
            case Socks5Proxy:
                return ("use socks5 proxy [host:port] format");
            case Socks4Proxy:
                return ("use socks4 proxy [host:port] format");
            case ProxyUsername:
                return ("Specify the username for authentication on a proxy server");
            case ProxyPassword:
                return ("Specify the password for authentication on a proxy server");

            case SSH:
                return ("use ssh connection as proxy - [remote_host:port], not supported yet");
            case SSHUser:
                return ("set ssh user name - remote login user name");
            case SSHPass:
                return ("set remote login password, if non will be asked from terminal");

            case CheckFile:
                return ("check download file if is complete, and try to complete it");
            case ChunkSize:
                return ("length of chunk/segment to check");
            case DownloadPieces:
                return ("index of pieces which need download. it could be in format of string as \"2 52 22 783 \" or a file holding the index separated by '\\n'");

            case Stream:
                return ("stream URL One download connection");


            case ServerPort:
                return ("run web application on port (default port 8080)");
            case ServerHost:
                return ("run web application for local interface (default is any all)");
            case ServerResourceLocation:
                return ("run web application with resource location directory path");

            //r

            case DaemonService:
                return ("start ariia as daemon service");

            case Help:
                return ("print this message");
            case Version:
                return ("display the version of ariia");
            case Debug:
                return ("display logging, Levels: " + Arrays.toString(Level.values()));


            default:
        }
        return "";
    }

}
