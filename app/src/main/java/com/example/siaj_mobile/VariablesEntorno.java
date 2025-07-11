package com.example.siaj_mobile;
public class VariablesEntorno {

//    public static String getHost(){
//        return BuildConfig.DB_HOST;
//    }
//
//    public static String getPort(){
//        return BuildConfig.DB_PORT;
//    }
//
//    public static String getDatabase(){
//        return BuildConfig.DB_DATABASE;
//    }
//
//    public static String getUser(){
//        return BuildConfig.DB_USER;
//    }
//
//    public static String getPassword(){
//        return BuildConfig.DB_PASSWORD;
//    }
//
//    public static String getJWTSecret(){
//        return BuildConfig.JWT_SECRET;
//    }
//
//    public static String getJWTExpiration(){
//        return BuildConfig.JWT_EXPIRATION;
//    }

    public static String getServerHost(){
        return BuildConfig.SERVER_HOST;
    }

//    public static String getServerPort(){
//        return BuildConfig.SERVER_PORT;
//    }

    public static String getServerURL(){
        return getServerHost();
    }
}