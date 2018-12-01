package io.goshisoft.marketcap;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface MarketApi {

    @GET("/v2/ticker")
    Observable<MarketResponse> getListCoin(@QueryMap Map<String, Object> queries);
}
