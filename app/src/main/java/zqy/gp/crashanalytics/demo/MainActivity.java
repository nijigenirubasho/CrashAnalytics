package zqy.gp.crashanalytics.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import zqy.gp.crashanalytics.CA;
import zqy.gp.crashanalytics.demo.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // SDK初始化
        CA.initialize("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", "ryuunoakaihitomi", "CA_demo_2");

        //noinspection Convert2Lambda
        binding.testCrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在按钮监听器中抛出测试崩溃异常
                throw new IllegalStateException("这是一条测试崩溃异常");
            }
        });

        //SDK的自定义信息
        CA.log("This Activity is " + this);
        CA.setProperty("A Key", "B value");
    }
}