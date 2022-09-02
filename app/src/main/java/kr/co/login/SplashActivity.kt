package kr.co.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    //var imageView4: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        var nameView_1: ImageView? = null
        var nameView_2: ImageView? = null
        var imageView: ImageView? = null

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        imageView = findViewById(R.id.imageView)
        nameView_1 = findViewById(R.id.nameView_1)
        nameView_2 = findViewById(R.id.nameView_2)

        Handler(Looper.getMainLooper()).postDelayed({
            val fadeInAnimation2: Animation = AlphaAnimation(0.0f, 1.0f)
            fadeInAnimation2.duration = 2000
            fadeInAnimation2.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // TODO Auto-generated method stub
                    imageView.setVisibility(View.VISIBLE)
                    nameView_1.setVisibility(View.VISIBLE)
                    nameView_2.setVisibility(View.VISIBLE)
                }




                override fun onAnimationRepeat(animation: Animation) {
                    // TODO Auto-generated method stub
                }

                override fun onAnimationEnd(animation: Animation) {
                    Handler(Looper.getMainLooper()).postDelayed({ StartMainActivity() }, 500)
                }
            })
            imageView.startAnimation(fadeInAnimation2)
            nameView_1.startAnimation(fadeInAnimation2)
            nameView_2.startAnimation(fadeInAnimation2)
        }, 300)
    }



    private fun StartMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}