package com.example.webviewsample

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.example.webviewsample.database.Code
import com.example.webviewsample.database.MyAppDatabase

/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context) {

    companion object {
        val TAG = WebAppInterface::class.java.simpleName.trim()
    }

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface fun getHtml(html: String) {
        Log.d(TAG, "=================Start=============== ")
        mLog(html)
        Log.d(TAG, "================Finished=============== ")
    }

    @JavascriptInterface
    fun mLog(s: String) {
        val MAX_LEN = 2000 // 2000 bytes 마다 끊어서 출력

        val len = s.length

        if (len > MAX_LEN) {
            var idx = 0
            var nextIdx = 0

            while (idx < len) {
                nextIdx += MAX_LEN
                if (nextIdx > len){
                    Log.e(TAG, "==\n" + s.substring(idx, len))
                } else {
                    Log.e(TAG, "==\n" + s.substring(idx, nextIdx))
                }
                idx = nextIdx
            }
        } else {
            Log.e(TAG, s)
        }
    }

    @JavascriptInterface
    fun getCodeData(): String {

        var output: List<Code?>? = null
        Log.e(TAG, "before : $output")
        output = MyAppDatabase.getInstance(mContext).codeDao()?.getCodes("EN")
        Log.e(TAG, "after : $output")

        return output.toString()
    }
    @JavascriptInterface
    fun addCode(){
        val newCode = Code()

        newCode.apply{
            languageCode = "EN"
            categoryId = "TEST"
            categoryDesc = "1"
            codeValue = "2"
            codeDesc = "3"
            codeShortDesc = "4"
        }

        MyAppDatabase.getInstance(mContext).codeDao()?.addCode(newCode)
        showToast("add finished")
    }
}