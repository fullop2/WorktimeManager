package com.android.worktimemanager.Class;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Collections.synchronizedList;

public class ApiExplorer {


    public List getData(int year) throws ExecutionException,InterruptedException {
        ExecutorService mPool = Executors.newFixedThreadPool(5);
        HolidayCallable r = new HolidayCallable();
        r.setYear(year);
        Future<List> mFuture = mPool.submit(r);
        mPool.shutdown();
        return mFuture.get();
    }

    List getDataFromServer(int year)
    {
        List holidays =  synchronizedList(new ArrayList());
        try {

           for (int i = 1; i <= 12; ++i) {
                URL url = new URL(getURL(year, i));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                BufferedReader rd;
                int a = conn.getResponseCode();
                if (a >= 200 && a <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
                holidays.add(sb.toString());
            }
        }
        catch(IOException e)
        {
            return null;
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
        return holidays;
    }
    String getURL(int year, int month) throws IOException
    {
        StringBuffer urlBuilder = new StringBuffer("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=B9VyN5ku%2BjF7wgmYlKXhs1mf8PqPP18lFFfY57eiO2gLkFKienpErxwQf6CI3JdBAHX3MArdSzmakeCjHxSecg%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("solYear","UTF-8") + "=" + URLEncoder.encode(Integer.toString(year), "UTF-8")); /*연*/
        urlBuilder.append("&" + URLEncoder.encode("solMonth","UTF-8") + "=" + URLEncoder.encode(String.format("%02d",month), "UTF-8")); /*월*/
        return urlBuilder.toString();
    }

    private class HolidayCallable implements Callable<List>
    {
        int year;
        void setYear(int year)
        {
            this.year = year;
        }

        @Override
        public List call() throws Exception {
            return getDataFromServer(year);
        }
    }
}