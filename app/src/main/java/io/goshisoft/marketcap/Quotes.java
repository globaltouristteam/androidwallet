
package io.goshisoft.marketcap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Quotes  implements Serializable {

    @SerializedName("USD")
    @Expose
    private USD uSD;
    @SerializedName("BTC")
    @Expose
    private BTC bTC;

    public USD getUSD() {
        return uSD;
    }

    public void setUSD(USD uSD) {
        this.uSD = uSD;
    }

    public BTC getBTC() {
        return bTC;
    }

    public void setBTC(BTC bTC) {
        this.bTC = bTC;
    }

}
