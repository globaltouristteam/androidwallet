package io.goshisoft.marketcap.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MarketDetailResponse {

    @SerializedName("price_usd")
    @Expose
    public
    ArrayList<float[]> priceUSD;

}
