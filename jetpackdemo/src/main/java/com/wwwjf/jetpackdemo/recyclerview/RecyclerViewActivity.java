package com.wwwjf.jetpackdemo.recyclerview;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wwwjf.jetpackdemo.R;
import com.wwwjf.jetpackdemo.databinding.ActivityRecyclerviewDemoBinding;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private ActivityRecyclerviewDemoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recyclerview_demo);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recyclerview_demo);

        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("222");
        list.add("333");
        list.add("444");
        list.add("555");
        list.add("666");
//        binding.rvRecyclerviewDemo.setLayoutManager(new RvManager(this,2, RecyclerView.VERTICAL,false));
//        binding.rvRecyclerviewDemo.setLayoutManager(new GridLayoutManager(this,2, RecyclerView.VERTICAL,false));
        binding.rvRecyclerviewDemo.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        binding.rvRecyclerviewDemo.setAdapter(new RecycleAdapter(this,list));
    }
}
