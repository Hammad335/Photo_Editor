package com.codewithhamad.editor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.codewithhamad.editor.databinding.ActivityMainBinding;
import com.codewithhamad.editor.databinding.ActivityResultBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();


        binding.resultImage.setImageURI(getIntent().getData());

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.show(ResultActivity.this);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("check", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });    }
}