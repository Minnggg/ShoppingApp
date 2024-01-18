package com.example.shoppingapp.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.example.shoppingapp.R;
import com.example.shoppingapp.adapter.LoaiSpAdapter;
import com.example.shoppingapp.databinding.ActivityMainBinding;
import com.example.shoppingapp.model.LoaiSp;
import com.example.shoppingapp.retrofit.ApiBanHang;
import com.example.shoppingapp.retrofit.RetrofitClient;
import com.example.shoppingapp.untils.Utils;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    private ActivityMainBinding binding;
    LoaiSpAdapter LoaiSpAdapter ;
    List<LoaiSp> listLoaiSp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        ActionBar();
        khoitaodata();
        
        if(isConnected(getApplicationContext())){
            ActionViewFlippter();
            getLoaiSanPham();
        }
        else {
            Toast.makeText(this, "Cần kết nối wifi hoặc dữ liệu di động", Toast.LENGTH_SHORT).show();

        }
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if (loaiSpModel.isSuccess()) {
                                listLoaiSp = loaiSpModel.getResult();
                                Log.d("TAG", "getLoaiSanPham: "+listLoaiSp.size());
                                LoaiSpAdapter = new LoaiSpAdapter(listLoaiSp,getApplicationContext());
                                binding.lvTrangChinh.setAdapter(LoaiSpAdapter);
                            }
                        },
                        throwable -> {
                            Log.e("TAG", "Error in getLoaiSanPham: " + throwable.getMessage());
                        }
                )
        );
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi!=null && wifi.isConnected())||(mobile!=null && mobile.isConnected())){
            return true;
        }else
        {
            return false;
        }
    }

    private void khoitaodata() {
        listLoaiSp = new ArrayList<>();
//        LoaiSpAdapter = new LoaiSpAdapter(listLoaiSp,getApplicationContext());
//        binding.lvTrangChinh.setAdapter(LoaiSpAdapter);
    }

    private void ActionViewFlippter() {
        List<String> quangcao = new ArrayList<>();
        quangcao.add("https://cdn.tgdd.vn/Products/Images/42/274018/samsung-galaxy-a24-black-thumb-600x600.jpg");
        quangcao.add("https://cdn.tgdd.vn/Products/Images/42/299034/oppo-find-n2-flip-purple-thumb-1-600x600-1-600x600.jpg");
        quangcao.add("https://cdn.tgdd.vn/Products/Images/42/251192/iphone-14-pro-max-den-thumb-600x600.jpg");
        quangcao.add("https://cdn.tgdd.vn/Products/Images/42/247508/iphone-14-pro-vang-thumb-600x600.jpg");
        quangcao.add("https://cdn.tgdd.vn/Products/Images/42/267211/Samsung-Galaxy-S21-FE-vang-1-2-600x600.jpg");

        for (String x: quangcao) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(x).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            binding.vfTrangChinh.addView(imageView);
        }
        binding.vfTrangChinh.setFlipInterval(3000);
        binding.vfTrangChinh.setAutoStart(true);
        Animation sile_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slider_home_in);
        Animation sile_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slider_home_out);
        binding.vfTrangChinh.setInAnimation(sile_in);
        binding.vfTrangChinh.setOutAnimation(sile_out);
    }

    private void ActionBar() {
        setSupportActionBar(binding.toolbarTrangChinh);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trang Chinh");
        binding.toolbarTrangChinh.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        binding.toolbarTrangChinh.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}