// GlobalApplication.kt
package com.haenaem.hamba
import android.app.Application
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk
import java.security.MessageDigest

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("GlobalApplication", "Application 시작")
        Log.d("GlobalApplication", "패키지명: $packageName")

        // 키 해시 확인 (디버그용)
        getKeyHash()

        // 카카오 SDK 초기화
        try {
            val appKey = "ef624904779a6cd5d1d6cadf9c41f47d"
            Log.d("GlobalApplication", "사용 중인 앱 키: $appKey")

            KakaoSdk.init(this, appKey)
            Log.d("GlobalApplication", "카카오 SDK 초기화 성공")
        } catch (e: Exception) {
            Log.e("GlobalApplication", "카카오 SDK 초기화 실패: ${e.message}")
        }

        // 카카오 지도 SDK 초기화
        try {
            KakaoMapSdk.init(this, "ef624904779a6cd5d1d6cadf9c41f47d")
            Log.d("GlobalApplication", "카카오 지도 SDK 초기화 성공")
        } catch (e: Exception) {
            Log.e("GlobalApplication", "카카오 지도 SDK 초기화 실패: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun getKeyHash() {
        try {
            val info = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    info.signingInfo?.apkContentsSigners
                } else {
                    @Suppress("DEPRECATION")
                    info.signatures
                }

            signatures?.forEach { signature ->
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("GlobalApplication", "키 해시: $keyHash")
            }
        } catch (e: Exception) {
            Log.e("GlobalApplication", "키 해시 가져오기 실패", e)
        }
    }
}