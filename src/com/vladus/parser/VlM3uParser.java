package com.vladus.parser;

import org.jetbrains.annotations.NotNull;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class VlM3uParser {

    public static @NotNull List<VLM3uEntity> parse (@NotNull String filePlaylist){


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
    private static @NotNull List<String> getBlocks(@NotNull String filePlaylist) {

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
                    blocks.add(String.valueOf(block));
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
    private static HashMap<String, String>  getAttrsSectionEXTINF(@NotNull String block){
        HashMap<String, String> keyValues = new HashMap<>(); // Создаем новый объект HashMap для хранения пар "ключ=значение"

        String sectionEXTINF = Arrays.stream(block.split("\\n")).findFirst().get(); // Выдергиваем первую строку из блока и
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

        return keyValues;
    }


    // Возвращает значение группы к которой относится канал
    private static String getGroupChannel(@NotNull String block){
        //Берем строки и фильтруем на наличие тэга #EXTGRP
        List <String> listBlocks;

        // Для начала ищем в секции #EXTGRP,
        listBlocks = Arrays.stream(block.split("\\n")).filter(s -> s.startsWith("#EXTGRP")).collect(Collectors.toList());
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

    // Возвращает имя канала, тянем из секции #EXTINF (идет последний после длительности)
    private static String getNameChannel(@NotNull String block) {
        return Arrays.stream(block.split("\\n")).findFirst().get().split(",")[1];
    }

    // Возвращает uri канала
    private static String getUriChannel(@NotNull String block) {

        Optional<String> lastString = Arrays.stream(block.split("\\n"))
                                      .reduce((first, second) -> second);

        String uriChannel;
        uriChannel = lastString.orElse("No strings found");
        return uriChannel;
    }

    //Возвращает логотип канала
    private static String getLogoChannel(@NotNull String block) {

        return getAttrsSectionEXTINF(block).get("tvg-logo");
    }

    // Возвращает  EPGID канала
    private static String getEpgIdChannel(@NotNull String block) {

        return getAttrsSectionEXTINF(block).get("tvg-id");
    }


}
