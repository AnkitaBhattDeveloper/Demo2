package com.example.demo2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.BlockingDeque;

public class MainActivity extends AppCompatActivity {


//    private Context mContext;

        private int randomNumberValue = 0;
        private boolean nIsBound;

        private TextView textView;
        private Button bindservice, unbindservice, getnumber;

        private Intent serviceIntent;

        public static final int GET_RANDOM_NUMBER_FLAG = 0;

        Messenger randomNumberRequestMessenger, randomNumberReceiveMessenger;


        class ReceiveRandomNumberHandler extends Handler {
            @Override
            public void handleMessage(@NonNull Message msg) {
                randomNumberValue = 0;
                switch (msg.what) {
                    case GET_RANDOM_NUMBER_FLAG:
                        randomNumberValue = msg.arg1;
                        textView.setText("RAndom number is:" + randomNumberValue);
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        }


            ServiceConnection RandomNumberServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    randomNumberRequestMessenger = new Messenger(iBinder);
                    randomNumberReceiveMessenger = new Messenger(new ReceiveRandomNumberHandler());
                    nIsBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    randomNumberRequestMessenger = null;
                    randomNumberReceiveMessenger = null;
                    nIsBound = false;
                }
            };



            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                bindservice = findViewById(R.id.bindservice);
                unbindservice = findViewById(R.id.unbindservice);
                getnumber = findViewById(R.id.getnumber);
                textView = findViewById(R.id.textView);


                bindservice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bindtoRemoteService();
                    }
                });


                unbindservice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UnbindtoRemoteService();
                    }
                });


                getnumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchRandomNumber();
                    }
                });


                serviceIntent = new Intent();
                serviceIntent.setComponent(new ComponentName("com.example.demo", "com.example.demo.RemoteService"));

            }



public void bindtoRemoteService()
{
    bindService(serviceIntent,RandomNumberServiceConnection,BIND_AUTO_CREATE);
    Log.d("random", "bindtoRemoteService: bind method is workign$$$$$$$$$$$$$$$$ ");
    Toast.makeText(this, "Service Bound", Toast.LENGTH_SHORT).show();

}
    public void UnbindtoRemoteService()
    {
      unbindService(RandomNumberServiceConnection);

      nIsBound=false;
        Log.d("random", "Un bind methid is working=========");
        Toast.makeText(this, "Unboud service", Toast.LENGTH_SHORT).show();
    }
    public void fetchRandomNumber()
    {
   if (nIsBound==true)
   {
       Message requestMessage = Message.obtain(null,GET_RANDOM_NUMBER_FLAG);
       requestMessage.replyTo=randomNumberReceiveMessenger;
     //  textView.setText("Random Number is );
       Log.d("random", "fetch random number is working-------- ");
       try {
           randomNumberRequestMessenger.send(requestMessage);
       }
       catch (RemoteException e) {
           e.printStackTrace();
       }
   }

   else
   {
       Toast.makeText(this, "SERVicE not bound----Cant Get a Random NUmber ", Toast.LENGTH_SHORT).show();
   }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RandomNumberServiceConnection=null;
    }


    }
