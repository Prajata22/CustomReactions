package com.github.pgreze.reactions.sample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.pgreze.reactions.sample.R
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.pgreze.reactions.*

class MainActivity : AppCompatActivity() {
    private val strings = arrayOf("Like", "Love", "Haha", "Wow", "Sad", "Angry")
    private val colors = arrayOf(R.color.blue, R.color.red, R.color.yellow, R.color.yellow, R.color.yellow, R.color.orange)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sampleCenterLeft()
//        sampleTopLeft()
//        sampleBottomLeft()
//        setupTopRight()
//        setupRight()
    }

    private fun sampleCenterLeft() {
        val reactionSelectedListener = {
                reaction: Reaction?,
                mediumIconSize: Int,
                eventX: Float,
                eventY: Float,
                dialogX: Int,
                dialogHeight: Int,
                position: Int ->

            Log.i("BAS", "1")

            if(reaction != null) {
                Log.i("BAS", "2")

                Handler(Looper.getMainLooper()).postDelayed({
                    ReactionView(this, reaction).also { reactionView ->
                        val lp = FrameLayout.LayoutParams(
                            mediumIconSize,
                            mediumIconSize
                        )
                        lp.setMargins(eventX.toInt() - 20, eventY.toInt() - (dialogHeight * 1.5).toInt(), 0, 0)

                        reactionView.layoutParams = lp
                        findViewById<FrameLayout>(R.id.rootView).addView(reactionView)

                        val animationArc = ArcTranslationAnimation(
                            0f,
                            -dialogX.toFloat() - ((position / 10f) * dialogX),
                            0f,
                            dialogHeight.toFloat()
                        )
                        animationArc.interpolator = AccelerateDecelerateInterpolator()
                        animationArc.duration = 700
                        animationArc.fillAfter = true

                        animationArc.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {
                                reactionView.animate().scaleX(0.3f).scaleY(0.3f).setDuration(1000).start()
                            }

                            override fun onAnimationEnd(animation: Animation) {
                                findViewById<FrameLayout>(R.id.rootView).removeView(reactionView)
                                findViewById<ImageView>(R.id.image).setImageDrawable(reaction.image)
                                findViewById<TextView>(R.id.text).text = strings[position]
                                findViewById<TextView>(R.id.text).setTextColor(
                                    ContextCompat.getColor(this@MainActivity, colors[position]))
                            }

                            override fun onAnimationRepeat(animation: Animation) {

                            }
                        })

                        reactionView.startAnimation(animationArc)
                    }
                }, 100)
            }

            true
        }

        val popup = ReactionPopup(
            this,
            findViewById(R.id.image),
            ReactionsConfigBuilder(this)
                .withReactions(intArrayOf(
                    R.raw.like,
                    R.raw.love,
                    R.raw.haha,
                    R.raw.wow,
                    R.raw.sad,
                    R.raw.angry))
                .withReactionTexts { position: Int? -> strings[position!!] }
                .build(),
            reactionSelectedListener
        )

        findViewById<View>(R.id.facebook_btn).setOnTouchListener(popup)
    }

//    private fun sampleTopLeft() {
//        val popup = ReactionPopup(
//            this,
//            ReactionsConfigBuilder(this)
//                .withReactions(intArrayOf(
//                    R.drawable.ic_fb_like,
//                    R.drawable.ic_fb_love,
//                    R.drawable.ic_fb_laugh))
//                .withPopupAlpha(20)
//                .withReactionTexts { position: Int? -> strings[position!!] }
//                .withTextBackground(ColorDrawable(Color.TRANSPARENT))
//                .withTextColor(Color.BLACK)
//                .withTextHorizontalPadding(0)
//                .withTextVerticalPadding(0)
//                .withTextSize(resources.getDimension(R.dimen.reactions_text_size))
//                .build())
//        //                position -> true);
//        findViewById<View>(R.id.top_btn).setOnTouchListener(popup)
//    }
//
//    private fun sampleBottomLeft() {
//        val margin = resources.getDimensionPixelSize(R.dimen.crypto_item_margin)
//        val popup = ReactionPopup(this, ReactionsConfigBuilder(this)
//            .withReactions(intArrayOf(
//                R.drawable.ic_crypto_btc,
//                R.drawable.ic_crypto_eth,
//                R.drawable.ic_crypto_ltc,
//                R.drawable.ic_crypto_dash,
//                R.drawable.ic_crypto_xrp,
//                R.drawable.ic_crypto_xmr,
//                R.drawable.ic_crypto_doge,
//                R.drawable.ic_crypto_steem,
//                R.drawable.ic_crypto_kmd,
//                R.drawable.ic_crypto_zec
//            ))
//            .withReactionTexts(R.array.crypto_symbols)
//            .withPopupColor(Color.LTGRAY)
//            .withReactionSize(resources.getDimensionPixelSize(R.dimen.crypto_item_size))
//            .withHorizontalMargin(margin)
//            .withVerticalMargin(margin / 2)
//            .withTextBackground(ColorDrawable(Color.TRANSPARENT))
//            .withTextColor(Color.BLACK)
//            .withTextSize(resources.getDimension(R.dimen.reactions_text_size) * 1.5f)
//            .build())
//
////        popup.setReactionSelectedListener((position) -> {
////            Log.i("Reactions", "Selection position=" + position);
////            // Close selector if not invalid item (testing purpose)
////            return position != 3;
////        });
//        findViewById<View>(R.id.crypto_bottom_left).setOnTouchListener(popup)
//    }
}