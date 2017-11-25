package com.example.foush.computerVision;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Main2Activity extends AppCompatActivity {


    private static int PIC1 = 1;
    private static int PIC2 = 2;
    private static int PIC3 = 3;
    @BindView(R.id.pic1)
    Button pic1;
    @BindView(R.id.pic2)
    Button pic2;
    @BindView(R.id.pic3)
    Button pic3;
    @BindView(R.id.uploadButton)
    Button uploadButton;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.upload_title)
    TextView uploadTitile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PIC1) {
            pic1.setVisibility(View.GONE);
            if(pic2.getVisibility()==View.GONE && pic3.getVisibility()==View.GONE){
                title.setVisibility(View.GONE);

            }
            if(title.getVisibility()==View.GONE){
                uploadButton.setVisibility(View.VISIBLE);
                uploadTitile.setVisibility(View.VISIBLE);
            }

        } else if (requestCode == PIC2) {
            pic2.setVisibility(View.GONE);
            if(pic3.getVisibility()==View.GONE && pic1.getVisibility()==View.GONE){
                title.setVisibility(View.GONE);

                if(title.getVisibility()==View.GONE){
                    uploadButton.setVisibility(View.VISIBLE);
                    uploadTitile.setVisibility(View.VISIBLE);
                }
            }

        } else if (requestCode == PIC3) {
            pic3.setVisibility(View.GONE);
            if(pic1.getVisibility()==View.GONE && pic2.getVisibility()==View.GONE){
                title.setVisibility(View.GONE);
                if(title.getVisibility()==View.GONE){
                    uploadButton.setVisibility(View.VISIBLE);
                    uploadTitile.setVisibility(View.VISIBLE);
                }

            }

        } else {
        }

    }

    @OnClick({R.id.pic1, R.id.pic3, R.id.pic2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pic1:
                Intent pic1 = new Intent(Main2Activity.this, MainActivity.class);
                startActivityForResult(pic1, PIC1);

                break;
            case R.id.pic3:
                Intent pic3 = new Intent(Main2Activity.this, MainActivity.class);
                startActivityForResult(pic3, PIC3);
                break;
            case R.id.pic2:
                Intent pic2 = new Intent(Main2Activity.this, MainActivity.class);
                startActivityForResult(pic2, PIC2);
                break;
            case R.id.zip:
                ZipUtil.pack(new File("/storage/emulated/0/Pictures/computerVision"), new File("/storage/emulated/0/Pictures/computerVision.zip"));
                ZipUtil.unexplode(new File("/storage/emulated/0/Pictures/computerVision.zip"));

        }
    }
}
