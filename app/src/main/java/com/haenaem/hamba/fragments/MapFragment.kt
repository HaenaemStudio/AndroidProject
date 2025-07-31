// MapFragment.kt
package com.haenaem.hamba.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.haenaem.hamba.R
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MapFragment", "onCreateView 호출됨")
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.map_view)
        Log.d("MapFragment", "MapView 찾음: ${::mapView.isInitialized}")

        // 카카오 지도 시작
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("MapFragment", "지도 종료됨")
            }

            override fun onMapError(exception: Exception) {
                Log.e("MapFragment", "지도 에러 발생: ${exception.message}")
                exception.printStackTrace()
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                Log.d("MapFragment", "지도 준비 완료!")
                kakaoMap = map
                // 일단 마커는 주석 처리하고 기본 지도만 표시
                // addHambaMarkers(map)
            }

            override fun getPosition(): LatLng {
                Log.d("MapFragment", "getPosition 호출됨")
                return LatLng.from(37.5665, 126.9780)
            }

            override fun getZoomLevel(): Int {
                Log.d("MapFragment", "getZoomLevel 호출됨")
                return 15
            }

            override fun getViewName(): String {
                Log.d("MapFragment", "getViewName 호출됨")
                return "HambaMap"
            }

            override fun isVisible(): Boolean {
                Log.d("MapFragment", "isVisible 호출됨")
                return true
            }

            override fun getTag(): String {
                Log.d("MapFragment", "getTag 호출됨")
                return "HambaMapTag"
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d("MapFragment", "onResume 호출됨")
        if (::mapView.isInitialized) {
            mapView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MapFragment", "onPause 호출됨")
        if (::mapView.isInitialized) {
            mapView.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("MapFragment", "onDestroyView 호출됨")
        if (::mapView.isInitialized) {
            mapView.finish()
        }
    }
}