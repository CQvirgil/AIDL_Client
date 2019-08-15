package com.virgil.study.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.virgil.study.aidl.Book;
import com.virgil.study.aidl.BookController;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "ClientMainActivity";
    private BookController bookController;//AIDL接口类，用于AIDL的操作
    private boolean connected;//用于绑定服务
    private List<Book> bookList;//用于存放接收到的数据

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookController = BookController.Stub.asInterface(service);
            connected = true;
            Log.i(TAG,"onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
            Log.i(TAG,"onServiceDisconnected");
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_getBookList:
                    if(connected){
                        try {
                            bookList = bookController.getBookList();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        log();
                    }
                    break;
                case R.id.btn_addBook_inOut:
                    if(connected){
                        Book book = new Book("这是一本新书 InOut");
                        try {
                            bookController.addBookInOut(book);
                            Log.e(TAG, "向服务器以InOut方式添加了一本新书");
                            Log.e(TAG, "新书名：" + book.getName());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    private void log() {
        for (Book book : bookList) {
            Log.e(TAG, book.toString());
        }
    }

    private void bindService(){
        Intent intent = new Intent();
        intent.setPackage("com.virgil.study.aidl");
        intent.setAction("com.virgil.study.aidl.MyService.Action");
        startService(intent);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_addBook_inOut).setOnClickListener(onClickListener);
        findViewById(R.id.btn_getBookList).setOnClickListener(onClickListener);
        bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connected){
            unbindService(serviceConnection);
        }
    }
}
