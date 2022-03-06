package com.example.zanewearableswatch;

import com.google.maps.model.LatLng;

import java.util.Arrays;
import java.util.stream.Collectors;

//POJO for step data parsed from call to Directions API
public class Step {
    private String instruction;
    private String maneuver;
    private int distance;
    private int duration;
    private LatLng startLocation;
    private LatLng endLocation;

    public Step() {
        instruction = "";
        maneuver = "";
        distance = 0;
        duration = 0;
        startLocation = null;
        endLocation = null;
    }

    public Step(String ins, String man, int dis, int dur, LatLng start, LatLng end) {
        instruction = ins;
        maneuver = man;
        distance = dis;
        duration = dur;
        startLocation = start;
        endLocation = end;
    }

    public String getInstruction()  {return instruction;}
    public String getManeuver()     {return maneuver;}
    public int getDistance()        {return distance;}
    public int getDuration()        {return duration;}
    public LatLng getStart()        {return startLocation;}
    public LatLng getEnd()          {return endLocation;}

    public void setInstruction(String ins)  {instruction = ins;}
    public void setManeuver(String man)     {maneuver = man;}
    public void setDistance(int dis)        {distance = dis;}
    public void setDuration(int dur)        {duration = dur;}
    public void setStart(LatLng start)      {startLocation = start;}
    public void setEnd(LatLng end)          {endLocation = end;}

    public String toString() {
        String[] arr = {instruction, maneuver, String.valueOf(distance), String.valueOf(distance),
                startLocation.toString(), endLocation.toString()};
        return Arrays.stream(arr).collect(Collectors.joining(", "));
    }
}