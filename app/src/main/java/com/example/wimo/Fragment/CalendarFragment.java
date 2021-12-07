package com.example.wimo.Fragment;

import android.content.DialogInterface;
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
import com.example.wimo.PrivacyInfo;
import com.example.wimo.R;
import com.example.wimo.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarFragment extends Fragment {

    private final ArrayList<PrivacyInfo> mPrivacyInfoList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);

        if (getActivity() instanceof HistoryActivity) {
            ArrayList<PrivacyInfo> list = ((HistoryActivity) getActivity()).getPrivacyInfoList();
            mPrivacyInfoList.addAll(list);
        }

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter(this);
        mRecyclerView.setAdapter(mAdapter);

        CalendarView calendarView = view.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                onDaySelected(year, month + 1, dayOfMonth);
            }
        });
        setCurrentDatePrivacyInfo();
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

    private void setCurrentDatePrivacyInfo() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        onDaySelected(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    private void onDaySelected(int year, int month, int dayOfMonth) {
        ArrayList<PrivacyInfo> list = new ArrayList<>();
        for (PrivacyInfo info : mPrivacyInfoList) {
            if (compareDate(year, month, dayOfMonth, info.getTime())) {
                list.add(info);
            }
        }
        if (mAdapter != null) mAdapter.setList(list);
    }

    private boolean compareDate(int year, int month, int dayOfMonth, String time) {
        time = time.split(" ")[0];
        String[] date = time.split("-");
        if (dayOfMonth != parseInt(date[2])) {
            return false;
        }
        if (month != parseInt(date[1])) {
            return false;
        }
        return year == parseInt(date[0]);
    }

    private int parseInt(String string) {
        int result = -1;
        try {
            result = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
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
        private final WeakReference<CalendarFragment> mFragment;
        private final ArrayList<PrivacyInfo> mList = new ArrayList<>();

        Adapter(CalendarFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        void setList(ArrayList<PrivacyInfo> list) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Adapter.VHItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_calendar,parent,false);
            return new Adapter.VHItem(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.VHItem holder, int position) {
            PrivacyInfo info = mList.get(position);
            holder.textTime.setText(info.getTime());
            holder.textLocation.setText(info.getLocation().trim());
            String memo = (info.getMemo() != null) ? info.getMemo().trim() : "";
            holder.textMemo.setText(memo);
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
