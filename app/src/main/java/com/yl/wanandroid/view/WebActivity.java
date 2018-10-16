package com.yl.wanandroid.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yl.wanandroid.R;
import com.yl.wanandroid.app.Constant;
import com.yl.wanandroid.base.BaseActivity;
import com.yl.wanandroid.view.collect.CollectFunction;

import butterknife.BindView;

public class WebActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnLongClickListener {
    @BindView(R.id.tb_web)
    Toolbar tbWeb;
    @BindView(R.id.wv_web)
    WebView wvWeb;
    @BindView(R.id.srl_web)
    SwipeRefreshLayout srlWeb;
    private BottomSheetDialog dialog;
    private String picUrl;

    public static void openWebPage(Activity activity, String url, boolean isCollected, int originId, int id) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra(Constant.KEY_WEB_URL, url);
        intent.putExtra(Constant.KEY_IS_COLLECTED, isCollected);
        intent.putExtra(Constant.KEY_ORIGIN_ID, originId);
        intent.putExtra(Constant.KEY_ID, id);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_web;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initView() {
        tbWeb.inflateMenu(R.menu.menu_toolbar_web);
        srlWeb.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        MenuItem item = tbWeb.getMenu().findItem(R.id.item_collect);
        item.setChecked(getIntent().getBooleanExtra(Constant.KEY_IS_COLLECTED, false));
        if (item.isChecked()) item.setIcon(R.drawable.ic_collected_white);
        wvWeb.getSettings().setJavaScriptEnabled(true);
        wvWeb.setOnLongClickListener(this);
        wvWeb.setWebViewClient(new WebViewClient() {
            private boolean isFailed;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isFailed = false;
                wvWeb.setVisibility(View.INVISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebViewActivity.openExternalUrl(WebActivity.this, url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                isFailed = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                srlWeb.setRefreshing(false);
                if (!isFailed) wvWeb.setVisibility(View.VISIBLE);
                else showNetError();
            }
        });
        wvWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                tbWeb.setTitle(title);
            }
        });
        initBottomDialog();
    }

    private void initBottomDialog() {
        String[] items = new String[]{"打开图片链接", "保存图片", "识别图中二维码"};
        dialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.dialog_bottom_sheet_web, null);
        ListView lvItem = inflate.findViewById(R.id.lv_item);
        lvItem.setAdapter(new ArrayAdapter<>(this, R.layout.item_bottom_dialog, items));
        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        WebViewActivity.openExternalUrl(WebActivity.this, picUrl);
                        break;
                    case 1:
                        showMsg("未完成的功能");
                        break;
                    case 2:
                        showMsg("未完成的功能");
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });
        inflate.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
    }

    @Override
    public void initListener() {
        tbWeb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tbWeb.setOnMenuItemClickListener(this);
        srlWeb.setOnRefreshListener(this);
    }

    @Override
    public void initData() {
        srlWeb.setRefreshing(true);
        wvWeb.loadUrl(getIntent().getStringExtra(Constant.KEY_WEB_URL));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_collect:
                CollectFunction.newInstance().handleArticleCollect(this, item, getIntent());
                return true;
            case R.id.item_share:
                return true;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        wvWeb.reload();
    }

    @Override
    public boolean onLongClick(View v) {
        WebView.HitTestResult result = ((WebView) v).getHitTestResult();
        if (result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE ||
                result.getType() == WebView.HitTestResult.IMAGE_TYPE) {
            if (TextUtils.isEmpty(result.getExtra())) {
                showMsg("获取图片失败");
            } else {
                picUrl = result.getExtra();
                dialog.show();
            }
        }
        return false;
    }
}
