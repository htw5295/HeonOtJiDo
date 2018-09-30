package kr.ac.cnu.heonotjido.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeoCode {

    @SerializedName("result")
    @Expose
    public Result result;

    public class Addrdetail {

        @SerializedName("country")
        @Expose
        public String country;
        @SerializedName("sido")
        @Expose
        public String sido;
        @SerializedName("sigugun")
        @Expose
        public String sigugun;
        @SerializedName("dongmyun")
        @Expose
        public String dongmyun;
        @SerializedName("ri")
        @Expose
        public String ri;
        @SerializedName("rest")
        @Expose
        public String rest;

    }

    public class Item {

        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("addrdetail")
        @Expose
        public Addrdetail addrdetail;
        @SerializedName("isRoadAddress")
        @Expose
        public Boolean isRoadAddress;
        @SerializedName("point")
        @Expose
        public Point point;

    }

    public class Point {

        @SerializedName("x")
        @Expose
        public Double x;
        @SerializedName("y")
        @Expose
        public Double y;

    }

    public class Result {

        @SerializedName("total")
        @Expose
        public Integer total;
        @SerializedName("userquery")
        @Expose
        public String userquery;
        @SerializedName("items")
        @Expose
        public List<Item> items = null;

    }
}

