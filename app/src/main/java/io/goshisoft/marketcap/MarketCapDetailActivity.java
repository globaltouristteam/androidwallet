package io.goshisoft.marketcap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnDrawListener;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.ui.BaseActivity;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MarketCapDetailActivity extends BaseActivity implements OnDrawListener {

    private Datum datum;
    private Disposable disposable;
    private LineChart chart;
    private ProgressBar progress;

    public static Intent getIntent(Context context, Datum datum) {
        Intent intent = new Intent(context, MarketCapDetailActivity.class);
        intent.putExtra("datum", datum);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail);
        toolbar();
        enableDisplayHomeAsUp();
        progress = findViewById(R.id.progress);

        chart = findViewById(R.id.chart);
        chart.getLegend().setEnabled(false);
        chart.setOnDrawListener(this);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        datum = (Datum) getIntent().getSerializableExtra("datum");
        showDetail(datum);
        loadByDate(Type.TODAY);
        RadioGroup rgTime = findViewById(R.id.rgTime);
        rgTime.setOnCheckedChangeListener((radioGroup, id) -> {
            switch (id) {
                case R.id.rbToday: {
                    loadByDate(Type.TODAY);
                    break;
                }
                case R.id.rbWeek: {
                    loadByDate(Type.WEEK);
                    break;
                }
                case R.id.rbMonth: {
                    loadByDate(Type.MONTH);
                    break;
                }
                case R.id.rb3Month: {
                    loadByDate(Type.MONTH_3);
                    break;
                }
                case R.id.rb6Month: {
                    loadByDate(Type.MONTH_6);
                    break;
                }
                case R.id.rbYear: {
                    loadByDate(Type.YEAR);
                    break;
                }
                case R.id.rbAll: {
                    loadByDate(Type.ALL);
                    break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    private void loadByDate(Type type) {
        int number = 0;
        switch (type) {
            case TODAY:
                number = 1;
                break;
            case WEEK:
                number = 7;
                break;
            case MONTH:
                number = 30;
                break;
            case MONTH_3:
                number = 90;
                break;
            case MONTH_6:
                number = 180;
                break;
            case YEAR:
                number = 365;
                break;
            case ALL:
                number = 9999;
                break;
        }

        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, date - number);
        Date start = calendar.getTime();
        Date end = new Date();
        Timestamp timestampStart = new Timestamp(start.getTime());
        Timestamp timestampEnd = new Timestamp(end.getTime());
        loadChart(timestampStart.getTime(), timestampEnd.getTime(), datum.getWebsiteSlug());
    }

    private void loadChart(long timeStart, long timeEnd, String name) {
        progress.setVisibility(View.VISIBLE);
        String url = String.format(Locale.US, "https://graphs2.coinmarketcap.com/currencies/%s/%d/%d/", name, timeStart, timeEnd);
        disposable = RestAdapter.createApi(MarketApi.class).getChart(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> showChart(response.priceUSD), throwable -> {
                    throwable.printStackTrace();
                    progress.setVisibility(View.GONE);
                });
    }

    private void showChart(ArrayList<Float[]> priceUSD) {
        progress.setVisibility(View.GONE);
        ArrayList<Entry> values = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        if (priceUSD != null && !priceUSD.isEmpty()) {
            for (Float[] value : priceUSD) {
                String x = df.format(value[0]);
                String y = df.format(value[1]);
                values.add(new Entry(Float.parseFloat(x), Float.parseFloat(y)));
            }
        }
        int color = ContextCompat.getColor(this, R.color.colorPrimary);
        LineDataSet set1 = new LineDataSet(values, "DataSet");
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setLineWidth(1.8f);
        set1.setCircleRadius(4f);
        set1.setCircleColor(color);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(color);
        set1.setFillColor(color);
        set1.setFillAlpha(100);

        // create a data object with the data sets
        LineData data = new LineData(set1);
        chart.setData(data);
        chart.invalidate();
    }

    private void showDetail(Datum datum) {
        setTitle(datum.getName());
        Quotes quotes = datum.getQuotes();
        USD usd = quotes.getUSD();
        BTC btc = quotes.getBTC();
        TextView tv_price = findViewById(R.id.tv_price);
        TextView tv_price_btc = findViewById(R.id.tv_price_btc);
        TextView tv_market = findViewById(R.id.tv_market);
        TextView tv_volume_24 = findViewById(R.id.tv_volume_24);
        TextView tv_available = findViewById(R.id.tv_available);
        TextView tv_total_supply = findViewById(R.id.tv_total_supply);
        TextView tv_1h = findViewById(R.id.tv_1h);
        TextView tv_1d = findViewById(R.id.tv_1d);
        TextView tv_1w = findViewById(R.id.tv_1w);

        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);

        tv_price.setText(String.format("$%s(%s%%)", df.format(usd.getPrice()), usd.getPercentChange24h()));
        tv_price_btc.setText(getString(R.string.bit_coin, df.format(btc.getPrice()), btc.getPercentChange24h()));

        tv_price.setEnabled(usd.getPercentChange24h() > 0.0);
        tv_price_btc.setEnabled(btc.getPercentChange24h() > 0.0);

        tv_market.setText(String.format("$%s", df.format(usd.getMarketCap())));
        tv_volume_24.setText(String.format("$%s", df.format(usd.getVolume24h())));

        tv_available.setText(String.format("$%s", df.format(datum.getCirculatingSupply())));
        tv_total_supply.setText(String.format("$%s", df.format(datum.getTotalSupply())));

        tv_1h.setText(String.format("%s%%", usd.getPercentChange1h()));
        tv_1d.setText(String.format("%s%%", usd.getPercentChange24h()));
        tv_1w.setText(String.format("%s%%", usd.getPercentChange7d()));

        tv_1h.setEnabled(usd.getPercentChange1h() > 0.0);
        tv_1d.setEnabled(usd.getPercentChange24h() > 0.0);
        tv_1w.setEnabled(usd.getPercentChange7d() > 0.0);
    }

    @Override
    public void onEntryAdded(Entry entry) {

    }

    @Override
    public void onEntryMoved(Entry entry) {

    }

    @Override
    public void onDrawFinished(DataSet<?> dataSet) {
        chart.getLegendRenderer().computeLegend(chart.getData());
    }

    enum Type {
        TODAY,
        WEEK,
        MONTH,
        MONTH_3,
        MONTH_6,
        YEAR,
        ALL
    }
}
