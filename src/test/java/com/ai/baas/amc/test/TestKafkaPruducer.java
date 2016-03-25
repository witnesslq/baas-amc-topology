package com.ai.baas.amc.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
/**
 * 
 * Date: 2016年3月24日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class TestKafkaPruducer {
    private String encoding = "UTF-8";

    public final static String FIELD_SPLIT = new String(new char[] { (char) 1 });

    public final static String RECORD_SPLIT = new String(new char[] { (char) 2 });

    public final static String PACKET_HEADER_SPLIT = ",";

    private String service_id = "GPRS";// PILE

    private String tenant_id = "VIV-BYD";

    private String fileName = "";

    public void send(String path) {
        InputStream in = null;
        BufferedReader reader = null;
        File file;
        try {
            file = FileUtils.getFile(path);
            fileName = file.getName();
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in, Charsets.toCharset(encoding)));
            List<String> lines = new ArrayList<String>();
            String sLine = reader.readLine();
            while (sLine != null) {
                lines.add(sLine);
                sLine = reader.readLine();
            }
            String message = assembleMessage(lines);
            // System.out.println("message----"+message);
            ProducerProxy.getInstance().sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private String assembleMessage(List<String> lines) {
        StringBuilder busData = new StringBuilder();
        String[] fieldNames = null;
        for (int i = 0; i < lines.size(); i++) {
            StringBuilder record = new StringBuilder();
            busData.append(tenant_id).append(FIELD_SPLIT);
            busData.append(service_id).append(FIELD_SPLIT);
            fieldNames = StringUtils.splitPreserveAllTokens(lines.get(i), ";");
            for (String fieldName : fieldNames) {
                record.append(fieldName).append(FIELD_SPLIT);
            }
            busData.append(record.substring(0, record.length() - 1)).append(RECORD_SPLIT);
        }
        return busData.substring(0, busData.length() - 1).toString();
    }

    public static void main(String[] args) {
        String path = "E:\\data.txt";
        TestKafkaPruducer simulator = new TestKafkaPruducer();
        simulator.send(path);
    }

}
