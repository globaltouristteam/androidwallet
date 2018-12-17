package io.goshisoft.marketcap;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.wallet.crypto.trustapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.goshisoft.marketcap.entity.Datum;
import io.goshisoft.marketcap.entity.USD;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {

    private final ArrayList<Datum> originObject = new ArrayList<>();
    private final ArrayList<Datum> filter = new ArrayList<>();
    private final Listener listener;
    private int sortType = 0;
    private boolean ASC = true;
    private String newText;

    MarketAdapter(Listener listener) {
        this.listener = listener;
    }

    void addAll(List<Datum> data) {
        originObject.addAll(data);
        filter.addAll(data);
        filter(newText);
        sort();
        notifyDataSetChanged();
    }

    private void sort() {
        Collections.sort(originObject, (datum, t1) -> {
            switch (sortType) {
                case 1:
                    return ASC ? datum.getName().compareToIgnoreCase(t1.getName()) : t1.getName().compareToIgnoreCase(datum.getName());
                case 2: {
                    USD usd = datum.getQuotes().getUSD();
                    USD usd1 = t1.getQuotes().getUSD();
                    return ASC ? Double.compare(usd.getPercentChange24h(), usd1.getPercentChange24h())
                            : Double.compare(usd1.getPercentChange24h(), usd.getPercentChange24h());
                }
                case 3: {
                    USD usd = datum.getQuotes().getUSD();
                    USD usd1 = t1.getQuotes().getUSD();
                    return ASC ? Double.compare(usd.getPrice(), usd1.getPrice())
                            : Double.compare(usd1.getPrice(), usd.getPrice());
                }
                default:
                    return ASC ? datum.getRank().compareTo(t1.getRank()) :
                            t1.getRank().compareTo(datum.getRank());
            }
        });
    }

    int getSortType() {
        return sortType;
    }

    void setSortType(int sortType) {
        this.sortType = sortType;
        sort();
        notifyDataSetChanged();
    }

    boolean setASC() {
        this.ASC = !this.ASC;
        sort();
        notifyDataSetChanged();
        return this.ASC;
    }

    void setASC(boolean ASC) {
        this.ASC = ASC;
    }

    void clean() {
        originObject.clear();
        filter.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(originObject.get(position));
    }

    @Override
    public int getItemCount() {
        return originObject.size();
    }

    void filter(String newText) {
        this.newText = newText;
        if (TextUtils.isEmpty(newText)) {
            originObject.clear();
            originObject.addAll(filter);
            notifyDataSetChanged();
        } else {
            originObject.clear();
            for (Datum datum : filter) {
                String name = datum.getName().toLowerCase(Locale.US);
                String symbol = datum.getId().toString().toLowerCase(Locale.US);
                String search = newText.toLowerCase(Locale.US);
                if (name.contains(search) || symbol.contains(search)) {
                    originObject.add(datum);
                }
            }
            notifyDataSetChanged();
        }
    }

    interface Listener {
        void onItemClick(Datum datum);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_id;
        private TextView tv_name;
        private TextView tv_price;
        private TextView tv_percent;
        private ImageView imv_coin;
        private RequestManager glide;

        ViewHolder(View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_percent = itemView.findViewById(R.id.tv_percent);
            imv_coin = itemView.findViewById(R.id.imv_coin);
            glide = Glide.with(itemView.getContext());
        }

        void bind(Datum object) {
            tv_id.setText(String.valueOf(object.getRank()));
            tv_name.setText(object.getName());
            USD usd = object.getQuotes().getUSD();
            DecimalFormat df = new DecimalFormat("##.##");
            df.setMaximumFractionDigits(8);
            tv_price.setText(String.format("%s%s", "$", df.format(usd.getPrice())));
            tv_percent.setText(String.format("%s%%", usd.getPercentChange24h()));
            boolean enable = (usd.getPercentChange24h() > 0);
            tv_price.setEnabled(enable);
            tv_percent.setEnabled(enable);
            String url = "https://s2.coinmarketcap.com/static/img/coins/32x32/" + object.getId() + ".png";
            glide.load(url).into(imv_coin);
            itemView.setOnClickListener(view -> listener.onItemClick(object));
        }
    }
}
