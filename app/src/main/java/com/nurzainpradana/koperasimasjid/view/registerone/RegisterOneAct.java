package com.nurzainpradana.koperasimasjid.view.registerone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nurzainpradana.koperasimasjid.R;
import com.nurzainpradana.koperasimasjid.view.registertwo.RegisterTwoAct;
import com.nurzainpradana.koperasimasjid.api.Api;
import com.nurzainpradana.koperasimasjid.api.ApiInterface;
import com.nurzainpradana.koperasimasjid.model.Member;
import com.nurzainpradana.koperasimasjid.model.ResultMember;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterOneAct extends AppCompatActivity {

    Button btnRegisterOneNext;
    EditText edtName;
    EditText edtNoPhone;
    EditText edtUsername;
    EditText edtPassword;

    List<Member> List = new ArrayList<>();
    ApiInterface Service;
    Call<ResultMember> Call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);
        getAllMember();

        btnRegisterOneNext = findViewById(R.id.btn_register_one_next);
        edtName = findViewById(R.id.edt_name);
        edtNoPhone = findViewById(R.id.edt_no_phone);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);

        btnRegisterOneNext.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String name = edtName.getText().toString();
            String noPhone = edtNoPhone.getText().toString();
            String password = edtPassword.getText().toString();

            Member member = new Member();
            member.setmName(name);
            member.setmNoPhone(noPhone);
            member.setmUsername(username);
            member.setmPassword(md5Java(password));

            if (!checkUsername(username)) {
                Intent goToRegisterTwo = new Intent(RegisterOneAct.this, RegisterTwoAct.class);
                goToRegisterTwo.putExtra(RegisterTwoAct.EXTRA_MEMBER, member);
                RegisterOneAct.this.startActivity(goToRegisterTwo);
            } else {
                Toast.makeText(RegisterOneAct.this, getString(R.string.username_already_registered), Toast.LENGTH_SHORT).show();
                edtUsername.setError(getString(R.string.username_already_registered));
                edtUsername.requestFocus();
            }
        });
    }

    private boolean checkUsername(String username) {
        boolean isAlready = false;
        if (List.toArray().length > 0){
            for (int i = 0; i < List.toArray().length; i++) {
                boolean checkUsername = (username.equals(List.get(i).getmUsername()));
                //Jika username sudah terdaftar
                if (checkUsername)
                    isAlready = true;
            }
        }
        return isAlready;
    }

    private void getAllMember() {
        Service = Api.getApi().create(ApiInterface.class);
        Call = Service.getAllMember();
        Call.enqueue(new Callback<ResultMember>() {
            @Override
            public void onResponse(retrofit2.Call<ResultMember> call, Response<ResultMember> response) {
                List.clear();
                if (response.body() != null)
                    List = response.body().getmResultMember();
            }

            @Override
            public void onFailure(retrofit2.Call<ResultMember> call, Throwable t) {
                Log.e("Error Bosque", t.toString());
            }
        });
    }

    public static String md5Java(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));

            //merubah byte array ke dalam String Hexadecimal
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash)
            {
                sb.append(String.format("%02x", b&0xff));
            }
            digest = sb.toString();
        } catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(RegisterOneAct.class.getName()).log(Level.SEVERE, null, ex);
        }
        return digest;
    }
}
