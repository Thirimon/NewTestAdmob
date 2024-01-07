package com.example.newtestadmob

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.window.layout.WindowMetricsCalculator
import com.example.newtestadmob.ui.theme.NewTestAdmobTheme
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        MobileAds.setRequestConfiguration(
//            RequestConfiguration.Builder()
//                .setTestDeviceIds(listOf("74B6E2185358380DDEA7460B62F87B3D")).build()
//            // RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
//        )
        setContent {
            MobileAds.initialize(this) {}
            NewTestAdmobTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //BannerAd()
                        Scaffold(topBar = {
                            TopAppBar(title = { Text(text = resources.getString(R.string.app_name)) },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                                ))
                        }) {
                           // BannerAd(Modifier.padding(it))
                            InterstitiaAdTest(context = this,Modifier.padding(it))
                        }

                    }
                }
            }
        }
    }


@Composable
fun BannerAd(modifier: Modifier=Modifier) {

    AndroidView(modifier = modifier
        .fillMaxWidth()
        .height(300.dp),

        factory = { context ->
            val windowMetrics: androidx.window.layout.WindowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context)
            val bounds = windowMetrics.bounds

            var adWidthPixels: Float = context.resources.displayMetrics.widthPixels.toFloat()

            if (adWidthPixels == 0f) {
                adWidthPixels = bounds.width().toFloat()
            }

            val density: Float = context.resources.displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            AdView(context).apply {

                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth))
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }


        })
}
@Composable
fun InterstitiaAdTest(context:Context,modifier:Modifier=Modifier){
  Column(
      modifier = modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
  ){
      var mInterstitialAd: InterstitialAd?=null
      val btnText=remember{ mutableStateOf("Loading interstitial Ad") }
      val btnEnable= remember { mutableStateOf(false) }
      fun loadInterstitialAd(context: Context){
          InterstitialAd.load(context,"ca-app-pub-3940256099942544/1033173712",AdRequest.Builder().build(),
              object :InterstitialAdLoadCallback(){
                  override fun onAdFailedToLoad(adError: LoadAdError) {
                      super.onAdFailedToLoad(adError)
                      mInterstitialAd=null
                  }

                  override fun onAdLoaded(interstitialAd: InterstitialAd) {
                      mInterstitialAd=interstitialAd
                      btnText.value="Show interstitial Ad"
                      btnEnable.value=true
                  }
              }
              )
      }
      fun showInterstitialAd(context: Context,onAdDismissed: ()->Unit){
          if(mInterstitialAd !=null){
              mInterstitialAd?.fullScreenContentCallback=object :FullScreenContentCallback(){
                  override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                     mInterstitialAd=null
                  }

                  override fun onAdDismissedFullScreenContent() {
                      mInterstitialAd=null
                      loadInterstitialAd(context)
                      onAdDismissed()
                      btnText.value="Loading interstitial Ad"
                      btnEnable.value=false
                  }
              }
              mInterstitialAd?.show(context as Activity)
          }
      }
      loadInterstitialAd(context)
      val coroutineScope= rememberCoroutineScope()
      Button(enabled = btnEnable.value,onClick = {
          coroutineScope.launch {
              showInterstitialAd(context){
                  Toast.makeText(context,"Interstitial Ad Shown!",Toast.LENGTH_LONG).show()
              }
          }
      }) {
          Text(text = btnText.value)
      }
  }
}