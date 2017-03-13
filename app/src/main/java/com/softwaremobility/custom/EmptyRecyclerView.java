package com.softwaremobility.custom;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by darkgeat on 3/12/17.
 */
public class EmptyRecyclerView extends RecyclerView {

    private View progressView;
    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean hasDefaultItem = false;

    final private AdapterDataObserver observer = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }

    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void checkIfEmpty() {
        final boolean noData = hasDefaultItem ? (((getAdapter() != null && getAdapter().getItemCount() == 1)) || getAdapter() == null) :
                (((getAdapter() != null && getAdapter().getItemCount() == 0)) || getAdapter() == null);
        if(noData){
            if(!swipeRefreshLayout.isRefreshing()){
                if (progressView != null){
                    progressView.setVisibility(GONE);
                }
                if (textView != null) {
                    textView.setVisibility(VISIBLE);
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setVisibility(VISIBLE);
                }
            }else{
                if (hasDefaultItem && getAdapter().getItemCount() == 1){
                    if (progressView != null){
                        progressView.setVisibility(GONE);
                    }
                    if (textView != null) {
                        textView.setVisibility(VISIBLE);
                    }
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setVisibility(VISIBLE);
                    }
                }else {
                    if (progressView != null) {
                        progressView.setVisibility(GONE);
                    }
                    if (textView != null) {
                        textView.setVisibility(VISIBLE);
                    }
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setVisibility(VISIBLE);
                    }
                }
            }

        }else{
            if (progressView != null) {
                progressView.setVisibility(GONE);
            }
            if (textView != null) {
                textView.setVisibility(GONE);
            }
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }

    public void setProgressView(View emptyView) {
        this.progressView = emptyView;
    }

    public void setEmptyTextView(TextView textView) {
        this.textView = textView;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public void hasDefaultItem(boolean value){
        hasDefaultItem = value; }
}