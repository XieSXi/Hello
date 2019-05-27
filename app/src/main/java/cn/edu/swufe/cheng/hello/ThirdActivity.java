package cn.edu.swufe.cheng.hello;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ThirdActivity extends AppCompatActivity implements Runnable{
    private final String TAG = "Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;
    private String updateDate = "";

    EditText rmb;
    TextView show;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        rmb = (EditText)findViewById(R.id.rmb);
        show = (TextView)findViewById(R.id.showOut);

        //获取sp里保存的数据
        SharedPreferences SharedPreferences=getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);//这个方法只能获取一个配置文件，用于保存关键信息
        dollarRate=SharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate=SharedPreferences.getFloat("euro_rate",0.0f);
        wonRate=SharedPreferences.getFloat("won_rate",0.0f);
        updateDate=SharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today=Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);



        Log.i(TAG,"onCreate:sp dollarRate="+dollarRate);
        Log.i(TAG,"onCreate:sp euroRate="+euroRate);
        Log.i(TAG,"onCreate:sp wonRate="+wonRate);
        Log.i(TAG,"onCreate:sp updataDate="+updateDate);
        Log.i(TAG,"onCreate:todayStr="+todayStr);

        //判断时间
        if(!todayStr.equals( updateDate)){
            Log.i(TAG,"onCreate:需要更新");
            //开启子线程 Runnable
            Thread t = new Thread(this);//this调用当前run方法
            t.start();//开始运行
        }
        else{
            Log.i(TAG,"onCreate:不需要更新");
        }



        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5){
                    Bundle bd1 = (Bundle) msg.obj;
                    dollarRate = bd1.getFloat("dollar-rate");
                    euroRate = bd1.getFloat("euro-rate");
                    wonRate = bd1.getFloat("won-rate");

                    Log.i(TAG,"handleMessage: dollarRate:"+dollarRate);
                    Log.i(TAG,"handleMessage: euroRate:"+euroRate);
                    Log.i(TAG,"handleMessage: wonRate:"+wonRate);
                   //保存更新的日期
                    SharedPreferences SharedPreferences=getSharedPreferences("myrate",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = SharedPreferences.edit();
                    editor.putString("update_date",todayStr);
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.apply();

                    Toast.makeText(ThirdActivity.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);//处理传过来的消息
            }
        };


    }
    public  void onClick(View btn){
        //获取用户输入内容
        String str = rmb.getText().toString();
        float r=0;
        if (str.length()>0){
            r=Float.parseFloat(str);
        }else {
            //提示用户输入内容
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG,"onClick: r="+r);

        if (btn.getId()==R.id.btn_dollar){
            show.setText(String.format("%.2f",r*dollarRate));
        }else if (btn.getId()==R.id.btn_euro){
            show.setText(String.format("%.2f",r*euroRate));
        }else if (btn.getId()==R.id.btn_won){
            show.setText(String.format("%.2f",r*wonRate));
        }
    }
    public void openOne(View btn){
        //打开一个页面Actively
        Log.i("open","openOne: ");
        openConfig();
    }

    private void openConfig() {
        Intent config= new Intent(this,ConfigActivity.class);//打开另一个Activity
        /*Intent web= new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jd.com"));//打开网页
        Intent intent= new Intent(Intent.ACTION_DIAL, Uri.parse("tel:87092173"));//拨号*/
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);

        Log.i(TAG,"openOne: dollarRate="+ dollarRate);
        Log.i(TAG,"openOne: euroRate="+ euroRate);
        Log.i(TAG,"openOne: wonRate="+ wonRate);

        //startActivity(config);
        startActivityForResult(config,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_set){
            Intent config= new Intent(this,ConfigActivity.class);//打开另一个Activity
        /*Intent web= new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jd.com"));//打开网页
        Intent intent= new Intent(Intent.ACTION_DIAL, Uri.parse("tel:87092173"));//拨号*/
            config.putExtra("dollar_rate_key",dollarRate);
            config.putExtra("euro_rate_key",euroRate);
            config.putExtra("won_rate_key",wonRate);

            Log.i(TAG,"openOne: dollarRate="+ dollarRate);
            Log.i(TAG,"openOne: euroRate="+ euroRate);
            Log.i(TAG,"openOne: wonRate="+ wonRate);

            //startActivity(config);
            startActivityForResult(config,1);
        }
        else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list= new Intent(this,RateListActivity.class);//打开另一个Activity
            startActivity(list);
            //测试数据库
//            RateItem item1 =new RateItem("aaa","123");
//            RateManager manager =new RateManager(this);
//            manager.add(item1);
//            manager.add(new RateItem("bbb","23.5"));
//            Log.i(TAG,"onOptionsItemSelected:写入数据完毕");
//
//            //查询所有数据
//            List<RateItem> testlist =manager.listAll();
//            for (RateItem i : testlist){
//                Log.i(TAG,"onOptionsItemSelected:取出数据[id="+i.getId()+"]Name="+i.getCurName()+" Rate="+i.getCurRate());
//            }

        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode==1 && resultCode==2){
            //Bundle bundle = data.getBundleExtra();
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate =  bundle.getFloat("key_euro",0.1f);
            wonRate =  bundle.getFloat("key_won",0.1f);
            Log.i(TAG,"onActivityResult: dollarRate="+dollarRate);
            Log.i(TAG,"onActivityResult: euroRate="+euroRate);
            Log.i(TAG,"onActivityResult: wonRate="+wonRate);

            //将新设置的汇率写道sp里
            SharedPreferences SharedPreferences=getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = SharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
            Log.i(TAG,"onActivityResult:数据以保存到sharedPreferences");

        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void run() {
        Log.i(TAG,"run:run()......");
        try {
                Thread.sleep(3000); //当前线程停止两秒，不让他跑这么快
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
        //用于保存获取的汇率
        Bundle bundle ;


        /*//获取Msg对象，用于返回
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;   //arg1,arg2:一个参数，两个参数int   what：用于标记当前msg的属性  obj ：所有对象的父类
        msg.obj = "Hello from run()";
        handler.sendMessage(msg);*/


        //获取网络数据
       /* URL url = null;
        try {
            url = new URL("http://www.usd-cny.com/bankofchina.htm");//http://www.usd-cny.com/icbc.htm
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();//获得一个输入流

            String html = inputStream2String(in);//调用方法，将输入流转化为字符串
            Log.i(TAG,"run:html="+html);
            Document doc = Jsoup.parse(html);

        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        bundle =getFromBOC ();

        //bundle中保存获取的汇率

        //获取Msg对象，用于返回
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;   //arg1,arg2:一个参数，两个参数int   what：用于标记当前msg的属性  obj ：所有对象的父类
        //msg.obj = "Hello from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    //从bankofchina获取数据
    private Bundle getFromBOC() {
        Bundle bundle=new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG,"run: "+doc.title());
            /*Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                Log.i(TAG,"%s\n\t%s"+headline.attr("title")+headline.absUrl("href"));
            }*/
            Elements tables = doc.getElementsByTag("table");
            /*int i=1;
            for (Element table:tables){
                Log.i(TAG,"run:tabel["+i+"]="+table);
                i++;//得到table1为我们所需的数据
            }*/

            Element table1 = tables.get(1);
            //获取TD中的数据
            Elements tds = table1.getElementsByTag("td");
            for (int i = 0;i<tds.size();i+=8){
                Element td1 = tds.get(i);//获取到第一列的数据:国家名字
                Element td2 = tds.get(i+5);//获取第六列的数据：汇率
                Log.i(TAG,"run:text= "+td1.text()+"==>"+td2.text());
                String str1= td1.text();
                String val = td2.text();
                if ("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if ("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if ("韩国元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }
    private Bundle getFromUsdCny() {
        Bundle bundle=new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG,"run: "+doc.title());
            /*Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                Log.i(TAG,"%s\n\t%s"+headline.attr("title")+headline.absUrl("href"));
            }*/
            Elements tables = doc.getElementsByTag("table");
            /*int i=1;
            for (Element table:tables){
                Log.i(TAG,"run:tabel["+i+"]="+table);
                i++;//得到table1为我们所需的数据
            }*/

            Element table1 = tables.get(0);
            //Log.i(TAG,"run:table6="+table1);
            //获取TD中的数据
            Elements tds = table1.getElementsByTag("td");
            for (int i = 0;i<tds.size();i+=6){
                Element td1 = tds.get(i);//获取到第一列的数据:国家名字
                Element td2 = tds.get(i+5);//获取第六列的数据：汇率
                Log.i(TAG,"run:text= "+td1.text()+"==>"+td2.text());
                String str1= td1.text();
                String val = td2.text();
                if ("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if ("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if ("韩元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    //把数据流转化为字符串输出
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int butfferSize = 1024;
        final char[] buffer = new  char[butfferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for(; ;){
            int rsz = in.read(buffer,0,buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }

}

