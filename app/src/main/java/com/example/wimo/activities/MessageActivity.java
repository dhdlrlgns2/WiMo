package com.example.wimo.activities;

import android.content.DialogInterface;
import android.os.Bundle;
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

import com.example.wimo.PrivacyInfoDB;
import com.example.wimo.R;
import com.example.wimo.data.MessageInfo;
import com.example.wimo.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        TextView textView = findViewById(R.id.text_title);
        textView.setText("재난 문자 크롤링");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(this);
        mRecyclerView.setAdapter(mAdapter);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage();
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

    private void addMessage() {
        EditText input = new EditText(this);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                MessageInfo info = new MessageInfo();
                info.setAddress(text);
                long id = PrivacyInfoDB.getInstance().insertMessage(info);
                if (id > 0) {
                    info.setId(id);
                    mAdapter.addMessage(info);
                }
                dialog.dismiss();
            }
        };
        Utils.showDialog(this, "재난문자 주소 추가", input, listener);
    }

    private void editMessage(MessageInfo info, int position) {
        EditText input = new EditText(this);
        input.setText(info.getAddress());
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                info.setAddress(text);
                boolean success = PrivacyInfoDB.getInstance().updateMessage(info);
                if (success) {
                    mAdapter.notifyItemChanged(position);
                }
                dialog.dismiss();
            }
        };
        Utils.showDialog(this, "주소 수정", input, listener);
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.VHItem> {
        private final ArrayList<MessageInfo> mMessages = new ArrayList<>();
        private final WeakReference<MessageActivity> mActivity;

        Adapter(MessageActivity activity) {
            mActivity = new WeakReference<>(activity);
            mMessages.addAll(PrivacyInfoDB.getInstance().getMessages());
        }

        private void addMessage(MessageInfo info) {
            mMessages.add(info);
            notifyItemInserted(mMessages.size() - 1);
        }

        @NonNull
        @Override
        public VHItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_message,parent,false);
            return new VHItem(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VHItem holder, int position) {
            MessageInfo info = mMessages.get(position);
            holder.textView.setText(info.getAddress());
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        class VHItem extends RecyclerView.ViewHolder{
            TextView textView;
            ImageButton buttonDelete;

            VHItem(final View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.text_view);
                buttonDelete = itemView.findViewById(R.id.button_delete);

                ImageView imageView = itemView.findViewById(R.id.image_view);
                imageView.setImageResource(R.drawable.ic_warning);

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        MessageInfo info = mMessages.get(position);
                        boolean success = PrivacyInfoDB.getInstance().deleteMessage(info);
                        if (success) {
                            mMessages.remove(position);
                            notifyItemRemoved(position);
                        }
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getAdapterPosition();
                        MessageInfo info = mMessages.get(position);
                        if (mActivity.get() != null) mActivity.get().editMessage(info, position);
                        return true;
                    }
                });
            }
        }
    }

}