package com.android.worktimemanager.Class;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

abstract public class ListItemRemoveClickListener implements AdapterView.OnItemClickListener
{
    protected List list;
    protected String tableName;
    protected int selPosition = -1;
    private Context context;
    public ListItemRemoveClickListener(Context context,String tableName,List list)
    {
        this.context = context;
        this.tableName = tableName;
        this.list = list;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        selPosition = pos;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("해당 일정을 삭제하시겠습니까?").setCancelable(true).setNegativeButton("취소",null).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeItem();
                        list = getList();
                        updateState();
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog al = alert.create();
        al.show();
    }

    abstract protected void updateState();
    abstract protected List getList();
    abstract protected void removeItem();
}