package xyz.denl.bluetoothcollector.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.denl.bluetoothcollector.R;
import xyz.denl.bluetoothcollector.util.OnAdapterSupport;


/**
 * Created by tw on 2017-10-01.
 */
public class BluetoothListCustomAdapter extends RecyclerView.Adapter<BluetoothListCustomAdapter.ViewHolder> {

    // UI
    private Context context;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<HashMap<String, String>> list;

//    // 무한 스크롤
//    private OnLoadMoreListener onLoadMoreListener;
//    private int visibleThreshold = 10;
//    private int lastVisibleItem, totalItemCount;
//    private boolean loading = false;

    // 생성자
    public BluetoothListCustomAdapter(Context context, ArrayList<HashMap<String, String>> list, RecyclerView recyclerView, OnAdapterSupport listener) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            recyclerView.addOnScrollListener(new ScrollListener() {
                @Override
                public void onHide() {
                    hideViews();
                }

                @Override
                public void onShow() {
                    showViews();
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //recycler view에 반복될 아이템 레이아웃 연결
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HashMap<String, String> map = list.get(position);
        final int pos = position;

        holder.tv_text.setText(map.get("name") + "\n" + map.get("address") + "\n" + map.get("rssi"));

    }



    @Override
    public int getItemCount() {
        return this.list.size();
    }

    private void hideViews() {
        if (onAdapterSupport != null) {
            onAdapterSupport.hideView();
        }
    }

    private void showViews() {
        if (onAdapterSupport != null) {
            onAdapterSupport.showView();
        }
    }

    // 무한 스크롤
//    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
//        this.onLoadMoreListener = onLoadMoreListener;
//    }
//    public void setLoaded() {
//        loading = false;
//    }

    public abstract class ScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 30;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

//            totalItemCount = linearLayoutManager.getItemCount();
//            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                // End has been reached
//                // Do something
//                loading = true;
//                if (onLoadMoreListener != null) {
//                    onLoadMoreListener.onLoadMore();
//                }
//            }
            // 여기까지 무한 스크롤

            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }

            if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                scrolledDistance += dy;
            }
            // 여기까지 툴바 숨기기
        }

        public abstract void onHide();
        public abstract void onShow();

    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_text;

        public ViewHolder(View v) {
            super(v);
            tv_text = (TextView)v.findViewById(R.id.text);
        }

    }

}
