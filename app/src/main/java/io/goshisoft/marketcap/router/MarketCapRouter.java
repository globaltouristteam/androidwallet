package io.goshisoft.marketcap.router;

import android.content.Context;
import android.content.Intent;

import io.goshisoft.marketcap.MarketCapActivity;

public class MarketCapRouter {
    public void open(Context context, boolean isClearStack, boolean bottomBar) {
        Intent intent = new Intent(context, MarketCapActivity.class);
        if (isClearStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        intent.putExtra("type", bottomBar);
        context.startActivity(intent);
    }
}
