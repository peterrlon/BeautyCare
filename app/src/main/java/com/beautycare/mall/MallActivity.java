package com.beautycare.mall;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.beautycare.R;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

public class MallActivity extends AppCompatActivity {

    private Intent intent;
    private RecyclerView mallRecyList;
    List<AVObject> objects;
    private MallMainRecyAdapter mallMainRecyAdapter;
    ArrayList<Data> dataList = new ArrayList<Data>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_list_main);

        setTitle("Mall");
        initImageLoader(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        AVOSCloud.initialize(MallActivity.this, "lpjA6quucO5BzlmMPxTrjKwD-gzGzoHsz", "vu6uDC74NlzzJP4YbrJmDFV6");
        new MyTask().execute();


    }

    class MyTask extends AsyncTask<Void, Void, ArrayList<Data>> {

        ArrayList<Data> tmplist = new ArrayList<Data>();
        String urlID;

        @Override
        protected ArrayList<Data> doInBackground(Void... params) {
        //这个方法是在onPreExecute后执行的，params参数的类型是第一个Void，返回的类型是第三个ArrayList<Data>，返回的参数会传到onPostExecute

            try {
                AVObject mallObject;
                AVFile logoObject;
                Double lat, lon;
//                AVCloudQueryResult result = AVQuery.doCloudQuery("select * from Mall");
                AVQuery<AVObject> query_1 = new AVQuery<>("Mall");//封装后，这里可以考虑传parms进来
                List<AVObject> result = query_1.find();//查询并传数据到result
                Log.d("mall_size", String.valueOf(result.size()));
                if (result.size() != 0) {

                    for (int i = 0; i < result.size(); i++) {

                        Data tmpData = new Data();//封装好的数据类型
                        mallObject = result.get(i);//获取第i个AVObject
                        /*通过key获取AVObject（即数据库）相应的列的内容并调用封装数据类的方法添加数据*/
                        tmpData.setTitle(mallObject.getString("mall_name"));
                        /*同理，这里因为每个数据库表的列名都不同，
                        我这里直接就写上key名了，如果这个要封装的话不知道怎么封装，所以再不行的话每人写自己的asynctask
                         */
                        tmpData.setContent(mallObject.getString("content"));

                        tmpData.setID(mallObject.getObjectId());

                        lat = mallObject.getDouble("Lat");//获取坐标
                        lon = mallObject.getDouble("Lon");
                        tmpData.setLatLng(new LatLng(lat,lon));

                        /*这个的网络数据库，可以新建一类型是AVFile的列，我这个的列名是mall_logo，这列是我们手动
                        上传图片的一个列，上传后这个图片会在本身的_File类（表）自动添加
                        一个AVFile的数据，包括url，接下来可以通过之前获得的数据直接get这个AVFile，
                        其实如果不想每个图片都放在本身的_File表的话好像也可以，
                        但是暂时看起来有点麻烦，之后再研究，下面就是获取AVFile的代码，一样有个key的值（列名）要提供*/
                        logoObject = mallObject.getAVFile("mall_logo");

                        if( logoObject != null) {
                            Log.d("logoObject", logoObject.toString());
                            urlID = logoObject.getUrl();//获取了AVFile之后可以直接获取url
                            Log.d("urlID", urlID);
                            tmpData.setURL(urlID);
                        }
                        tmplist.add(i, tmpData);//将获取的数据加入全局的一个ArrayList
                        Log.d("data", tmplist.get(i).getTitle());
                    }
                }

            } catch (Exception e) {

                Log.d("exception", String.valueOf(e));
            }
            return tmplist;
        }

        @Override
        protected void onPostExecute(ArrayList<Data> tmpdatalist) {
        //这个方法的参数是在doInBackground传过来的，是AsyncTask处理获取结果的方法

            dataList = tmpdatalist;
            Log.d("size", String.valueOf(dataList.size()));
            /*这里就新建adapter并把参数传给adapter初始化,因为我和刘生的adapter类似，所以不知道申申和主任的
            * adapter是怎么写的，不知道你们是怎么传参，所以这里也只能给你们参考*/
            mallMainRecyAdapter = new MallMainRecyAdapter(MallActivity.this, dataList);

            mallRecyList.setLayoutManager(new LinearLayoutManager(MallActivity.this));

            mallRecyList.setAdapter(mallMainRecyAdapter);

            mallMainRecyAdapter.setOnItemClickListener(new MallMainRecyAdapter.mallListListener() {
                @Override
                public void onItemClick(View v, Data tmpdata) {

                    Intent intent = new Intent(MallActivity.this, MallDetail.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", tmpdata.getURL());
                    bundle.putString("content", tmpdata.getContent());
                    bundle.putString("name", tmpdata.getTitle());
//                    LatLng latLng = tmpdata.getLatLng();
//                    System.out.println(latLng.toString());
                    bundle.putParcelable("latlng", tmpdata.getLatLng());
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });

            /*还有一点我还没照着刘生的onClickListner做好响应的动作，然而点击相应要传参进去第二层页面，
            * 这个还需要研究*/

            Log.d("boolean", mallMainRecyAdapter.toString());

        }

        @Override
        protected void onPreExecute() {

            /*这个是AsyncTask最开始的动作，这里我们可以初始化一些东西，比如说这个很重要的云数据需要初始，
            * 下面两个很奇怪的参数我自己账户中建了一个应用，然后就会提供应用的id和key,如果大家想试的话，
            * 我把我的账号密码给你们，暂时不知道能不能同时登，
            * 账号：maskliang@gmail.com
            * 密码：fgh159456IOP*/
            AVOSCloud.initialize(MallActivity.this, "lpjA6quucO5BzlmMPxTrjKwD-gzGzoHsz", "vu6uDC74NlzzJP4YbrJmDFV6");
            mallRecyList = (RecyclerView) findViewById(R.id.mall_list_main);

        }
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);

        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        // config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        //      config.diskCacheSize(50 * 1024 * 1024); // 50 MiB   dsc
        config.discCacheSize(50 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.enableLogging();
        //    config.writeDebugLogs();

        ImageLoader.getInstance().init(config.build());
    }



    //菜单返回上一页
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
        }
        return false;
    }
}