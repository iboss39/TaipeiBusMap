package com.example.pochun.mybusgooglemap;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BusEstimateTimeRoot {

    @JsonProperty("EssentialInfo")
    public EstimateTimeEssentialInfo essentialInfo;
    @JsonProperty("BusInfo")
    public List<EstimateTimeBusInfo> busInfos;
}

class EstimateTimeBusInfo {
    String RouteID;
    String StopID;
    String EstimateTime;
    String GoBack;
}

class EstimateTimeLocation {
    public String name;
    public String CenterName;
}

class EstimateTimeEssentialInfo
{
    @JsonProperty("Location")
    public EstimateTimeLocation location;
    @JsonProperty("UpdateTime")
    public String UpdateTime;
    @JsonProperty("CoordinateSystem")
    public String CoordinateSystem;
}