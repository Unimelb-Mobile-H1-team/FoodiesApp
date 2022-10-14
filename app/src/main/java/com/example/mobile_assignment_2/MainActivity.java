package com.example.mobile_assignment_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobile_assignment_2.chat.ChatFragment;
import com.example.mobile_assignment_2.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();
        navigation();

    }

    protected void onStart() {

        super.onStart();

        FirebaseUser user = myAuth.getCurrentUser();
        if(user == null){
//            startActivity(new Intent(MainActivity.this, login.class));
        }
    }


    //navigation bar - replace navigation items by id
    public void navigation(){

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item ->{

            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.chat:
                    replaceFragment(new ChatFragment());
                    break;
                case R.id.add:
                    replaceFragment(new AddFragment());
                    break;
                case R.id.community:
                    replaceFragment(new CommunityFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new MeFragment());
                    break;

            }

            return true;

        });

    }


    //replace navigation items by id
    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }


}