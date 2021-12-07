package com.example.wimo.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.wimo.HistoryActivity;
import com.example.wimo.PrivacyInfo;
import com.example.wimo.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.edt_input);
        Button btn_search = view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("address", autoCompleteTextView.getText().toString());

            replaceFragment(new MapFragment(), bundle);
        });


        autoCompleteTextView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getRows()));

        autoCompleteTextView.setOnItemClickListener((parent, v, position, id) -> {
            if (getContext() == null) return;
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

            autoCompleteTextView.setText(parent.getItemAtPosition(position).toString());
        });

    }

    private List<String> getRows(){
        ArrayList<String> rows = new ArrayList<>();
        if (getActivity() instanceof HistoryActivity) {
            ArrayList<PrivacyInfo> list = ((HistoryActivity) getActivity()).getPrivacyInfoList();
            for (PrivacyInfo info : list) {
                rows.add(info.getLocation());
            }
        }
        return rows;
    }

    public void replaceFragment(Fragment fragment, Bundle bundle){
        if (getFragmentManager() == null) return;
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_root,fragment)
                .commit();
    }

}
