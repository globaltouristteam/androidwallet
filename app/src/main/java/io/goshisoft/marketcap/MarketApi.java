package io.goshisoft.marketcap;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface MarketApi {

    @GET("/v2/ticker")
    Observable<MarketResponse> getListCoin(@QueryMap Map<String, Object> queries);

    @GET
    Observable<MarketDetailResponse> getChart(@Url String url);
}
