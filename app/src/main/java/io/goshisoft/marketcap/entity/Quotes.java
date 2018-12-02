
package io.goshisoft.marketcap.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Quotes  implements Serializable {

    @SerializedName("USD")
    @Expose
    private USD uSD;
    @SerializedName("BTC")
    @Expose
    private USD bTC;

    public USD getUSD() {
        return uSD;
    }

    public void setUSD(USD uSD) {
        this.uSD = uSD;
    }

    public USD getBTC() {
        return bTC;
    }

    public void setBTC(USD bTC) {
        this.bTC = bTC;
    }

}
