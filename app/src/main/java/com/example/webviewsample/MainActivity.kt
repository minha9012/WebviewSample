package com.example.webviewsample

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewAssetLoader.ResourcesPathHandler
import com.example.webviewsample.database.Code
import com.example.webviewsample.database.MyAppDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var backKeyPressedTime: Long = 0 // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private var toast: Toast? = null // 첫 번째 뒤로가기 버튼을 누를때 표시

    var filePathCallbackNormal: ValueCallback<Uri>? = null
    var filePathCallbackLollipop: ValueCallback<Array<Uri?>?>? = null
    private val FILECHOOSER_NORMAL_REQ_CODE = 2001
    private val FILECHOOSER_LOLLIPOP_REQ_CODE = 2002
    private var cameraImageUri: Uri? = null

    companion object {
        val TAG = MainActivity::class.java.simpleName.trim()
//        var loadUrl: String = "http://192.168.0.117:8080/skylark-frm"
        var loadUrl: String = "file:///android_asset/www/index.html"
//        var loadUrl: String = "http://appassets.androidplatform.net/assets/html/index.html"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkVerify() //check Permissions
        setDatabase() //set Database
        setWebView() //WebView Setting
//        setBtnEvent() //set Button Listener Event

    }

    private fun setDatabase() {
//        MyAppDatabase.getInstance(this@MainActivity) //create Database Instance
        Log.e(TAG, "Database build complete")
    }

//    private fun setBtnEvent() {
//        //뒤로가기 버튼
//        btn_previous.setOnClickListener {
//            val canGoBack: Boolean = web_view.canGoBack()
//            if(canGoBack) {
//                web_view.goBack()
//            } else {
//                showToast("뒤로 갈 페이지가 없습니다.")
//            }
//        }
//
//        //앞으로가기 버튼
//        btn_next.setOnClickListener {
//            val canGoForward: Boolean = web_view.canGoForward()
//            if(canGoForward) {
//                web_view.goForward()
//            } else {
//                showToast("앞으로 갈 페이지가 없습니다.")
//            }
//        }
//    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun setWebView() {

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(this))
            .addPathHandler("/res/", ResourcesPathHandler(this))
            .build()

        web_view.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        web_view.webChromeClient = WebChromeClientClass()

        //WebView Setting
        web_view.apply {

            //apply JS Interface
            addJavascriptInterface(WebAppInterface(this@MainActivity), "Android")
//            addJavascriptInterface(this@MainActivity, "Android")

            settings.javaScriptEnabled = true //admit JS
            settings.setSupportMultipleWindows(true) //새창띄우기 허용여부
            settings.javaScriptCanOpenWindowsAutomatically = true //자바스크립트 새창띄우기(멀티뷰)
            settings.loadWithOverviewMode = true //메타태크 허용여부
            settings.useWideViewPort = true // 화면사이즈 맞추기 허용여부
            settings.setSupportZoom(true) //화면 줌허용 여부
            settings.builtInZoomControls = true //화면 확대 축소 허용 여부

            settings.cacheMode = WebSettings.LOAD_NO_CACHE //브라우저 캐시허용여부 //WebSetting.LOAD_DEFAULT
            settings.domStorageEnabled = true //로컬 저장소 허용여부
            settings.displayZoomControls = true
            settings.allowContentAccess = true
            settings.setGeolocationEnabled(true) //geolocation API 사용여부
            settings.allowFileAccess = true //File Access

            settings.databaseEnabled = true //Database 사용여부
//            settings.databasePath

            settings.mediaPlaybackRequiresUserGesture = false



            //TODO deprecated 된 코드, WebViewAssetLoader 적용 어떻게 하냐,,,
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                settings.safeBrowsingEnabled = true //api 26

            fitsSystemWindows = true

        }

        web_view.loadUrl(loadUrl) //invoke URL


    }

    //html 소스 가져오기
    fun getHtmlSource() {
        web_view.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);")
            } // <html></html> 사이에 있는 html 소스를 넘겨준다. } }
        }
    }

    //뒤로가기 버튼 클릭 시
    override fun onBackPressed() {

        if (web_view.canGoBack()) {
            web_view.goBack()
            Log.e(TAG, "WebView Back")
        } else {
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
            // 2000 milliseconds = 2 seconds
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis()
                toast = Toast.makeText(this, "한번 더 클릭하면 종료됩니다.", Toast.LENGTH_SHORT)
                toast?.show()
                return
            }

            // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
            // 현재 표시된 Toast 취소
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                finish()
                toast?.cancel()
            }
        }
    }

    //권한 획득 여부 확인
    @TargetApi(Build.VERSION_CODES.M)
    fun checkVerify() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
//            Log.d("checkVerify() : ","if문 들어옴");

            //카메라 또는 저장공간 권한 획득 여부 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                Toast.makeText(
                    applicationContext,
                    "권한 관련 요청을 허용해 주셔야 카메라 캡처이미지 사용등의 서비스를 이용가능합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//                Log.d("checkVerify() : ","카메라 및 저장공간 권한 요청");
                // 카메라 및 저장공간 권한 요청
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 1
                )
            }
        }
    }

    //권한 획득 여부에 따른 결과 반환
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Log.d("onRequestPermissionsResult() : ","들어옴");
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()) {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // 카메라, 저장소 중 하나라도 거부한다면 앱실행 불가 메세지 띄움
                        AlertDialog.Builder(this).setTitle("알림")
                            .setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                            .setPositiveButton(
                                "종료"
                            ) { dialog, _ ->
                                dialog.dismiss()
                                finish()
                            }.setNegativeButton(
                                "권한 설정"
                            ) { dialog, _ ->
                                dialog.dismiss()
                                val intent: Intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + applicationContext.packageName))
                                applicationContext.startActivity(intent)
                            }.setCancelable(false).show()
                        return
                    }
                }
                //                Toast.makeText(this, "Succeed Read/Write external storage !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //액티비티가 종료될 때 결과를 받고 파일을 전송할 때 사용
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var data = data
        Log.d(TAG, " onActivityResult resultCode = $requestCode")
        when (requestCode) {
            FILECHOOSER_NORMAL_REQ_CODE -> if (resultCode == RESULT_OK) {
                if (filePathCallbackNormal == null) return
                val result = if (data == null || resultCode != RESULT_OK) null else data.data
                //  onReceiveValue 로 파일을 전송한다.
                filePathCallbackNormal?.onReceiveValue(result)
                filePathCallbackNormal = null
            }
            FILECHOOSER_LOLLIPOP_REQ_CODE -> {
                Log.d(
                    "onActivityResult() ",
                    "FILECHOOSER_LOLLIPOP_REQ_CODE = $FILECHOOSER_LOLLIPOP_REQ_CODE"
                )
                if (resultCode == RESULT_OK) {
                    Log.d(
                        "onActivityResult() ",
                        "FILECHOOSER_LOLLIPOP_REQ_CODE 의 if문  RESULT_OK 안에 들어옴"
                    )
                    if (filePathCallbackLollipop == null) return
                    if (data == null) data = Intent()
                    if (data.data == null) data.data = cameraImageUri
                    filePathCallbackLollipop?.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(
                            resultCode,
                            data
                        )
                    )
                    filePathCallbackLollipop = null
                } else {
                    Log.d("onActivityResult() ", "FILECHOOSER_LOLLIPOP_REQ_CODE 의 if문의 else문 안으로~")
                    if (filePathCallbackLollipop != null) {   //  resultCode에 RESULT_OK가 들어오지 않으면 null 처리하지 한다.(이렇게 하지 않으면 다음부터 input 태그를 클릭해도 반응하지 않음)
                        Log.d(
                            "onActivityResult() ",
                            "FILECHOOSER_LOLLIPOP_REQ_CODE 의 if문의 filePathCallbackLollipop이 null이 아니면"
                        )
                        filePathCallbackLollipop?.onReceiveValue(null)
                        filePathCallbackLollipop = null
                    }
                    if (filePathCallbackNormal != null) {
                        filePathCallbackNormal?.onReceiveValue(null)
                        filePathCallbackNormal = null
                    }
                }
            }
            else -> {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // 카메라 기능 구현
    @JavascriptInterface
    fun runCamera(_isCapture: Boolean) {
        Log.e(TAG, "Start runCamera!")

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //intentCamera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //File path = getFilesDir();
        val path: File = Environment.getExternalStorageDirectory()
        val file = File(path, "sample.png") // sample.png 는 카메라로 찍었을 때 저장될 파일명이므로 사용자 마음대로
        // File 객체의 URI 를 얻는다.
        cameraImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val strpa = applicationContext.packageName
            FileProvider.getUriForFile(this, "$strpa.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        if (!_isCapture) { // 선택팝업 카메라, 갤러리 둘다 띄우고 싶을 때
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            pickIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val pickTitle = "사진 가져올 방법을 선택하세요."
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)

            // 카메라 intent 포함시키기..
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(intentCamera))
            startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE)
        } else { // 바로 카메라 실행..
            startActivityForResult(intentCamera, FILECHOOSER_LOLLIPOP_REQ_CODE)
        }
    }

    inner class WebChromeClientClass : WebChromeClient() {
        // 자바스크립트의 alert창
        override fun onJsAlert(
            view: WebView,
            url: String?,
            message: String?,
            result: JsResult
        ): Boolean {
            AlertDialog.Builder(view.context)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ -> result.confirm() }
                .setCancelable(false)
                .create()
                .show()
            return true
        }

        // 자바스크립트의 confirm창
        override fun onJsConfirm(
            view: WebView, url: String?, message: String?,
            result: JsResult
        ): Boolean {
            AlertDialog.Builder(view.context)
                .setTitle("Confirm")
                .setMessage(message)
                .setPositiveButton(
                    "Yes"
                ) { _, _ -> result.confirm() }
                .setNegativeButton(
                    "No"
                ) { _, _ -> result.cancel() }
                .setCancelable(false)
                .create()
                .show()
            return true
        }

        // For Android 5.0+ 카메라 - input type="file" 태그를 선택했을 때 반응
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(
            webView: WebView?, filePathCallback: ValueCallback<Array<Uri?>?>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            Log.d(TAG, "5.0+")

            // Callback 초기화 (중요!)
            if (filePathCallbackLollipop != null) {
                filePathCallbackLollipop?.onReceiveValue(null)
                filePathCallbackLollipop = null
            }
            filePathCallbackLollipop = filePathCallback
            val isCapture = fileChooserParams.isCaptureEnabled
            Log.d("onShowFileChooser : ", isCapture.toString())
            runCamera(isCapture)
            return true
        }

        //새 창이 뜨지 않게 하기 위해서
        inner class WebViewClientClass : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                //Log.d("WebViewClient URL : " , request.getUrl().toString());
                view.loadUrl(request.url.toString())
                return true
                //return super.shouldOverrideUrlLoading(view, request);
            }
        }
    }

}