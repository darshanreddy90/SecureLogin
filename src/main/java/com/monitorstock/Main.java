package com.monitorstock;

import com.google.gson.Gson;
import controller.CompanyController;
import data.Stock;
import data.StockDetails;
import database.DBManager;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.Spark;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static spark.Spark.*;

/**
 * Created by tejas on 4/5/2016.
 */
public class Main {
    static CompanyController controller;
    static Gson gson;

    public static void main(String[] args) {
        controller = new CompanyController();
        gson = new Gson();
        Main m= new Main();
        Spark.staticFileLocation("/");
        get("/",(req,res) -> m.render("/home.html"));
        get("/login",(req,res) -> m.render("/login.html"));
        post("/userLogin", (req, res) -> login(req,res));
        post("/mfaAuth", (req, res) -> mfa(req, res));
//        get("/list/:id", (req, res) -> listCompanies(req));
//        post("/stock/:id", (req, res) -> add(req));
//        get("/history/:id/:company/:startDate/:endDate", (req, res) -> getHistory(req));
//        delete("stock/:id/:company", (req, res) -> deleteStock(req));

    }

    private static Object mfa(Request req, Response res) {
        Main m = new Main();
        String screenHeight = req.session().attribute("screenHeight");
        String screenWidth = req.session().attribute("screenWidth");
        String timeZone = req.session().attribute("timeZone");
        String ip = req.ip();
        String uaString = req.userAgent();
        String email = req.session().attribute("email");
        if (req.queryParams("mfa").equals("445321")) {
            addFingerPrintToDb(email,screenHeight,screenWidth,timeZone,ip,uaString);
            return m.render("/home.html");

        }else {
            return m.render("/mfa.html");
        }

    }

    private static void addFingerPrintToDb(String email, String screenHeight, String screenWidth, String timeZone, String ip, String uaString) {
        new DBManager().addFingerPrint(email, screenHeight,screenWidth,timeZone,ip, uaString);
    }

    private static Object login(Request req, Response res) {
        Main m = new Main();
        req.session(true);
        String screenHeight = req.queryParams("screenHeight");
        String screenWidth = req.queryParams("screenWidth");
        String timeZone = req.queryParams("timeZone");
        String email = req.queryParams("email");
        String password = req.queryParams("passwd");
        String ip = req.ip();
        String uaString = req.userAgent();

        System.out.println("Login request details : height: "+screenHeight+"width :"+screenWidth+"timeZone :"+timeZone+"email :"+email+"ip :"+ip+"user agent :"+uaString+"password :"+password);
        boolean loginSuccess = new DBManager().login(email, password);

        if (!loginSuccess) {
          return m.render("/login.html");
        }

        if (!isMFARequired(req)) {
            return m.render("/home.html");
        }

        req.session().attribute("email",email);
        req.session().attribute("timeZone",timeZone);
        req.session().attribute("screenHeight",screenHeight);
        req.session().attribute("screenWidth",screenWidth);
        return m.render("/mfa.html");
    }

    private static boolean isMFARequired(Request req) {
        //TODO : compute the score and decide whether finger print is required
        if (req.cookie("user") != null) {
            return false;
        }
        return true;
    }

    public String render(String s) {
        try{

            URL url=getClass().getResource(s);
            Path path= Paths.get(url.toURI());
            return new String (Files.readAllBytes(path), Charset.defaultCharset());
        }catch (Exception e){
            System.out.println(e);;
        }
        return null;
    }

//
//    // monitorstockpriceinstance.ctoveuujovpy.us-east-1.rds.amazonaws.com:3306
//    private static Object listCompanies(Request req) throws IOException {
//        ArrayList<Stock> stocks = controller.listCompanies(Integer.parseInt(req.params("id")));
//        return gson.toJson(stocks);
//    }
//
//    private static Object getHistory(Request req) throws ParseException {
//        int userId = Integer.parseInt(req.params("id"));
//        String company = req.params("company");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
//        Date startDate = sdf.parse(req.params("startDate"));
//        Date endDate = sdf.parse(req.params("endDate"));
//        ArrayList<StockDetails> sdetails = controller.companyHistory(userId, company, startDate, endDate);
//
//        return gson.toJson(sdetails);
//    }
//
//    private static Object deleteStock(Request req) {
//        int userId = Integer.parseInt(req.params("id"));
//        String company = req.params("company");
//        controller.deleteCompany(userId, company);
//        return "deleted" + req.params("company");
//    }
//
//
//    private static String add(Request req) {
//        int userId = Integer.parseInt(req.params("id"));
//        String company = req.queryParams("stock");
//        boolean added = controller.addNewCompany(userId, company);
//        if (added) {
//            return company + " Added";
//        }
//        return "Error";
//    }
}
