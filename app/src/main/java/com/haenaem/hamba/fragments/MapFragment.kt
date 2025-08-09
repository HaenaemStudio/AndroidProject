package com.haenaem.hamba.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.haenaem.hamba.R
import com.haenaem.hamba.activities.AddHambaActivity
import com.haenaem.hamba.activities.HambaDetailActivity
import com.haenaem.hamba.activities.SearchFilterActivity
import com.haenaem.hamba.data.HambaData
import com.haenaem.hamba.repository.HambaRepository
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null

    private lateinit var searchBar: EditText
    private lateinit var btnFilter: Button
    private lateinit var btnAddHamba: Button

    private lateinit var hambaRepository: HambaRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MapFragment", "🔥 onCreateView 시작!")

        val view = inflater.inflate(R.layout.fragment_map, container, false)

        hambaRepository = HambaRepository.getInstance(requireContext())
        Log.d("MapFragment", "🔥 Repository 초기화 완료")

        addSampleDataIfEmpty()
        Log.d("MapFragment", "🔥 addSampleDataIfEmpty 호출 완료")

        initializeViews(view)
        setupClickListeners()

        mapView = view.findViewById(R.id.map_view)

        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("MapFragment", "지도 종료됨")
            }

            override fun onMapError(exception: Exception) {
                Log.e("MapFragment", "지도 에러: ${exception.message}")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                Log.d("MapFragment", "지도 준비 완료!")
                kakaoMap = map
                addHambaMarkers(map)
            }

            override fun getPosition(): LatLng {
                return LatLng.from(37.5665, 126.9780)
            }

            override fun getZoomLevel(): Int {
                return 15
            }

            override fun getViewName(): String {
                return "HambaMap"
            }

            override fun isVisible(): Boolean {
                return true
            }

            override fun getTag(): String {
                return "HambaMapTag"
            }
        })

        return view
    }

    private fun initializeViews(view: View) {
        searchBar = view.findViewById(R.id.search_bar)
        btnFilter = view.findViewById(R.id.btn_filter)
        btnAddHamba = view.findViewById(R.id.btn_add_hamba)
    }

    private fun setupClickListeners() {
        btnFilter.setOnClickListener {
            startActivity(Intent(requireContext(), SearchFilterActivity::class.java))
        }

        btnAddHamba.setOnClickListener {
            startActivity(Intent(requireContext(), AddHambaActivity::class.java))
        }

        searchBar.setOnClickListener {
            startActivity(Intent(requireContext(), SearchFilterActivity::class.java))
        }
    }

    private fun addSampleDataIfEmpty() {
        Log.d("MapFragment", "🔥 addSampleDataIfEmpty 함수 호출됨!")

        val currentData = hambaRepository.getAllHambas()
        Log.d("MapFragment", "현재 저장된 함바 개수: ${currentData.size}")

        if (currentData.isEmpty()) {
            Log.d("MapFragment", "샘플 데이터 추가 중...")

            val sampleHambas = listOf(
                HambaData(
                    name = "맛있는 한식뷔페",
                    address = "서울시 강남구 역삼동",
                    latitude = 37.5013,
                    longitude = 127.0396,
                    lunchPrice = 15000,
                    dinnerPrice = 18000,
                    description = "반찬 종류가 정말 많아요!"
                ),
                HambaData(
                    name = "행복한 가정식뷔페",
                    address = "서울시 서초구 서초동",
                    latitude = 37.4946,
                    longitude = 127.0206,
                    lunchPrice = 12000,
                    dinnerPrice = 15000,
                    description = "가정식 반찬이 맛있어요!"
                )
            )

            sampleHambas.forEach { hamba ->
                hambaRepository.addHamba(hamba)
                Log.d("MapFragment", "샘플 추가: ${hamba.name}")
            }
        } else {
            Log.d("MapFragment", "이미 데이터가 있음: ${currentData.size}개")
            currentData.forEach { hamba ->
                Log.d("MapFragment", "기존 데이터: ${hamba.name}")
            }
        }
    }

    private fun addHambaMarkers(map: KakaoMap) {
        val hambaList = hambaRepository.getAllHambas()
        Log.d("MapFragment", "마커 추가할 함바 개수: ${hambaList.size}")

        hambaList.forEach { hamba ->
            try {
                val position = LatLng.from(hamba.latitude, hamba.longitude)
                Log.d("MapFragment", "마커 추가: ${hamba.name} at (${hamba.latitude}, ${hamba.longitude})")

                val labelStyle = com.kakao.vectormap.label.LabelStyle.from(R.drawable.ic_launcher_foreground)
                val labelStyles = com.kakao.vectormap.label.LabelStyles.from(labelStyle)
                val labelOptions = com.kakao.vectormap.label.LabelOptions.from(position).setStyles(labelStyles)

                map.labelManager?.layer?.addLabel(labelOptions)

            } catch (e: Exception) {
                Log.e("MapFragment", "마커 추가 실패: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::mapView.isInitialized) {
            mapView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mapView.isInitialized) {
            mapView.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mapView.isInitialized) {
            mapView.finish()
        }
    }
}