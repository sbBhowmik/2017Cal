package com.rupik.a2017calendar;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Button doneButton = (Button)findViewById(R.id.buttonDone);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        Button skipButton = (Button)findViewById(R.id.buttonSkip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void validateInputs()
    {
        EditText editTextEmail_Profile = (EditText)findViewById(R.id.editTextEmail_Profile);
        EditText pageET = (EditText)findViewById(R.id.editText2);
        EditText ageET = (EditText)findViewById(R.id.editText3);
        EditText bdET = (EditText)findViewById(R.id.editText7);
        EditText interestsET = (EditText)findViewById(R.id.editText);

        SharedPreferences sp = getSharedPreferences("your_prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if(pageET.getText().toString()!=null)
        {
            if(pageET.getText().toString().length()>0)
            {
                editor.putString("pageLink",pageET.getText().toString());
            }
        }

        String interestsString = interestsET.getText().toString();
        if(interestsString!=null)
        {
            if(interestsString.length()==0){
                Toast.makeText(this,"Please enter your areas of interest or hobby like Games, TV, Movies, Travel, Food etc",Toast.LENGTH_LONG).show();
                return;
            }
        }
        else {
            Toast.makeText(this,"Please enter your areas of interest or hobby like Games, TV, Movies, Travel, Food etc",Toast.LENGTH_LONG).show();
            return;
        }

        editor.putString("interestsString",interestsString);

        String email = editTextEmail_Profile.getText().toString();
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this,"Please enter valid email address",Toast.LENGTH_LONG).show();
            //alert Please enter valid email address
            return;
        }

        editor.putString("email",email);

        String ageStr = ageET.getText().toString();
        try {
            int age = Integer.parseInt(ageStr);
            if(age<=0 || age>90) {
                Toast.makeText(this,"Please enter a valid age",Toast.LENGTH_LONG).show();;
                return;
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Please enter a valid age",Toast.LENGTH_LONG).show();;
            // alert: Please enter a valid age
            return;
        }

        editor.putString("ageStr",ageStr);

        String bDayText = "";
        try {
            bDayText = bdET.getText().toString();
            String components[] = bDayText.split("/");
            if (components.length != 3) {
                //alert: Please enter a valid birthday
                Toast.makeText(this,"Please enter a valid birthday",Toast.LENGTH_LONG).show();;
                return;
            } else {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(components[2]));
                cal.set(Calendar.MONTH, Integer.parseInt(components[1]));
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(components[0]));
                Date usrBDate = cal.getTime();

                int age = getAge(Integer.parseInt(components[2]), Integer.parseInt(components[1]), Integer.parseInt(components[0]));
                if(age<=0 || age>90) {
                    //alert: Please enter a valid birthday.
                    Toast.makeText(this,"Please enter a valid birthday",Toast.LENGTH_LONG).show();;
                    return;
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Please enter a valid birthday",Toast.LENGTH_LONG).show();;
            return;
        }

        editor.putString("bDayText",bDayText);

        RadioButton femaleRadioBtn = (RadioButton) findViewById(R.id.radio1);
        if(femaleRadioBtn.isChecked())
        {
            editor.putBoolean("isFemale",true);
        }
        else {
            editor.putBoolean("isFemale",false);
        }



        editor.putBoolean("isProfileUpdated", true);

        editor.commit();

        finish();
    }

    public int getAge (int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0) {
            //
        }
        return a;
    }
}
