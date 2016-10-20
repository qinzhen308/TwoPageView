package com.paulz.towpageview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TwoPageLayout mLayout;
    WebView mWebView;
    RadioGroup mTabs;
    ListView mListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }
    private List<Map<String ,String>> getListData(){
        List<Map<String ,String>> data=new ArrayList<>();
        Random r=new Random(100);
        for(int i=0;i<20;i++){
            Map<String,String> map=new HashMap<>();
            map.put("key_text", "第二页第二选项卡 listview---item+"+i);
            map.put("key_text2", "呵呵 listview---item+"+i);
            data.add(map);
        }
        return data;
    }

    private void initView() {
        mLayout=(TwoPageLayout)findViewById(R.id.main_content);
        mWebView=(WebView)findViewById(R.id.web_view);

        mTabs=(RadioGroup)findViewById(R.id.radio_group);
        mListview=(ListView)findViewById(R.id.lv);
        initWebView();
        mWebView.loadUrl("http://blog.csdn.net/qinzhen308/article/details/52789199");
        //设置滑动会冲突的视图进去
        mLayout.setScrollerChildren(mWebView,mListview);

        //第二页切换标签
        mTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_detail:
                        mListview.setVisibility(View.GONE);
                        mWebView.setVisibility(View.VISIBLE);
                        mLayout.changeCurScrollerChild(0);
                        break;
                    case R.id.rb_comments:
                        mWebView.setVisibility(View.GONE);
                        mListview.setVisibility(View.VISIBLE);
                        mLayout.changeCurScrollerChild(1);
                        break;
                }
            }
        });
        mTabs.check(R.id.rb_detail);
        mListview.setAdapter(new SimpleAdapter(MainActivity.this,getListData(),R.layout.item_adapter,new String[]{"key_text","key_text2"},new int[]{R.id.text1,R.id.text2}));
        //监听切换到那一页了
        mLayout.setOnPageChangeListener(new TwoPageLayout.OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, View currentView, boolean bySelf) {
                Toast.makeText(getApplicationContext(),"当前页码："+page+",是否自己改变："+bySelf,Toast.LENGTH_LONG).show();
                if(page==1){
//                    mWebView.loadUrl("http://blog.csdn.net/qinzhen308/article/details/52789199");
                    if(mTabs.getCheckedRadioButtonId()==R.id.rb_detail){
                        mWebView.loadUrl("http://blog.csdn.net/qinzhen308/article/details/52789199");
                    }
                }
            }
        });
    }

    private void initWebView() {
        // LayoutParams lp=new
        // LayoutParams(LayoutParams.MATCH_PARENT,ScreenUtil.HEIGHT);
        // mWebView.setLayoutParams(lp);
        // contentInSV.addView(mWebView);
        WebSettings wSet = mWebView.getSettings();
        wSet.setDefaultTextEncodingName("UTF-8");
        wSet.setJavaScriptEnabled(true);
        mWebView.setVerticalScrollbarOverlay(true); // 指定的垂直滚动条有叠加样式
        wSet.setUseWideViewPort(true);// 设定支持viewport
        wSet.setLoadWithOverviewMode(true);
        wSet.setBuiltInZoomControls(false);
        wSet.setSupportZoom(false);
//        wSet.setDisplayZoomControls(false);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }
        });
    }
}
