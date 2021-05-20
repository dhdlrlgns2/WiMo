package com.example.wimo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class PrivacyListAdapter extends RecyclerView.Adapter<PrivacyListAdapter.PrivacyListViewHolder> {

    private Context context;

    private List<PrivacyInfo> privacyList;

    public PrivacyListAdapter(List<PrivacyInfo> privacyList) {
        this.privacyList = privacyList;
    }

    @NonNull
    @Override
    public PrivacyListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_privacy_item, viewGroup, false);
        PrivacyListViewHolder holder = new PrivacyListViewHolder(holderView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PrivacyListViewHolder moduleViewHolder, int position) {
        moduleViewHolder.text.setText(privacyList.get(position).getLocation() + "(" + privacyList.get(position).getTime() + ")");
    }

    @Override
    public int getItemCount() {
        return privacyList.size();
    }

    public static class PrivacyListViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;

        public PrivacyListViewHolder(View view) {
            super(view);

            text = view.findViewById(R.id.text_item);
        }
    }
}
