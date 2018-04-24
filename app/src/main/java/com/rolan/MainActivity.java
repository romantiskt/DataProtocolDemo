
package com.rolan;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rolan.flat.Friend;
import com.rolan.flat.People;
import com.rolan.flat.PeopleList;
import com.rolan.jsonmodel.PeopleListJson;
import com.rolan.proto.PersonEntity;
import com.rolan.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView textViewFlat, textViewJson;
    private int sampleSize = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewFlat = (TextView) findViewById(R.id.textViewFlat);
        textViewJson = (TextView) findViewById(R.id.textViewJson);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            et.setText(sampleSize + "");
            new AlertDialog.Builder(this).setTitle("设置样本大小")
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String input = et.getText().toString();
                            sampleSize = Integer.parseInt(input);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadFromFlatBuffer(View view) {
        byte[] buffer = Utils.readRawResource(getApplication(), R.raw.sample_flatbuffer);
        long startTime = System.currentTimeMillis();
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        PeopleList peopleList = PeopleList.getRootAsPeopleList(bb);
        long timeTaken = System.currentTimeMillis() - startTime;
        String logText = "FlatBuffer : " + timeTaken + "ms";
        textViewFlat.setText(logText);
        Log.d(TAG, "loadFromFlatBuffer " + logText);
    }

    public void loadFromJson(View view) {
        String jsonText = new String(Utils.readRawResource(getApplication(), R.raw.sample_json));
        long startTime = System.currentTimeMillis();
        PeopleListJson peopleList = new Gson().fromJson(jsonText, PeopleListJson.class);
        long timeTaken = System.currentTimeMillis() - startTime;
        String logText = "Json : " + timeTaken + "ms";
        textViewJson.setText(logText);
        Log.d(TAG, "loadFromJson " + logText);
    }

    /**
     * 解析flatbuffer数据
     *
     * @param view
     */
    public void parseData(View view) {
        File file = new File(getCacheDir() + "/flatbuffers.bin");
        try {
            byte[] bytes = Utils.toByteArray(file);
            long startTime = System.currentTimeMillis();
            ByteBuffer wrap = ByteBuffer.wrap(bytes);
            PeopleList rootAsPeopleList = PeopleList.getRootAsPeopleList(wrap);
            People peoples = rootAsPeopleList.peoples(1);//取第1个
            Toast.makeText(this, System.currentTimeMillis() - startTime + "ms", Toast.LENGTH_SHORT).show();
            String company = peoples.company();
            int peopleLength = peoples.friendsLength();
            Log.d("wang", "flatbuffers__________" + peoples.company());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 组装flatbuffer数据
     *
     * @param view
     */
    public void filterData(View view) {
        long startTime = System.currentTimeMillis();
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int peopleList[] = new int[sampleSize];
        for (int i = 0; i < peopleList.length; i++) {
            int nameOffect1 = builder.createString("李四");
            int nameOffect2 = builder.createString("张三");
            int nameOffect3 = builder.createString("王五");
            int friends[] = new int[3];
            friends[0] = Friend.createFriend(builder, 3243544, nameOffect1);
            friends[1] = Friend.createFriend(builder, 2341423, nameOffect2);
            friends[2] = Friend.createFriend(builder, 1232432, nameOffect3);
            int index = builder.createString("23412243");
            int guid = builder.createString("xnvnsfsd");
            int name = builder.createString("赵钱");
            int gender = builder.createString("男");
            int company = builder.createString("加油宝");
            int email = builder.createString("ye@163.com");
            int friendsVector = People.createFriendsVector(builder, friends);
            People.startPeople(builder);
            People.addId(builder, index);
            People.addIndex(builder, 3001);//
            People.addGuid(builder, guid);
            People.addName(builder, name);
            People.addGender(builder, gender);
            People.addCompany(builder, company);
            People.addEmail(builder, email);
            People.addFriends(builder, friendsVector);
            peopleList[i] = People.endPeople(builder);
        }
        int peoplesVector = PeopleList.createPeoplesVector(builder, peopleList);
        PeopleList.startPeopleList(builder);
        PeopleList.addPeoples(builder, peoplesVector);
        int endPeopleList = PeopleList.endPeopleList(builder);
        builder.finish(endPeopleList);
        Toast.makeText(this, System.currentTimeMillis() - startTime + "ms", Toast.LENGTH_SHORT).show();
        ByteBuffer bb = builder.dataBuffer();
        File file = new File(getCacheDir() + "/flatbuffers.bin");
        try {
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bb.array(), bb.position(), bb.remaining());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 组装protobuff数据
     *
     * @param view
     */
    public void protofilterData(View view) {
        long startTime = System.currentTimeMillis();
        PersonEntity.People.Builder people = PersonEntity.People.newBuilder();
        for (int i = 0; i < sampleSize; i++) {
            PersonEntity.People.Person.Builder person = PersonEntity.People.Person.newBuilder();
            PersonEntity.People.Person.Friend friend1 = PersonEntity.People.Person.Friend.newBuilder().setId("1111").setName("李四").build();
            PersonEntity.People.Person.Friend friend2 = PersonEntity.People.Person.Friend.newBuilder().setId("2222").setName("张三").build();
            PersonEntity.People.Person.Friend friend3 = PersonEntity.People.Person.Friend.newBuilder().setId("3333").setName("王五").build();
            person.setId(i)
                    .setIndex(i)
                    .setGuid("guid" + i)
                    .setName("name:" + i)
                    .setGender(i % 2 == 0 ? "男" : "女")
                    .setCompany("加油宝：" + i)
                    .setEmail("sd@.com")
                    .addFriend(friend1)
                    .addFriend(friend2)
                    .addFriend(friend3)
                    .build();
            people.addPerson(person);
        }
        PersonEntity.People build = people.build();
        Toast.makeText(this, System.currentTimeMillis() - startTime + "ms", Toast.LENGTH_SHORT).show();
        byte[] peopleBytes = build.toByteArray();
        try {
            Utils.writeBytesToFile(getCacheDir() + "/protocalbuffers.bin", peopleBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析protobuff数据
     *
     * @param view
     */
    public void parseProtoData(View view) {
        File file = new File(getCacheDir() + "/protocalbuffers.bin");
        try {
            byte[] bytes = Utils.toByteArray(file);
            long startTime = System.currentTimeMillis();
            PersonEntity.People people = PersonEntity.People.parseFrom(bytes);
            PersonEntity.People.Person person = people.getPerson(1);
            Toast.makeText(this, System.currentTimeMillis() - startTime + "ms", Toast.LENGTH_SHORT).show();
            Log.d("wang", "protobuffers__________" + person.getCompany());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 组装json数据
     *
     * @param view
     */
    public void jsonfilterData(View view) {
        long startTime = System.currentTimeMillis();
        try {
            JSONObject root = new JSONObject();
            JSONArray peopleList = new JSONArray();
            for (int i = 0; i < sampleSize; i++) {
                JSONObject people = new JSONObject();
                people.put("id", i);
                people.put("index", i);
                people.put("guid", "guid" + i);
                people.put("name", "name:" + i);
                people.put("gender", i % 2 == 0 ? "男" : "女");
                people.put("company", "加油宝：" + i);
                people.put("email", "sd@.com");

                JSONArray friends = new JSONArray();
                JSONObject friend1 = new JSONObject();
                friend1.put("id", 23434);
                friend1.put("name", "张三");
                friends.put(0, friend1);

                JSONObject friend2 = new JSONObject();
                friend2.put("id", 23434);
                friend2.put("name", "张三");
                friends.put(2, friend2);

                JSONObject friend3 = new JSONObject();
                friend3.put("id", 23434);
                friend3.put("name", "张三");
                friends.put(3, friend3);

                people.put("friends", friends);
                peopleList.put(i, people);
            }
            root.put("peoples", peopleList);
            Toast.makeText(this, System.currentTimeMillis() - startTime + "ms", Toast.LENGTH_SHORT).show();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(getCacheDir() + "/people.json");
                fos.write(root.toString().getBytes("UTF-8"));
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析json数据
     *
     * @param view
     */
    public void parseJsonData(View view) {
//        String jsonStr = Utils.ReadFile(getCacheDir() + "/people.json");
        try {
            File file = new File(getCacheDir() + "/people.json");
            byte[] bytes = Utils.toByteArray(file);
            long startTime = System.currentTimeMillis();
            String jsonStr = new String(bytes, "UTF-8");
            PeopleListJson peopleList = new Gson().fromJson(jsonStr.toString(), PeopleListJson.class);
            Toast.makeText(this, System.currentTimeMillis() - startTime + "ms", Toast.LENGTH_SHORT).show();
            ;
            Log.d("wang", "json__________" + peopleList.peoples.get(0).company);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
