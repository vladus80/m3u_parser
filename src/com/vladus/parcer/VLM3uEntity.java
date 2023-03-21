package com.vladus.parcer;

import java.util.HashMap;
import java.util.Objects;

public class VLM3uEntity {

    private String nameChannel;
    private String uriChannel;
    private String groupChannel;
    private String logoChannel;
    private String epgChannelId;
    private HashMap<String, String> attrsSectionEXTINF;

    public HashMap<String, String> getAttrsSectionEXTINF() {
        return attrsSectionEXTINF;
    }


    public String getNameChannel() {
        return nameChannel;
    }

    protected void setNameChannel(String nameChannel) {
        this.nameChannel = nameChannel;
    }

    public String getUriChannel() {
        return uriChannel;
    }

    protected void setUriChannel(String uriChannel) {
        this.uriChannel = uriChannel;
    }

    public String getGroupChannel() {
        return groupChannel;
    }

    protected void setGroupChannel(String groupChannel) {
        this.groupChannel = groupChannel;
    }

    public String getLogoChannel() {
        return logoChannel;
    }

    protected void setLogoChannel(String logoChannel) {
        this.logoChannel = logoChannel;
    }

    public String getEpgChannelId() {
        return epgChannelId;
    }

    protected void setEpgChannelId(String epgChannelId) {
        this.epgChannelId = epgChannelId;
    }

    protected  void setAttrsSectionEXTINF(HashMap<String, String> attrsSectionEXTINF) {
        this.attrsSectionEXTINF = attrsSectionEXTINF;
    }


    @Override
    public String toString() {
        return "VLM3uEntity{" +
                "nameChannel='" + nameChannel + '\'' +
                ", uriChannel='" + uriChannel + '\'' +
                ", groupChannel='" + groupChannel + '\'' +
                ", logoChannel='" + logoChannel + '\'' +
                ", epgChannelId='" + epgChannelId + '\'' +
                ", attrsSectionEXTINF=" + attrsSectionEXTINF +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VLM3uEntity)) return false;
        VLM3uEntity vlm3u = (VLM3uEntity) o;
        return Objects.equals(getNameChannel(), vlm3u.getNameChannel()) && Objects.equals(getUriChannel(), vlm3u.getUriChannel()) && Objects.equals(getGroupChannel(), vlm3u.getGroupChannel()) && Objects.equals(getLogoChannel(), vlm3u.getLogoChannel()) && Objects.equals(getEpgChannelId(), vlm3u.getEpgChannelId()) && Objects.equals(getAttrsSectionEXTINF(), vlm3u.getAttrsSectionEXTINF());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNameChannel(), getUriChannel(), getGroupChannel(), getLogoChannel(), getEpgChannelId(), getAttrsSectionEXTINF());
    }
}
