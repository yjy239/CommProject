package com.example.asus.myproject;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.myproject.service.GuardService;
import com.example.asus.myproject.service.JobWakeUpService;
import com.example.asus.myproject.service.MessageService;
import com.example.easyioclibrary.CommonDialog.CommonDialog;
import com.example.easyioclibrary.fix.FixClassManager;
import com.example.easyioclibrary.ioc.CheckNet;
import com.example.easyioclibrary.ioc.OnClick;
import com.example.easyioclibrary.ioc.ViewById;
import com.example.easyioclibrary.ioc.ViewUtils;
import com.example.framelibrary.BaseSkinActivity;
import com.example.framelibrary.skin.SkinManager;

import java.io.File;
import java.lang.ref.WeakReference;

public class MainActivity extends BaseSkinActivity{

    @ViewById(R.id.text_tv)
    private TextView mTextView;
    @ViewById(R.id.test_img)
    private Button img;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
//        HttpUtils.with(this).url("").addParam().addParam()
//                .execute(new HttpCallback<Object>() {
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Object result) {
//
//                    }
//                });

    }


    @Override
    public void initData() {
        mTextView.setText("IOCTV");
        startService(new Intent(this, MessageService.class));
        startService(new Intent(this, GuardService.class));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //必须大于5.0
            startService(new Intent(this, JobWakeUpService.class));
        }
//        fixDexBug();
//        int i = 2/0;
    }


    private void fixDexBug() {
        Log.e("fix","here");
        File fixfile = new File(Environment.getExternalStorageDirectory(),"fix.dex");

        if(fixfile.exists()){
            Log.e("fix",""+fixfile.getAbsolutePath());
            try{
                FixClassManager fixClassManager = new FixClassManager(this);
                fixClassManager.fixDexClass(fixfile.getAbsolutePath());
                Toast.makeText(MainActivity.this,"修复成功",Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(MainActivity.this,"修复失败",Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
        }
    }

    public void skin(){
        String skinpath = Environment.getExternalStorageDirectory().getAbsolutePath()
                +File.separator+"red.skin";
        int result = SkinManager.getInstance().loadSkin(skinpath);
    }

    public void skin1(){
        int result = SkinManager.getInstance().restoreSkin();
    }

    @Override
    public void initView() {
        ViewUtils.inject(MainActivity.this);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommonDialog.Builder(MainActivity.this)
                        .setContentView(R.layout.simple_dialog_layout)
                        .setText(R.id.b1,"按钮1")
                        .setText(R.id.b2,"按钮2")
                        .setOnClickListener(R.id.b1, new WeakReference(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(MainActivity.this,"B1",Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .setOnClickListener(R.id.b2,new WeakReference<View.OnClickListener>(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(MainActivity.this,"B2",Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .addDefaultAnimation()
                        .fromBottom(true)
                        .show();
                Toast.makeText(MainActivity.this,"Bug测试",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void initTitle() {

    }

    @OnClick({R.id.text_tv,R.id.test_img})
    @CheckNet("你的网络不给力")
    private void onClick(View v){
        Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
    }

//    private void alifixBug() {
//        //获取上次的异常崩溃信息
//        File file = ExceptionCrashHandler.getInstance().getCrashFile();
//        if(file.exists()){
//            //上传服务器
//            try {
//                InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file));
//                char[] buffer = new char[1024];
//                int lenght = 0;
//                while((lenght = fileReader.read(buffer)) != -1){
//                    String str = new String(buffer,0,lenght);
//                    Log.e("TAG",str);
//                }
//                fileReader.read();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//        File fixfile = new File(Environment.getExternalStorageDirectory(),"fix.apatch");
//        if(fixfile.exists()){
//            //修复bug
//            try{
//                BaseApplication.patchManager.addPatch(fixfile.getAbsolutePath());
//                Toast.makeText(this,"修复成功",Toast.LENGTH_SHORT).show();
//            }catch(Exception e){
//                e.printStackTrace();
//                Toast.makeText(this,"修复失败",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
