package com.example.wimo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wimo.PrivacyDetailActivity;
import com.example.wimo.PrivacyInfoDB;
import com.example.wimo.R;
import com.example.wimo.data.HostInfo;
import com.example.wimo.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HostActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        TextView textView = findViewById(R.id.text_title);
        textView.setText("주최자 정보");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(this);
        mRecyclerView.setAdapter(mAdapter);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHost();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }
    }

    private void addHost() {
        EditText input = new EditText(this);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                HostInfo info = new HostInfo();
                info.setIdCode(text);
                long id = PrivacyInfoDB.getInstance().insertHost(info);
                if (id > 0) {
                    info.setId(id);
                    mAdapter.addHost(info);
                }
                dialog.dismiss();
            }
        };
        Utils.showDialog(this, "ID Code 추가", input, listener);
    }

    private void editHost(HostInfo info, int position) {
        EditText input = new EditText(this);
        input.setText(info.getIdCode());
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                info.setIdCode(text);
                boolean success = PrivacyInfoDB.getInstance().updateHost(info);
                if (success) {
                    mAdapter.notifyItemChanged(position);
                }
                dialog.dismiss();
            }
        };
        Utils.showDialog(this, "ID Code 수정", input, listener);
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.VHItem> {
        private final ArrayList<HostInfo> mHosts = new ArrayList<>();
        private final WeakReference<HostActivity> mActivity;

        Adapter(HostActivity activity) {
            mActivity = new WeakReference<>(activity);
            mHosts.addAll(PrivacyInfoDB.getInstance().getHost());
        }

        private void addHost(HostInfo info) {
            mHosts.add(info);
            notifyItemInserted(mHosts.size() - 1);
        }

        @NonNull
        @Override
        public VHItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_message,parent,false);
            return new VHItem(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VHItem holder, int position) {
            HostInfo info = mHosts.get(position);
            holder.textView.setText(info.getIdCode());
        }

        @Override
        public int getItemCount() {
            return mHosts.size();
        }

        class VHItem extends RecyclerView.ViewHolder{
            TextView textView;
            ImageButton buttonDelete;

            VHItem(final View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.text_view);
                buttonDelete = itemView.findViewById(R.id.button_delete);
                ImageView imageView = itemView.findViewById(R.id.image_view);
                imageView.setImageResource(R.drawable.ic_qr_code);

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        HostInfo info = mHosts.get(position);
                        boolean success = PrivacyInfoDB.getInstance().deleteHost(info);
                        if (success) {
                            mHosts.remove(position);
                            notifyItemRemoved(position);
                        }
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getAdapterPosition();
                        HostInfo info = mHosts.get(position);
                        if (mActivity.get() != null) mActivity.get().editHost(info, position);
                        return true;
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("privacy", "On");
                        Intent intent = new Intent(itemView.getContext(), PrivacyDetailActivity.class);
                        itemView.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });

            }
        }
    }

}