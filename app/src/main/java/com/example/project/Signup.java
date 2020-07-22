package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.regex.Pattern;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Signup extends AppCompatActivity {
    //EditTexts
    EditText ename,eemail,eDOB,epassword;
    String name,email,DOB,gender="",password;
    DatePickerDialog picker;
    User user;
    boolean RadioCheck;
    DatabaseHelper dbHelper;
    Preferences utils;
    //for validation
    private AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);
        Intent i=getIntent();
        user=new User();
        dbHelper=new DatabaseHelper(this);
        utils=new Preferences();
        //EditTexts
        ename=(EditText)findViewById(R.id.Name);
        eemail=(EditText)findViewById(R.id.email);
        eDOB=(EditText)findViewById(R.id.DOB);
        epassword=(EditText) findViewById(R.id.password);
        //Function to validate edittexts
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

    }

    //Date Picker
    public void pickDate(View v){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(Signup.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        eDOB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    //Gender Radiobox
    public void pickGender(View v)
    {
        switch(v.getId())
        {
            case R.id.male:
                gender="Male";
                break;
            case R.id.female:
                gender="Female";
                break;
            default:
                gender="";
        }
    }
    //get values of EditTexts
    private void getData()
    {
        user.setName(ename.getText().toString().trim());
        user.setEmail(eemail.getText().toString().trim());
        user.setDOB(eDOB.getText().toString().trim());
        user.setGender(gender.trim());
        user.setPassword(epassword.getText().toString().trim());
        /*name=String.valueOf(ename.getText());
        email=String.valueOf(eemail.getText());
        DOB=String.valueOf(eDOB.getText());
        password=String.valueOf(epassword.getText());*/
    }
    public void validate()
    {
        //validate name
            awesomeValidation.addValidation(this,R.id.Name,"^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$",R.string.error_name1);
        //validate Email
        if(!String.valueOf(eemail.getText()).isEmpty())
        {
            awesomeValidation.addValidation(this,R.id.email,"^[A-Za-z]+[A-Za-z0-9+_.-]*@(.+)$",R.string.error_email1);
        } else {
            awesomeValidation.addValidation(this,R.id.email,"^[A-Za-z]+[A-Za-z0-9+_.-]*@(.+)$",R.string.error_email2);
        }
        //Validate DOB
        awesomeValidation.addValidation(this,R.id.DOB,"^[0-9]{1,2}[/][0-9]{1,2}[/][0-9]{4}$",R.string.error_dob1);
        //Validate Password
        if(!String.valueOf(epassword.getText()).isEmpty())
        {
            if(String.valueOf(epassword.getText()).length()<5)
            {
                awesomeValidation.addValidation(this,R.id.password,"^[a-zA-Z0-9]{5,}$",R.string.error_password2);
            }
            else{
                awesomeValidation.addValidation(this,R.id.password,"^[a-zA-Z0-9]+$",R.string.error_password1);
            }
        }
        else {
            awesomeValidation.addValidation(this,R.id.password,"^[a-zA-Z0-9]{5,}$",R.string.error_password3);
        }
        //Validate Gender
        RadioCheck=TRUE;
        RadioButton gender1=(RadioButton)findViewById(R.id.female);
        RadioGroup gender=(RadioGroup)findViewById(R.id.radio);
        if(gender.getCheckedRadioButtonId()==-1)
        {
            gender1.setError(getString(R.string.error_gender1));//Set error to last Radio button
            RadioCheck=FALSE;
        }
        else{
            gender1.setError(null);
            RadioCheck=TRUE;
        }

    }
    private void setPreferences()
    {
        utils.saveEmail(user.getEmail(),this);
    }
    public void submit(View v){
        validate();
        if(awesomeValidation.validate()&&RadioCheck==TRUE)
        {
            //get and set values of all user EditTexts after validation
            getData();
            if(!dbHelper.checkUser(user.getEmail()))
            {
                Boolean flag=dbHelper.addUser(user);
                if(flag)
                {
                    setPreferences();
                    if(utils.getEmail(this)!=null || !utils.getEmail(this).equals(" "))
                    {
                        Toast.makeText(this, "Successfull", Toast.LENGTH_LONG).show();
                        Intent i2=new Intent(this,Home.class);
                        startActivity(i2);
                    }
                }
            }
            else
            {
                Snackbar.make(v, "Email already Exists", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        }
    }
    public void account(View v)
    {
        Intent i3=new Intent(this,Login.class);
        startActivity(i3);
    }
}
