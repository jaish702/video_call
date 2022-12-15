package com.example.video_call

import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.*
import android.content.BroadcastReceiver
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import com.example.video_call.VideoCallPlugin.Companion.video_call_CLOSE
import com.example.video_call.VideoCallPlugin.Companion.video_call_TAG
import com.facebook.react.ReactRootView
import com.facebook.react.views.text.ReactTextView
import com.facebook.react.views.view.ReactViewGroup
import org.jitsi.meet.sdk.*
import java.util.*


/**
 * Activity extending VideoCallActivity in order to override the conference events
 */
class VideoCallPluginActivity : VideoCallActivity() {
    companion object {

        var classroomLogo: String? = null;
        var whiteboardUrl: String? = null;
        var classroomLogoId: Int? = null;
        var context: Context? = null;

        @JvmStatic
        fun launchActivity(
            context: Context?,
            options: JitsiMeetConferenceOptions
        ) {
            var intent = Intent(context, VideoCallPluginActivity::class.java).apply {
                action = "org.jitsi.meet.CONFERENCE"
                putExtra("JitsiMeetConferenceOptions", options)
            }
            context?.startActivity(intent)
            this.context = context;
        }

        @JvmStatic
        fun setData(classroomLogo: String?, whiteboardUrl: String?): Unit {
            this.classroomLogo = classroomLogo;
            this.whiteboardUrl = whiteboardUrl;
            if (this.classroomLogo != null) {
                this.classroomLogoId = this.context!!.resources.getIdentifier(
                    this.classroomLogo,
                    "drawable",
                    context!!.packageName
                );
            }
            Log.i(
                video_call_TAG,
                "classroomLogo [" + classroomLogo + "] whiteboardUrl [" + whiteboardUrl + "]"
            );
        }
    }

    var onStopCalled: Boolean = false;
    var videoLayout: LinearLayout? = null;


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            VideoCallEventStreamHandler.instance.onPictureInPictureWillEnter()
            this.videoLayout!!.setVisibility(LinearLayout.GONE);
        } else {
            VideoCallEventStreamHandler.instance.onPictureInPictureTerminated()
            this.videoLayout!!.setVisibility(LinearLayout.VISIBLE);
        }
        if (isInPictureInPictureMode == false && onStopCalled) {
            // Picture-in-Picture mode has been closed, we can (should !) end the call
            getJitsiView().leave()
        }
    }

    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent?.action) {
                video_call_CLOSE -> finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        onStopCalled = true;
        unregisterReceiver(myReceiver)
    }

    override fun onResume() {
        super.onResume()
        onStopCalled = false
        registerReceiver(myReceiver, IntentFilter(video_call_CLOSE))
    }

    override fun onConferenceWillJoin(data: HashMap<String, Any>?) {
        Log.d(video_call_TAG, String.format("VideoCallPluginActivity.onConferenceWillJoin: %s", data))
        VideoCallEventStreamHandler.instance.onConferenceWillJoin(data)
        super.onConferenceWillJoin(data)
    }

    override fun onConferenceJoined(data: HashMap<String, Any>?) {
        Log.d(video_call_TAG, String.format("VideoCallPluginActivity.onConferenceJoined: %s", data))
        VideoCallEventStreamHandler.instance.onConferenceJoined(data)
        super.onConferenceJoined(data)
        this.test();
    }

    override fun onConferenceTerminated(data: HashMap<String, Any>?) {

        Log.d(
            video_call_TAG,
            String.format("VideoCallPluginActivity.onConferenceTerminated: %s", data)
        )
        VideoCallEventStreamHandler.instance.onConferenceTerminated(data)
        super.onConferenceTerminated(data)
    }

    override fun onParticipantLeft(data: HashMap<String, Any>?) {
        Log.d(video_call_TAG, String.format("VideoCallPluginActivity.onParticipantLeft: %s", data))
        VideoCallEventStreamHandler.instance.onParticipantLeft(data)
        super.onConferenceTerminated(data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff();
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        Log.i(video_call_TAG, "ABC Post Create");
        super.onPostCreate(savedInstanceState);
//        logContentView(getWindow().getDecorView(), "");
        val view = window.decorView as ViewGroup;
        Log.d(video_call_TAG, "ABC " + view.javaClass.canonicalName);
        val layout: LinearLayout = view.getChildAt(0) as LinearLayout;
        prepareWhiteboardLayout(layout);

    }

    fun test() {
        if(true){
            return;
        }
        try {
            var jitsiView: JitsiMeetView = jitsiView;
            Log.d(video_call_TAG, "ABC " + jitsiView.javaClass.canonicalName);
            var ab = jitsiView.getRootReactView(jitsiView);
            Log.d(video_call_TAG, "ABC " + ab.javaClass.canonicalName);
            var rootReactView: ReactRootView = ab as ReactRootView;
            Log.d(video_call_TAG, "ABC " + rootReactView.javaClass.canonicalName);
            logContentView(rootReactView.rootViewGroup, "");
        } catch (ex: Exception) {
            Log.e(video_call_TAG, "ABC Error", ex);
        }
//     
    }

    fun prepareWhiteboardLayout(layout: LinearLayout) {
        this.videoLayout = LinearLayout(this);
        this.videoLayout!!.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        this.videoLayout!!.setPadding(25, 25, 25, 25)

        this.videoLayout!!.gravity = Gravity.LEFT;
        var logoParentlayout: LinearLayout = LinearLayout(this);
        logoParentlayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        logoParentlayout.gravity = Gravity.LEFT;
        val logoImage = ImageView(this);
        //logoImage.setImageURI(Uri.parse("https://www.example.in/wp-content/uploads/2021/02/vidyartham@2x_1.png"));
        if (VideoCallPluginActivity.classroomLogoId != null) {
            logoImage.setImageResource(VideoCallPluginActivity.classroomLogoId!!);
        }
        logoImage.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            100
        );
        logoImage.id = View.generateViewId();
        logoImage.scaleType = ImageView.ScaleType.FIT_START;
        logoImage.adjustViewBounds = true;

        var btnParentlayout: LinearLayout = LinearLayout(this);
        btnParentlayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        btnParentlayout.gravity = Gravity.RIGHT;

        val btnTag = Button(this)
        btnTag.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            100
        );
        btnTag.text = "Whiteboard";
        btnTag.id = View.generateViewId();
        btnTag.setBackgroundColor(Color.BLACK);
        if (VideoCallPluginActivity.whiteboardUrl != null) {
            btnTag.setTextColor(Color.WHITE);
            btnTag.setOnClickListener {
                VideoCallEventStreamHandler.instance.onWhiteboardClicked();
//                Toast.makeText(this, "Whiteboard", Toast.LENGTH_SHORT).show()
                val alert: AlertDialog.Builder = AlertDialog.Builder(this)
                alert.setTitle("Whiteboard")

                val wv = WebView(this)
                wv.loadUrl(whiteboardUrl)
                wv.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return true
                    }
                }
                wv.settings.javaScriptEnabled = true;
                wv.settings.javaScriptCanOpenWindowsAutomatically = true;
                wv.settings.domStorageEnabled = true;

                alert.setView(wv)
                alert.setNegativeButton("Close",
                    DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() });
                alert.show()
            }

        } else {
            btnTag.setTextColor(Color.BLACK);
        }

        layout.setBackgroundColor(Color.BLACK);
        logoParentlayout.addView(logoImage);
        btnParentlayout.addView(btnTag);
        this.videoLayout!!.addView(logoParentlayout);
        this.videoLayout!!.addView(btnParentlayout);
        layout.addView(videoLayout, 0);
    }

    fun logContentView(parent: View, indent: String) {
        if (parent is ReactViewGroup) {
            var abc = parent as ReactViewGroup;
            Log.i("ABC test", indent + parent.javaClass.name + " - Tag "+ abc.tag)
        } else if (parent is ReactTextView) {
            var abc = parent as ReactTextView;
            Log.i("ABC test", indent + parent.javaClass.name + " - Text " + abc.text)
        } else {
            Log.i("ABC test", indent + parent.javaClass.name)
        }
        if (parent is ViewGroup) {
            val group = parent
            for (i in 0 until group.childCount) logContentView(group.getChildAt(i), "$indent ")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn();
    }

    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // For newer than Android Oreo: call setShowWhenLocked, setTurnScreenOn
            setShowWhenLocked(true)
            setTurnScreenOn(true)

            // If you want to display the keyguard to prompt the user to unlock the phone:
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            // For older versions, do it as you did before.
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_FULLSCREEN
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }

    private fun turnScreenOffAndKeyguardOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_FULLSCREEN
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }
}

fun BaseReactView<JitsiMeetViewListener>.getRootReactView(view: JitsiMeetView): Any {

    return BaseReactView::class.java.getDeclaredField("reactRootView").let {
        it.isAccessible = true;
        val value = it.get(view);
        //todo
        return@let value;
    }

//    return this.reactRootView;
}