package com.stardust.autojs.inrt.autojs

import android.view.KeyEvent
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.core.inputevent.InputEventObserver
import com.stardust.autojs.core.inputevent.ShellKeyObserver
import com.stardust.autojs.inrt.Pref
import com.stardust.view.accessibility.AccessibilityService
import com.stardust.view.accessibility.OnKeyListener

/**
 * Created by Stardust on 2017/8/14.
 */

class GlobalKeyObserver internal constructor() : OnKeyListener, ShellKeyObserver.KeyListener {
    private var mVolumeDownFromShell: Boolean = false
    private var mVolumeDownFromAccessibility: Boolean = false
    private var mVolumeUpFromShell: Boolean = false
    private var mVolumeUpFromAccessibility: Boolean = false

    init {
        AccessibilityService.stickOnKeyObserver
                .addListener(this)
        val observer = ShellKeyObserver()
        observer.setKeyListener(this)
        InputEventObserver.getGlobal(GlobalAppContext.get()).addListener(observer)
    }

    fun onVolumeUp() {
        if (Pref.shouldStopAllScriptsWhenVolumeUp()) {
            AutoJs.instance.scriptEngineService.stopAllAndToast()
        }
    }

    override fun onKeyEvent(keyCode: Int, event: KeyEvent) {
        if (event.action != KeyEvent.ACTION_UP)
            return
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (mVolumeDownFromShell) {
                mVolumeDownFromShell = false
                return
            }
            mVolumeUpFromAccessibility = true
            onVolumeDown()
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (mVolumeUpFromShell) {
                mVolumeUpFromShell = false
                return
            }
            mVolumeUpFromAccessibility = true
            onVolumeUp()
        }
    }

    fun onVolumeDown() {

    }


    override fun onKeyDown(keyName: String) {

    }

    override fun onKeyUp(keyName: String) {
        if ("KEY_VOLUMEUP" == keyName) {
            if (mVolumeUpFromAccessibility) {
                mVolumeUpFromAccessibility = false
                return
            }
            mVolumeUpFromShell = true
            onVolumeUp()
        } else if ("KEY_VOLUMEDOWN" == keyName) {
            if (mVolumeDownFromAccessibility) {
                mVolumeDownFromAccessibility = false
                return
            }
            mVolumeDownFromShell = true
            onVolumeDown()
        }
    }

    companion object {


        private val LOG_TAG = "GlobalKeyObserver"
        private val sSingleton = GlobalKeyObserver()

        fun init() {
            //do nothing
        }
    }
}
