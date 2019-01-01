package rajat.com.notesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    ListView listView;
    Button btn;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    SharedPreferences sharedPreferences;
    int id;
    String  sid;
    MenuItem menuItem;
    Menu add;
    boolean state=true,isHold=false;
    String clickedId="";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.activity_menu,menu);
        add=menu;
        menuItem=menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }
    void getList(){

        Map<String,?> mp=sharedPreferences.getAll();
        Set<String> s=mp.keySet();
        SortedSet<String> ss= new TreeSet(s).descendingSet();
        arrayList.clear();
        arrayAdapter.clear();
        for(String st:ss){
            if(!st.equals("id")){
                arrayList.add(sharedPreferences.getString(st,""));
            }
        }
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

    }
    void genertateId(){
        sid="U"+id;
    }
    void addItem(){
        genertateId();
        editText.setText("");
        listView.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
    }
    public void onSave(View view){

        if(editText.getText()+""==""){
            Toast.makeText(this, "Please Enter Something!! ", Toast.LENGTH_SHORT).show();
        }
        else{
            if(state)
            {
                id++;
                sharedPreferences.edit().putInt("id",id).apply();
                sharedPreferences.edit().putString(sid,editText.getText()+"").apply();
            }
            else{
                sharedPreferences.edit().putString(clickedId,editText.getText()+"").apply();

            }
            btn.setVisibility(View.INVISIBLE);
            editText.setVisibility(View.INVISIBLE);
            getList();
            listView.setVisibility(View.VISIBLE);
            state=true;
            menuItem.setEnabled(true);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setEnabled(false);
        String data=item.getTitle().toString();
        switch (data){
            case "Add":
                addItem();
                return true;
            case "Exit":
                android.os.Process.killProcess(android.os.Process.myPid());
            default:
                Toast.makeText(this, "Not a default", Toast.LENGTH_SHORT).show();
                return false;
        }
    }
    void getData(int index){
        listView.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        String temp=arrayList.get(index);
        editText.setText(temp);
        Map<String,?> mp=sharedPreferences.getAll();
        if(mp.containsValue(temp)){
            Set<String> s=mp.keySet();
            for(String k:s){
                if(!k.equals("id")){
                    String temp2;
                    temp2=sharedPreferences.getString(k,"");
                    if(temp2==temp){
                        clickedId=k;
                        break;
                    }
                }

            }
        }
    }
    void delete(int i){
        String temp=arrayList.get(i);
        Map<String,?> mp=sharedPreferences.getAll();
        if(mp.containsValue(temp)){
            Set<String> s=mp.keySet();
            for(String k:s){
                if(!k.equals("id")){
                    String temp2;
                    temp2=sharedPreferences.getString(k,"");
                    if(temp2==temp){
                        clickedId=k;
                        break;
                    }
                }

            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        arrayList=new ArrayList<String>();
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=(EditText)findViewById(R.id.editText);
        editText.setVisibility(View.INVISIBLE);
        sharedPreferences=this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        id=sharedPreferences.getInt("id",1000);
        listView=(ListView)findViewById(R.id.listView);
        btn=(Button)findViewById(R.id.save);
        btn.setVisibility(View.INVISIBLE);
        getList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!isHold){
                    view.setVisibility(View.INVISIBLE);
                    state=false;
                    getData(i);
                }
                else {
                    isHold=false;
                }

            }



        });
        final MainActivity m=this;
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Toast.makeText(MainActivity.this, "HOLD IT!!!", Toast.LENGTH_SHORT).show();
                isHold=true;
                new AlertDialog.Builder(m).setTitle("Delete?").
                        setMessage("Do You Really Want to DELETE")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int inter) {
                                delete(i);
                                sharedPreferences.edit().remove(clickedId).apply();
                                getList();
                                Toast.makeText(m, "DONE!!!", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No",null).show();
                getList();

                return false;
            }
        });


    }
}
