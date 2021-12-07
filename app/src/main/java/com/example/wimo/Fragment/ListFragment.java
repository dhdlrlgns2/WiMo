package com.example.wimo.Fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wimo.HistoryActivity;
import com.example.wimo.MainActivity;
import com.example.wimo.PrivacyInfo;
import com.example.wimo.R;
import com.example.wimo.SettingActivity;
import com.example.wimo.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter(this);
        mRecyclerView.setAdapter(mAdapter);

        ArrayList<PrivacyInfo> list = new ArrayList<>();
        if (getActivity() instanceof HistoryActivity) {
            HistoryActivity activity = (HistoryActivity) getActivity();
            list.addAll(activity.getPrivacyInfoList());
        }
        mAdapter.setList(list);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }
    }

    private void onItemClicked(String address) {
        Utils.showDialogFragment(getFragmentManager(), "DIALOG_MAP", DialogMap.newInstance(address));
    }

    private void onItemLongClicked(PrivacyInfo info, int position) {
        if (getContext() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("메모를 입력하세요.");
        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (info.getMemo() != null) input.setText(info.getMemo());
        builder.setView(input);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                info.setMemo(text);
                if (mAdapter != null) mAdapter.notifyItemChanged(position);
                if (getActivity() instanceof HistoryActivity) {
                    ((HistoryActivity) getActivity()).updateDB(info);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.VHItem> {
        private final WeakReference<ListFragment> mFragment;
        private final ArrayList<PrivacyInfo> mList = new ArrayList<>();

        Adapter(ListFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        void setList(ArrayList<PrivacyInfo> list) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VHItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_calendar,parent,false);
            return new VHItem(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VHItem holder, int position) {
            PrivacyInfo info = mList.get(position);
            holder.textTime.setText(info.getTime());
            holder.textLocation.setText(info.getLocation().trim());
            String memo = (info.getMemo() != null) ? info.getMemo().trim() : "";
            holder.textMemo.setText(memo);

            if (((MainActivity) MainActivity.mcontext).a != null) {
                String[] v = ((MainActivity) MainActivity.mcontext).a; // 액티비티에서 문자열 받아옴 현재 메인액티비티와 연결 나중에 병합하면 셋팅액티비티에서 연결
                int ex = -1;
                for (int i = 0; i < mList.size(); i++) { // 사용자 주소 반복문
                    for (int j = 0; j < v.length; j++) { // 확진자(받아온 배열) 주소 반복문
                        if (v[j] == "null") { // 받아온 배열 속 널값 제외
                            continue;
                        }
                        if (mList.get(i).getLocation().equals(v[j])) {  //추가 mList의 위치 값과 특정 위치값(현재 스트링 a) 같을 경우
                            ex = i; // 사용자 주소에 대한 리스트뷰의 포지션 위치 전달
                            System.out.println(v[j]);
                        }
                        if (position == ex) { //추가 포지션(리스트의 순서) 값에 따라 색상 변경 코드 추후 시간 또는 위치 비교 코드 삽입
                            holder.textTime.setTextColor(Color.RED); //추가 리스트의 시간 색상 변경 코드
                            holder.textLocation.setTextColor(Color.RED); //추가 리스트의 주소 색상 변경 코드
                        }
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class VHItem extends RecyclerView.ViewHolder{
            TextView textTime;
            TextView textLocation;
            TextView textMemo;

            VHItem(final View itemView) {
                super(itemView);
                textTime = itemView.findViewById(R.id.text_time);
                textLocation = itemView.findViewById(R.id.text_location);
                textMemo = itemView.findViewById(R.id.text_memo);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PrivacyInfo info = mList.get(getAdapterPosition());
                        if (mFragment.get() != null) mFragment.get().onItemClicked(info.getLocation().trim());
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getAdapterPosition();
                        PrivacyInfo info = mList.get(position);
                        if (mFragment.get() != null) mFragment.get().onItemLongClicked(info, position);
                        return true;
                    }
                });
            }
        }
    }

}
