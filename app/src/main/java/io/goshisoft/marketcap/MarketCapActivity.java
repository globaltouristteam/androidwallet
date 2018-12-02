package io.goshisoft.marketcap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.router.ManageWalletsRouter;
import com.wallet.crypto.trustapp.viewmodel.BaseNavigationActivity;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MarketCapActivity extends BaseNavigationActivity implements SearchView.OnQueryTextListener {

    private int start = 0;
    private Disposable disposable;
    private MarketAdapter adapter;
    private SwipeRefreshLayout refresh_layout;

    public static Intent getIntent(Context context, boolean createBottom) {
        Intent intent = new Intent(context, MarketCapActivity.class);
        intent.putExtra("type", createBottom);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_cap);
        toolbar();
        Intent intent = getIntent();
        boolean createBottom = intent.getBooleanExtra("type", false);
        if (createBottom) {
            initBottomNavigation();
            setBottomMenu(R.menu.menu_main_network);
            setBottomSelectedItem(R.id.action_log);
            findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
        } else {
            enableDisplayHomeAsUp();
            findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
        }
        adapter = new MarketAdapter();
        RecyclerView recyclerView = findViewById(R.id.list);
        CustomRadioGroup rbId = findViewById(R.id.rbId);
        CustomRadioGroup rbName = findViewById(R.id.rbName);
        CustomRadioGroup rbHour = findViewById(R.id.rbHour);
        CustomRadioGroup rbPrice = findViewById(R.id.rbPrice);
        rbId.setListener(() -> {
            boolean ASC = adapter.setASC();
            int resId = ASC ? R.drawable.radio_up : R.drawable.radio_down;
            changeRadio(rbId, resId);
        });
        rbName.setListener(() -> {
            boolean ASC = adapter.setASC();
            int resId = ASC ? R.drawable.radio_up : R.drawable.radio_down;
            changeRadio(rbName, resId);
        });
        rbHour.setListener(() -> {
            boolean ASC = adapter.setASC();
            int resId = ASC ? R.drawable.radio_up : R.drawable.radio_down;
            changeRadio(rbHour, resId);

        });
        rbPrice.setListener(() -> {
            boolean ASC = adapter.setASC();
            int resId = ASC ? R.drawable.radio_up : R.drawable.radio_down;
            changeRadio(rbPrice, resId);
        });

        RadioGroup rgSort = findViewById(R.id.rgSort);
        rgSort.setOnCheckedChangeListener((radioGroup, id) -> {
            adapter.setASC(true);
            int _resId = R.drawable.radio_up;
            switch (id) {
                case R.id.rbId: {
                    adapter.setSortType(0);
                    changeRadio(rbId, _resId);
                    break;
                }
                case R.id.rbName: {
                    adapter.setSortType(1);
                    changeRadio(rbName, _resId);
                    break;
                }
                case R.id.rbHour: {
                    adapter.setSortType(2);
                    changeRadio(rbHour, _resId);
                    break;
                }
                case R.id.rbPrice: {
                    adapter.setSortType(3);
                    changeRadio(rbPrice, _resId);
                    break;
                }
            }
        });
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(() -> {
            refresh_layout.setRefreshing(true);
            start = 0;
            adapter.clean();
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            getListCoin(start);
        });
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        getListCoin(0);
    }

    private void changeRadio(RadioButton rb, int resId) {
        rb.setButtonDrawable(ContextCompat.getDrawable(this, resId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log: {
                return true;
            }
            case R.id.action_send:
            case R.id.action_my_address:
            case R.id.action_my_tokens:
            case R.id.action_transaction: {
                new ManageWalletsRouter().open(this, false);
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    private void getListCoin(int start) {
        HashMap<String, Object> queries = new HashMap<>();
        queries.put("limit", 100);
        switch (adapter.getSortType()) {
            case 1:
                queries.put("sort", "rank");
                break;
            case 2:
                queries.put("sort", "percent_change_24h");
                break;
            case 3:
                queries.put("sort", "volume_24h");
                break;
            default:
                queries.put("sort", "id");
                break;
        }
        queries.put("convert", "BTC");
        queries.put("start", start);
        queries.put("structure", "array");
        disposable = RestAdapter.createApi(MarketApi.class).getListCoin(queries)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marketResponse -> {
                    refresh_layout.setRefreshing(false);
                    List<Datum> datum = marketResponse.getData();
                    if (datum != null) {
                        adapter.addAll(datum);
                        if (datum.size() >= 100) {
                            this.start += 100;
                            getListCoin(this.start);
                        }
                    }
                }, throwable -> {

                });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return true;
    }
}
