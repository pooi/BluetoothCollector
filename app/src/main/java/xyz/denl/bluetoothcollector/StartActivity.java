package xyz.denl.bluetoothcollector;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xyz.denl.bluetoothcollector.adapter.BluetoothListCustomAdapter;
import xyz.denl.bluetoothcollector.util.DividerItemDecoration;
import xyz.denl.bluetoothcollector.util.OnAdapterSupport;
import xyz.denl.bluetoothcollector.util.ParsePHP;

public class StartActivity extends BaseActivity implements OnAdapterSupport {

    private Button showBtn;
    private Button submitBtn;
    private TextView tv_building;
    private TextView tv_status;
    private MaterialEditText edit_room;

    // Bluetooth
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
//    private ArrayAdapter<String> mBTArrayAdapter;
    private String[] btList;


    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private BluetoothListCustomAdapter adapter;


    ArrayList<HashMap<String,String>> dataDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        init();



        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLinearLayoutManager);
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));

        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio


        //블루투스 브로드캐스트 리시버 등록
        //리시버1
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        registerReceiver(mBluetoothStateReceiver, stateFilter);
        //리시버2
        IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        registerReceiver(mBluetoothSearchReceiver, searchFilter);

        makeList();


    }

    private void init(){

        dataDevice = new ArrayList<>();

        showBtn = (Button)findViewById(R.id.btn_show);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(StartActivity.this)
                        .withPermissions(
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){

//                    showBluetoothConnectionMenu();
                            showBluetoothList();

                        }else{
                            showNeedPermissionDialog();
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        showNeedPermissionDialog();

                    }
                }).check();
            }
        });

        submitBtn = (Button)findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSubmit()){

//                    registBluetoothInfo();

                    new MaterialDialog.Builder(StartActivity.this)
                            .title("확인")
                            .content(tv_building.getText() + " / " + edit_room.getText() + " / " + dataDevice.size() + "개 결과")
                            .positiveText("제출")
                            .negativeText("취소")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    registBluetoothInfo();
                                }
                            })
                            .show();

                }else{
                    new MaterialDialog.Builder(StartActivity.this)
                            .title("경고")
                            .content("건물, 방, 블루투스 검색 부족")
                            .positiveText("확인")
                            .show();
                }
            }
        });

        tv_building = (TextView)findViewById(R.id.tv_building);
        tv_building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] buildings = {
                        "아사달연못",
                        "애지헌",
                        "영실관",
                        "용덕관",
                        "우정당",
                        "율곡관",
                        "광개토관",
                        "군자관",
                        "다산관",
                        "대양홀",
                        "이당관",
                        "진관홀",
                        "박물관",
                        "세종관",
                        "세종이노베이션센터",
                        "모짜르트홀",
                        "행복기숙사",
                        "학술정보원",
                        "학생회관",
                        "집현관",
                        "충무관"
                };
                new MaterialDialog.Builder(StartActivity.this)
                        .title("건물 선택")
                        .items(buildings)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                tv_building.setText(text.toString());
                            }
                        })
                        .show();
            }
        });
        tv_status = (TextView)findViewById(R.id.tv_status);
        tv_status.setText("");

        edit_room = (MaterialEditText)findViewById(R.id.edit_room);

    }

    private void registBluetoothInfo(){

        JSONArray jArray = new JSONArray();
        try {
            for (int i = 0; i < dataDevice.size(); i++) {
                JSONObject sObject = new JSONObject();
                sObject.put("rssi", dataDevice.get(i).get("rssi"));
                sObject.put("name", dataDevice.get(i).get("name") == null ? "" : dataDevice.get(i).get("name"));
                sObject.put("address", dataDevice.get(i).get("address"));
                jArray.put(sObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url = "http://172.16.1.250:30000/registRoomBluetooth";
        HashMap<String, String> map = new HashMap<>();
        map.put("building", tv_building.getText().toString());
        map.put("room", edit_room.getText().toString());
        map.put("bluetooth", jArray.toString());


        new ParsePHP(url, map){

            @Override
            protected void afterThreadFinish(String data) {
                println(data);
            }

        }.start();

    }

    private boolean checkSubmit(){
        return dataDevice.size()>0 && !tv_building.getText().equals("") && !tv_building.getText().equals("Building") && edit_room.getText().toString().length() > 0;
    }

    public void makeList(){

        adapter = new BluetoothListCustomAdapter(getApplicationContext(), dataDevice, rv, this);

        rv.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    private void showBluetoothList(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        //mBluetoothAdapter.startDiscovery() : 블루투스 검색 시작
        mBluetoothAdapter.startDiscovery();

    }

    //블루투스 상태변화 BroadcastReceiver
    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //BluetoothAdapter.EXTRA_STATE : 블루투스의 현재상태 변화
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

            //블루투스 활성화
            if(state == BluetoothAdapter.STATE_ON){
//                println("블루투스 활성화");
                tv_status.setText("블루투스 활성화");
            }
            //블루투스 활성화 중
            else if(state == BluetoothAdapter.STATE_TURNING_ON){
                tv_status.setText("블루투스 활성화 중...");
            }
            //블루투스 비활성화
            else if(state == BluetoothAdapter.STATE_OFF){
                tv_status.setText("블루투스 비활성화");
            }
            //블루투스 비활성화 중
            else if(state == BluetoothAdapter.STATE_TURNING_OFF){
                tv_status.setText("블루투스 비활성화 중...");
            }
        }
    };

    //블루투스 검색결과 BroadcastReceiver
    BroadcastReceiver mBluetoothSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    dataDevice.clear();
                    adapter.notifyDataSetChanged();
                    showSnackbar("블루투스 검색 시작");
                    tv_status.setText("블루투스 검색 시작");
//                    Toast.makeText(S.this, "블루투스 검색 시작", Toast.LENGTH_SHORT).show();
                    break;
                //블루투스 디바이스 찾음
                case BluetoothDevice.ACTION_FOUND:
                    //검색한 블루투스 디바이스의 객체를 구한다
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,  Short.MIN_VALUE);
                    //데이터 저장
//                    println(device);
//                    println(rssi);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", device.getName()); //device.getName() : 블루투스 디바이스의 이름
                    map.put("address", device.getAddress()); //device.getAddress() : 블루투스 디바이스의 MAC 주소
                    map.put("rssi", rssi + "");
                    dataDevice.add(map);
//                    println(dataDevice);
                    //리스트 목록갱신
                    adapter.notifyDataSetChanged();
                    break;
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    showSnackbar("블루투스 검색 종료");
                    tv_status.setText("블루투스 검색 종료");
//                    Toast.makeText(MainActivity.this, "블루투스 검색 종료", Toast.LENGTH_SHORT).show();
//                    btnSearch.setEnabled(true);
                    break;
            }
        }
    };


    private void showNeedPermissionDialog(){

        new MaterialDialog.Builder(this)
                .title("확인")
                .content("진행을 위하여 권한 승인이 필요합니다.")
                .positiveText("승인")
                .negativeText("취소")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myAppSettings);

                    }
                })
                .show();

    }

    private void showBluetoothConnectionMenu(){

        String[] items = {
                "페어링된 기기 보기"
        };

        new MaterialDialog.Builder(this)
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position){
                            case 0:
                                listPairedDevices();
                                break;
                        }
                    }
                })
                .show();


    }

    private void listPairedDevices(){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            btList = new String[mPairedDevices.size()];
            int i=0;
            for (BluetoothDevice device : mPairedDevices) {
                btList[i] = device.getName() + "\n" + device.getAddress();
                //mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                i++;
            }


            //new MaterialDialog.Builder(this).title(R.string.connect_bluetooth).adapter(mBTArrayAdapter, null).show();
            showSnackbar("Show Paired Devices");
            showBluetoothDeviceList();
            //Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else{
            showSnackbar("Bluetooth not on");
        }
        //Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private void showBluetoothDeviceList(){

        new MaterialDialog.Builder(this)
                .items(btList)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                        connectBluetoothDevice(btList[position]);
                        System.out.println(btList[position]);
                    }
                })
                .negativeText("취소")
                .show();

    }

    @Override
    public void showView() {

    }

    @Override
    public void hideView() {

    }

    @Override
    public void redirectActivityForResult(Intent intent) {

    }

    @Override
    public void redirectActivity(Intent intent) {

    }
}
