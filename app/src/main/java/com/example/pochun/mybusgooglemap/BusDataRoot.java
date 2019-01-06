package com.example.pochun.mybusgooglemap;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BusDataRoot
{
        @JsonProperty("EssentialInfo")
        public BusDataEssentialInfo essentialInfo;
        @JsonProperty("BusInfo")
        public List<BusDataInfo> busInfos;
}

class BusDataInfo
{
    public String ProviderID;
    public String StationID;
    public String GoBack;
    public Integer Speed;
    public String DutyStatus;
    public String DataTime;
    public Double Latitude;
    public Integer RouteID;
    public Double Longitude;
    public String BusID;
    public String BusStatus;
    public String Azimuth;
    public String CarType;
    public String CarID;
}

class BusDataLocation {
    public String name;
    public String CenterName;
}

class BusDataEssentialInfo
{
    @JsonProperty("Location")
    public BusDataLocation location;
    @JsonProperty("UpdateTime")
    public String UpdateTime;
    @JsonProperty("CoordinateSystem")
    public String CoordinateSystem;
}
