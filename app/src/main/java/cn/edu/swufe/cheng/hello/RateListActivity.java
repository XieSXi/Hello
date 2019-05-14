package cn.edu.swufe.cheng.hello;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RateListActivity extends ListActivity implements Runnable {
    String data[]={"wait..."};
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);
        List<String> list1=new ArrayList<String>();
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }
        Thread t=new Thread(this);
        t.start();

        ListAdapter adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);
        handler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==7){
                    List<String> list2= (List<String>)msg.obj;
                    ListAdapter adapter =new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);

                }
                super.handleMessage(msg);
            }
        };

    }


    @Override
    public void run() {
        //获取网络数据，放入List带回到主线程中
        List<String> retList=new ArrayList<String>();
        Document doc = null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG,"run: "+doc.title());
            Elements tables = doc.getElementsByTag("table");
            Element table1 = tables.get(1);
            //获取TD中的数据
            Elements tds = table1.getElementsByTag("td");
            for (int i = 0;i<tds.size();i+=8){
                Element td1 = tds.get(i);//获取到第一列的数据:国家名字
                Element td2 = tds.get(i+5);//获取第六列的数据：汇率
                Log.i(TAG,"run:text= "+td1.text()+"==>"+td2.text());
                String str1= td1.text();
                String val = td2.text();
                retList.add(str1+"==>"+val);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);
    }
}
