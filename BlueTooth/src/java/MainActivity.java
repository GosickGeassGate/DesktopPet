package com.example.babsis.desktoppet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button)findViewById(R.id.bluetooth);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"进入蓝牙设置",Toast.LENGTH_SHORT).show();
                //BlueTooth bth = new BlueTooth(MainActivity.this);
                //bth.work();
                Intent intent = new Intent(MainActivity.this,BlueTooth.class);
                startActivity(intent);
                //BlueTooth();
            }
        });
    }
}
