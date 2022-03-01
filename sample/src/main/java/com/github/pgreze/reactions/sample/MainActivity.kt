package com.github.pgreze.reactions.sample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.pgreze.reactions.*

class MainActivity : AppCompatActivity(), ApplexGestureListener {
    private val strings = arrayOf("Pranam", "Dhak", "Diya", "Flower", "Dhup", "Ghanta")
    private val colors = arrayOf(R.color.blue, R.color.red, R.color.yellow, R.color.yellow, R.color.yellow, R.color.orange)

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var button: LinearLayout
    private lateinit var frameLayout: FrameLayout

    private var isLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image)
        textView = findViewById(R.id.text)
        button = findViewById(R.id.facebook_btn)
        frameLayout = findViewById(R.id.rootView)

        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like))
        textView.text = strings[0]
        textView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text_secondary))

        reactions()
    }

    private fun reactions() {
        val reactionSelectedListener = {
                reaction: Reaction?,
                mediumIconSize: Int,
                eventX: Float,
                eventY: Float,
                dialogX: Int,
                dialogHeight: Int,
                position: Int ->

            if(reaction != null) {
                isLiked = true
                Handler(Looper.getMainLooper()).postDelayed({
                    ReactionView(this, reaction).also { reactionView ->
                        val lp = FrameLayout.LayoutParams(
                            mediumIconSize,
                            mediumIconSize
                        )
                        lp.setMargins(eventX.toInt() - 20, eventY.toInt() - (dialogHeight * 1.5).toInt(), 0, 0)

                        reactionView.layoutParams = lp
                        frameLayout.addView(reactionView)

                        val toXDelta = if(position == 5 || position == 6) {
                            -dialogX.toFloat() - ((position / 10f) * dialogX)
                        } else if(position == 1) {
                            -dialogX.toFloat() + 150
                        } else if(position == 0) {
                            -dialogX.toFloat() + 250
                        } else {
                            -dialogX.toFloat()
                        }

                        val animationArc = ArcTranslationAnimation(
                            0f,
                             toXDelta,
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
                                frameLayout.removeView(reactionView)
                                imageView.setImageDrawable(reaction.image)
                                textView.text = strings[position]
                                textView.setTextColor(ContextCompat.getColor(this@MainActivity, colors[position]))
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
            this,
            button,
            ReactionsConfigBuilder(this)
                .withReactions(intArrayOf(
                    R.raw.namaste,
                    R.raw.dhak,
                    R.raw.lamp,
                    R.raw.flower,
                    R.raw.incense,
                    R.raw.bell))
                .withReactionTexts { position: Int? -> strings[position!!] }
                .build(),
            reactionSelectedListener
        )

        button.setOnTouchListener(popup)
    }

    override fun onSingleClick() {
        if(isLiked) {
            isLiked = false
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like))
            textView.text = strings[0]
            textView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text_secondary))
        } else {
            isLiked = true
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.namaste))
            textView.text = strings[0]
            textView.setTextColor(ContextCompat.getColor(this@MainActivity, colors[0]))
        }
    }

    override fun onLongClick() {
        button.visibility = View.GONE
    }
}