package org.qpeterp.mymusic

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import org.qpeterp.mymusic.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var isPlaying: Boolean = false
    private val playList = arrayListOf(
        R.raw.hey,
        R.raw.dreams,
        R.raw.happyrock
    )
    private var playIndex = 0
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var animationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initMediaPlayer()
        initButtonClick()
        initNextButtonClick("2")
        initPreviousButtonClick()
        setSongTitle()
        moveSeekBar()

        binding.loopBtn.setOnClickListener {
            initLoopBtnClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
    private fun initLoopBtnClick() {
        val loopBtn = binding.loopBtn
        if (mediaPlayer.isLooping) {
            loopBtn.setImageResource(R.drawable.arrow_next)
            mediaPlayer.isLooping = false
        } else {
            loopBtn.setImageResource(R.drawable.baseline_loop)
            mediaPlayer.isLooping = true
        }
    }

    private fun initMediaPlayer() {
        animationView = binding.listeningGirl
        animationView.setAnimation("animation_listeningGirl.json")
        mediaPlayer = MediaPlayer.create(this, R.raw.hey)
        // 노래 재생이 끝나면 다음 노래 재생을 위해 리스너 등록
        firstMusicPlayer()
        initLoopBtnClick()
    }

    private fun firstMusicPlayer() {
        mediaPlayer.start()
        animationView.playAnimation()
        binding.playArrow.setImageResource(R.drawable.baseline_pause)
        binding.songNumberTextView.text = "재생목록 (${playIndex+1}/${playList.size})"

        val Mm = mediaPlayer.getDuration() / 60000
        val Ms = mediaPlayer.getDuration() % 60000 / 1000

        binding.maxSec.text = String.format("%02d:%02d", Mm, Ms)

        val seekBar = binding.seekbar

        seekBar.setVisibility(ProgressBar.VISIBLE)
        seekBar.setMax(mediaPlayer.getDuration())

        mediaPlayer.setOnCompletionListener {
            initNextButtonClick("1")
        }

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }

                val m = progress / 60000
                val s = progress % 60000 / 1000
                binding.playingSec.text = String.format("%02d:%02d", m, s)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun initButtonClick() {
        binding.playArrow.setOnClickListener {
            if (isPlaying) {
                musicPause()
            } else {
                musicPlaying()
            }
            isPlaying = !isPlaying
        }
    }

    private fun initPreviousButtonClick() {
        binding.skipPrevious.setOnClickListener {
            mediaPlayer.release()
            playIndex--
            if (playIndex <= -1) {
                playIndex = playList.size - 1
            }
            val music = playList[playIndex]
            mediaPlayer = MediaPlayer.create(this, music)
            firstMusicPlayer()
            setSongTitle()
        }
    }

    private fun initNextButtonClick(LLoooooop: String) {
        if (LLoooooop == "1") {
            mediaPlayer.release()
            playIndex++
            if (playIndex >= playList.size) {
                playIndex = 0
            }
            val music = playList[playIndex]
            mediaPlayer = MediaPlayer.create(this, music)
            isPlaying = !isPlaying
            firstMusicPlayer()
            setSongTitle()
        } else {
            binding.skipNext.setOnClickListener {
                mediaPlayer.release()
                playIndex++
                if (playIndex >= playList.size) {
                    playIndex = 0
                }
                val music = playList[playIndex]
                mediaPlayer = MediaPlayer.create(this, music)
                isPlaying = !isPlaying
                firstMusicPlayer()
                setSongTitle()
            }
        }
    }

    private fun musicPlaying() {
        mediaPlayer.start()
        animationView.playAnimation()
        binding.playArrow.setImageResource(R.drawable.baseline_pause)
        moveSeekBar()
    }

    private fun musicPause() {
        animationView.pauseAnimation()
        mediaPlayer.pause()
        binding.playArrow.setImageResource(R.drawable.play_arrow)
    }

    private fun setSongTitle() {
        binding.songTitle.text = when(playIndex) {
            0 -> "Hey"
            1 -> "Dreams"
            else -> "Happyrock"
        }
    }

    private fun moveSeekBar() {
        val seekBar = binding.seekbar

        Thread {
            while (mediaPlayer.isPlaying()) {  // 음악이 실행중일때 계속 돌아가게 함
                try {
                    Thread.sleep(1000) // 1초마다 시크바 움직이게 함
                    if (seekBar == binding.maxSec) {
                        firstMusicPlayer()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // 현재 재생중인 위치를 가져와 시크바에 적용
                seekBar.setProgress(mediaPlayer.getCurrentPosition())
            }
        }.start()
    }

}