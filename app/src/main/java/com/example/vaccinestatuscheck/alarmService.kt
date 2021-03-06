package com.example.vaccinestatuscheck

import android.app.*
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.kakao.sdk.talk.TalkApiClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link

class alarmService : Service() {
    var mediaPlayer: MediaPlayer? = null
    var channelId = "alarm_check"
    private lateinit var timer: CountDownTimer
    private var tempTime: Long = 0
    var friendId = ""
    var message = ""
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val alarmId = intent.extras!!.getInt("alarmId")
        friendId = intent.extras!!.getString("friendId").toString()
        message = intent.extras!!.getString("message").toString()
//        val getState = intent.extras!!.getString("state")
        Log.d("serviceID", alarmId.toString())
        //        Intent alarmFunction = new Intent(this, AlarmFunctionActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarm = Intent(this, AlarmFunctionActivity::class.java)
            alarm.putExtra("alarmId", alarmId)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addNextIntentWithParentStack(alarm)
            val pendingIntent = stackBuilder.getPendingIntent(alarmId, PendingIntent.FLAG_UPDATE_CURRENT)
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, channelId)
                    .setContentTitle("????????? ?????? ??????!")
                    .setContentIntent(pendingIntent)
                    .setContentText("???????????? ???????????????.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build()
            startForeground(alarmId, notification)
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
            mediaPlayer!!.start()
            mediaPlayer!!.isLooping = true;
            //30??? ????????? ??????
            startTimer()
            //30??? ?????? ??? ?????? ?????? acitivty ?????? ??? ??? ????????? ??????
//            Intent push = new Intent(this, MessagePushActivity.class);
//            push.putExtra("friendId", friendId);
//            push.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(push);
            //Service??? ?????? ??????????????? ?????? ???????????? ?????? Service??? ????????? ?????? ????????? intent ?????? null??? ????????? ????????? ?????????
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestory() ??????", "????????? ??????")
        mediaPlayer!!.stop()
        stopTimer()
        //        unregisterReceiver(alarmReceiverKakao);
    }

    private fun startTimer() {
        timer = object : CountDownTimer(60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tempTime = millisUntilFinished
//                updateTimer()
                Log.d("service", message);
                Log.d("msg", "????????? ??????")
            }

            override fun onFinish() {
                Log.d("msg", "????????? ???")
                Log.d("service", message);
                /*???????????? ????????? ???????????? ??????????????? ???????????? ?????? ??????*/
//                var intent = Intent();
//                var message = intent.getStringExtra("message").toString();
//                Log.d("service", message);
                val defaultFeed = FeedTemplate(
                        content = Content(
                                title = "SOS",
                                description = message,
                                imageUrl = "",
                                link = Link(
                                        webUrl = "https://developers.kakao.com",
                                        mobileWebUrl = "https://developers.kakao.com"
                                )
                        ),
                        buttons = listOf(
                                Button(
                                        "????????? ??????",
                                        Link(
                                                webUrl = "https://developers.kakao.com",
                                                mobileWebUrl = "https://developers.kakao.com"
                                        )
                                ),
                                Button(
                                        "????????? ??????",
                                        Link(
                                                androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                                                iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                                        )
                                )
                        )
                )
                TalkApiClient.instance.sendDefaultMessage(receiverUuids = listOf(friendId) as List<String>, template = defaultFeed) { result, error ->
                    if (error != null) {
                        Log.e(ContentValues.TAG, "????????? ????????? ??????", error)
                    } else if (result != null) {
                        Log.i(ContentValues.TAG, "????????? ????????? ?????? ${result.successfulReceiverUuids}")

                        if (result.failureInfos != null) {
                            Log.d(ContentValues.TAG, "????????? ???????????? ?????? ???????????????, ?????? ??????????????? ?????? \n${result.failureInfos}")
                        }
                    }
                }
            }
        }
        timer.start()
    }

    private fun stopTimer() {
        Log.d("?????????", "??????")
        timer.cancel()
    }
}