package com.projects.leophilo.eltools.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.projects.leophilo.eltools.R;
import com.projects.leophilo.eltools.core.Calculator;
import com.projects.leophilo.eltools.model.entity.HistoryResultEntity;
import com.projects.leophilo.eltools.model.entity.NormalCompositionItemEntity;
import com.projects.leophilo.eltools.model.greendao.GreenDaoHelper;
import com.projects.leophilo.eltools.view.adapter.MainListAdapter;
import com.projects.leophilo.eltools.view.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.OnClick;

public class ResultDetailDialog extends BaseDialogFragment {

    public static final String TAG = "ResultDetailDialog";

    @BindView(R.id.textTemperature)
    TextView textTemperature;
    @BindView(R.id.textPressure)
    TextView textPressure;
    @BindView(R.id.textLEL)
    TextView textLEL;
    @BindView(R.id.textUEL)
    TextView textUEL;
    @BindView(R.id.textVolume)
    TextView textVolume;
    @BindView(R.id.list_view)
    RecyclerView listView;
    @BindView(R.id.button_save)
    TextView btnSave;

    @OnClick(R.id.button_save)
    void save() {
        String detail = new Gson().toJson(entities);
        HistoryResultEntity entity = new HistoryResultEntity(
                System.currentTimeMillis(),
                temperature, pressure,
                LEL, UEL,
                sum, detail);

        GreenDaoHelper.getDaoSession().getHistoryResultEntityDao().insert(entity);
        this.getDialog().dismiss();
        Toast.makeText(getContext(), "已保存", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_close)
    void close() {
        this.getDialog().dismiss();
    }

    private String temperature;
    private String pressure;
    private String LEL;
    private String UEL;
    private String sum;
    private ArrayList<NormalCompositionItemEntity> entities;
    private boolean hideSaveAction;
    private DialogInterface.OnDismissListener dismissListener;

    public static ResultDetailDialog newInstance(
            Calculator.ELResult result, ArrayList<NormalCompositionItemEntity> entities) {

        Bundle args = new Bundle();

        ResultDetailDialog fragment = new ResultDetailDialog();
        args.putString("TEMPERATURE", result.getTemperature() + "");
        args.putString("PRESSURE", result.getPressure() + "");
        args.putString("LEL", result.getDate().getLEL() + "");
        args.putString("UEL", result.getDate().getUEL() + "");
        args.putString("SUM", result.getSum() + "");
        args.putParcelableArrayList("LIST", entities);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            if (null != dialog.getWindow())
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    protected void startEvent() {
        if (hideSaveAction) {
            btnSave.setVisibility(View.GONE);
        }

        Bundle args = getArguments();

        if (args != null) {
            temperature = args.getString("TEMPERATURE");
            pressure = args.getString("PRESSURE");
            LEL = args.getString("LEL");
            UEL = args.getString("UEL");
            sum = args.getString("SUM");
            String temperatureStr = temperature + "℃";
            String pressureStr = pressure + "Mpa";
            String LELStr = LEL + "%";
            String UELStr = UEL + "%";
            String sumStr = sum + "%";
            entities = args.getParcelableArrayList("LIST");

            if (entities != null) {
                Collections.sort(entities);
            }

            textTemperature.setText(temperatureStr);
            textPressure.setText(pressureStr);
            textLEL.setText(LELStr);
            textUEL.setText(UELStr);
            textVolume.setText(sumStr);

            LinearLayoutManager llm = new LinearLayoutManager(getContext());

            MainListAdapter adapter = new MainListAdapter(R.layout.item_result_recycler, entities);

            listView.setAdapter(adapter);
            listView.setLayoutManager(llm);

            adapter.setEmptyView(R.layout.view_empty_list, listView);
        }
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public void setHideSaveAction(boolean hideSaveAction) {
        this.hideSaveAction = hideSaveAction;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != dismissListener) {
            dismissListener.onDismiss(null);
        }
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.dialog_result_detail;
    }

}
