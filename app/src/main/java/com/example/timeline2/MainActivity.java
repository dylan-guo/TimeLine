package com.example.timeline2;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> titleList;
    private List<String> timeList;
    private TimeLineView timeLineView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initView() {
        timeLineView = findViewById(R.id.timeLineView);
        timeLineView.setPointStrings(titleList, (float) 3.5,timeList); //添加 数据源和步数

    }

    private void initData() {
        titleList = new ArrayList<>();
        titleList.add("待受理");
        titleList.add("已受理");
        titleList.add("处理中");
        titleList.add("处理完成");
        timeList = new ArrayList<>();
        timeList.add("1天");
        timeList.add("2天");
        timeList.add("3天");

    }

    @Override
    protected void onStop() {
        super.onStop();
        TimeLineView.num = 0;
    }
}
