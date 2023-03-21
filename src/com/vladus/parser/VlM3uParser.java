package com.vladus.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;



public class VlM3uParser {

    public static List<VLM3uEntity> parse (String filePlaylist){


        List<VLM3uEntity> vlm3uEntities = new ArrayList<>();
        List<String> blocks = getBlocks(filePlaylist);

        for (String block: blocks) {

            VLM3uEntity uEntity = new VLM3uEntity();
            uEntity.setAttrsSectionEXTINF(getAttrsSectionEXTINF(block)); //атрибуты
            uEntity.setNameChannel(getNameChannel(block)); // Имя канала
            uEntity.setLogoChannel(getLogoChannel(block)); // Лого канала
            uEntity.setUriChannel(getUriChannel(block));   // uri канала
            uEntity.setEpgChannelId(getEpgIdChannel(block)); //epgId канала
            uEntity.setGroupChannel(getGroupChannel(block)); // Группа канала

            vlm3uEntities.add(uEntity);
        }

        return vlm3uEntities;
    }


    // Возвращает блоки из файла m3u состоящие из строк начиная с #EXTINF и заканчива url канала
    private static List<String> getBlocks(String filePlaylist) {

        ArrayList<String> blocks = new ArrayList<String>();
        try {
            String line;
            StringBuilder block = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(filePlaylist));

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#EXTINF")) {
                    block.append(line).append("\n");
                } else if (line.startsWith("http")) {
                    block.append(line).append("\n");
                    blocks.add(block.toString());
                    block = new StringBuilder();
                } else {
                    if (!line.startsWith("#EXTM3U")) {
                        block.append(line).append("\n");
                    }
                }
            }
        reader.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filePlaylist);
            e.printStackTrace();
        }
        return blocks ;
    }


    // Возвращает атрибуты из секции #EXTINF строки блока в виде HashMap
    private static HashMap<String, String>  getAttrsSectionEXTINF(String block){
        HashMap<String, String> keyValues = new HashMap<>(); // Создаем новый объект HashMap для хранения пар "ключ=значение"

        String sectionEXTINF = block.lines().findFirst().get(); // Выдергиваем первую строку из блока и
        if(sectionEXTINF.contains("#EXTINF")){                  // проверяем на наличие в ней тэга #EXTINF"
            String inputAttrEXTINF = sectionEXTINF.split(",", 2)[0]; // Отделяем атрибуты
            String inputNameChannelEXTINF = sectionEXTINF.split(",", 2)[1]; // Отделяем имя канала, как правило после запятой в секции #EXTINF
            String[] pairs = inputAttrEXTINF.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2 && !keyValue[0].contains("\"")) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].replaceAll("\"", "").trim();
                    keyValues.put(key, value);
                }
            }
        } else {
            keyValues = null;
            System.out.println("Section not #EXTINF");
        }
        //System.out.println();
        return keyValues;
    }


    // Возвращает значение группы к которой относится канал
    private static String getGroupChannel(String block){
        //Берем строки и фильтруем на наличие тэга #EXTGRP
        List <String> listBlocks;

        // Для начала ищем в секции #EXTGRP,
        listBlocks = block.lines().filter(s -> s.startsWith("#EXTGRP")).collect(Collectors.toList());
        HashMap<String,String> stringHashMap;


        String groupChannel;
        if(listBlocks.size()>0){
            groupChannel = listBlocks.get(0).split(":")[1].trim();
        }else {
            //если  нет секции #EXTGRP то ищем в секции #EXTINFO в атрибутах
            stringHashMap = getAttrsSectionEXTINF(block);
            groupChannel = stringHashMap.get("group-title");
        }

        return groupChannel;

    }

    private static String getNameChannel(String block) {
        String nameChannel = block.lines().findFirst().get().split(",")[1];
        return nameChannel;
    }

    private static String getUriChannel(String block) {

        Optional<String> lastString = block.lines()
                .reduce((first, second) -> second);

        String uriChannel;
        if (lastString.isPresent()) {
            //System.out.println("Last string: " + lastString.get());
            uriChannel = lastString.get();
        } else {
            uriChannel =  "No strings found";
        }
        return uriChannel;
    }

    private static String getLogoChannel(String block) {

        String logoChannel = getAttrsSectionEXTINF(block).get("tvg-logo");
        return logoChannel;
    }

    private static String getEpgIdChannel(String block) {

        String epgIdChannel = getAttrsSectionEXTINF(block).get("tvg-id");
        return epgIdChannel;
    }


}
