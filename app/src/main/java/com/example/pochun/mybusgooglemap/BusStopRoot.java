package com.example.pochun.mybusgooglemap;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BusStopRoot {
    @JsonProperty("EssentialInfo")
    public StopBusEssentialInfo essentialInfo;
    @JsonProperty("BusInfo")
    public List<StopBusInfo> busInfos;
}

class StopBusInfo
{
    public String Id;
    public Integer routeId;
    public String nameZh;
    public String nameEn;
    public String seqNo;
    public String DataTime;
    public String pgp;
    public Double longitude;
    public Double latitude;
    public String goBack;
    public String address;
    public String stopLocationId;
    public String showLon;
    public String showLat;
    public String vector;
}

class StopBusLocation {
    public String name;
    public String CenterName;
}

class StopBusEssentialInfo
{
    @JsonProperty("Location")
    public StopBusLocation location;
    @JsonProperty("UpdateTime")
    public String UpdateTime;
    @JsonProperty("CoordinateSystem")
    public String CoordinateSystem;
}