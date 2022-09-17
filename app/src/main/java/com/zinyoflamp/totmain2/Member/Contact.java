package com.zinyoflamp.totmain2.Member;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zinyoflamp.totmain2.R;

public class Contact extends AppCompatActivity{

    Button agreebtn;
    RadioButton contact2agree, contact2disagree, contactagree, contactdisagree;
    RadioGroup radioGroup1, radioGroup2;
    Boolean aboutagree1, aboutagree2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_contact);

        radioGroup1=(RadioGroup)findViewById(R.id.radioGroup1);
        radioGroup2=(RadioGroup)findViewById(R.id.radioGroup2);
        contactagree=(RadioButton)findViewById(R.id.contactagree);
        contactdisagree=(RadioButton)findViewById(R.id.contactdisagree);
        contact2agree=(RadioButton)findViewById(R.id.contact2agree);
        contact2disagree=(RadioButton)findViewById(R.id.contact2disagree);
        agreebtn=(Button)findViewById(R.id.agreejoin);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                if(contactdisagree.isChecked()){
                    Toast.makeText(getApplicationContext(),"동의하지 않으시면 회원가입이 불가능합니다.",Toast.LENGTH_SHORT).show();
                    agreebtn.setEnabled(false);
                }else{
                    Toast.makeText(getApplicationContext(),"동의해 주셔서 감사합니다.",Toast.LENGTH_SHORT).show();
                    agreebtn.setEnabled(true);
                }
            }
        });

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
                if(contact2disagree.isChecked()){
                    Toast.makeText(getApplicationContext(),"동의하지 않으시면 회원가입이 불가능합니다.",Toast.LENGTH_SHORT).show();
                    agreebtn.setEnabled(false);
                }else{
                    Toast.makeText(getApplicationContext(),"동의해 주셔서 감사합니다.",Toast.LENGTH_SHORT).show();
                    agreebtn.setEnabled(true);
                }
            }
        });

        agreebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent=new Intent(getApplicationContext(),JoinForm.class);
                startActivity(joinIntent);

            }
        });


    }
}
