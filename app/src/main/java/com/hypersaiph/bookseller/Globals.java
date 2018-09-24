package com.hypersaiph.bookseller;

public class Globals {
    //hosts
    //home
    //private final static String HOST = "192.168.1.2:8000";
    //private net
    private final static String HOST = "192.168.1.10:8000";
    //workshop
    //private final static String HOST = "192.168.99.131:8000";
    //production
    //private final static String HOST = "gvsphp.herokuapp.com";
    // client vars
    public final static String CLIENT_ID = "1";
    public final static String CLIENT_SECRET = "FYmRPDg0ovWO6qpNzSAVVrS37v4NWNl19voRucGY";
    //urls
    private final static String PROTOCOL = "http";
    public final static String API_BASE_URL = PROTOCOL+"://"+HOST;
    public final static String API_URL = PROTOCOL+"://"+HOST+"/api/v1";
    public final static String SHARED_PREFERENCES = "system_settings";
    //resources
    public final static String API_BOOK_RESOURCE = PROTOCOL+"://"+HOST+"/images/books/";
}
