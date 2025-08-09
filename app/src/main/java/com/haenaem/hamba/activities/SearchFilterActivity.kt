package com.haenaem.hamba.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haenaem.hamba.R
import com.haenaem.hamba.data.PriceRange
import com.haenaem.hamba.data.SearchFilter
import com.haenaem.hamba.repository.HambaRepository

class SearchFilterActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnReset: TextView
    private lateinit var tabSearch: Button
    private lateinit var tabFilter: Button
    private lateinit var btnSearchResult: Button
    private lateinit var etSearch: EditText

    // 필터 버튼들
    private lateinit var btnPriceUnder10k: Button
    private lateinit var btnPrice10to20k: Button
    private lateinit var btnPriceOver20k: Button
    private lateinit var btnDistance1km: Button
    private lateinit var btnDistance3km: Button
    private lateinit var btnDistance5km: Button
    private lateinit var btnRating4plus: Button
    private lateinit var btnRating3plus: Button
    private lateinit var btnRatingAll: Button

    private lateinit var hambaRepository: HambaRepository
    private var currentFilter = SearchFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_filter)

        hambaRepository = HambaRepository.getInstance(this)

        initializeViews()
        setupClickListeners()
        updateFilterButtons()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back)
        btnReset = findViewById(R.id.btn_reset)
        tabSearch = findViewById(R.id.tab_search)
        tabFilter = findViewById(R.id.tab_filter)
        btnSearchResult = findViewById(R.id.btn_search_result)
        etSearch = findViewById(R.id.et_search)

        // 가격 필터 버튼들은 레이아웃에서 실제 ID를 확인해서 매핑해야 함
        // 현재 레이아웃이 하드코딩되어 있어서 동적으로 찾기 어려움
        // 임시로 로그 출력용으로만 사용
    }

    private fun setupClickListeners() {
        // 뒤로가기 버튼
        btnBack.setOnClickListener {
            finish() // 현재 Activity 종료하고 이전 화면으로 돌아가기
        }

        // 초기화 버튼
        btnReset.setOnClickListener {
            resetFilters()
        }

        // 탭 전환
        tabSearch.setOnClickListener {
            switchToSearchTab()
        }

        tabFilter.setOnClickListener {
            switchToFilterTab()
        }

        // 검색 결과 보기
        btnSearchResult.setOnClickListener {
            performSearch()
        }

        // 검색어 변경 감지
        etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                currentFilter.query = etSearch.text.toString().trim()
                updateSearchResultCount()
            }
        }
    }

    private fun resetFilters() {
        currentFilter = SearchFilter()
        etSearch.setText("")
        updateFilterButtons()
        updateSearchResultCount()
        android.util.Log.d("SearchFilterActivity", "필터 초기화")
    }

    private fun switchToSearchTab() {
        // 검색 탭 활성화
        tabSearch.setBackgroundResource(R.drawable.tab_active)
        tabSearch.setTextColor(resources.getColor(R.color.md_theme_light_primary, null))

        // 필터 탭 비활성화
        tabFilter.setBackgroundResource(R.drawable.tab_inactive)
        tabFilter.setTextColor(resources.getColor(android.R.color.black, null))

        android.util.Log.d("SearchFilterActivity", "검색 탭 선택됨")
    }

    private fun switchToFilterTab() {
        // 필터 탭 활성화
        tabFilter.setBackgroundResource(R.drawable.tab_active)
        tabFilter.setTextColor(resources.getColor(R.color.md_theme_light_primary, null))

        // 검색 탭 비활성화
        tabSearch.setBackgroundResource(R.drawable.tab_inactive)
        tabSearch.setTextColor(resources.getColor(android.R.color.black, null))

        android.util.Log.d("SearchFilterActivity", "필터 탭 선택됨")
    }

    private fun updateFilterButtons() {
        // 현재 레이아웃이 하드코딩되어 있어서 동적으로 버튼 상태 변경이 어려움
        // 실제로는 선택된 필터에 따라 버튼 배경색과 텍스트 색상을 변경해야 함
        android.util.Log.d("SearchFilterActivity", "필터 버튼 상태 업데이트")
        android.util.Log.d("SearchFilterActivity", "현재 가격 필터: ${currentFilter.priceRange}")
        android.util.Log.d("SearchFilterActivity", "현재 거리 필터: ${currentFilter.distance}km")
        android.util.Log.d("SearchFilterActivity", "현재 평점 필터: ${currentFilter.minRating}")
    }

    private fun updateSearchResultCount() {
        try {
            val searchResults = hambaRepository.searchHambas(currentFilter)
            val count = searchResults.size
            btnSearchResult.text = "검색 결과 보기 (${count}개)"

            android.util.Log.d("SearchFilterActivity", "검색 결과 개수: $count")
        } catch (e: Exception) {
            android.util.Log.e("SearchFilterActivity", "검색 결과 개수 업데이트 실패: ${e.message}")
            btnSearchResult.text = "검색 결과 보기"
        }
    }

    private fun performSearch() {
        try {
            currentFilter.query = etSearch.text.toString().trim()
            val searchResults = hambaRepository.searchHambas(currentFilter)

            android.util.Log.d("SearchFilterActivity", "검색 수행됨")
            android.util.Log.d("SearchFilterActivity", "검색어: '${currentFilter.query}'")
            android.util.Log.d("SearchFilterActivity", "결과 개수: ${searchResults.size}")

            searchResults.forEach { hamba ->
                android.util.Log.d("SearchFilterActivity", "- ${hamba.name} (${hamba.address})")
            }

            if (searchResults.isEmpty()) {
                Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "${searchResults.size}개의 함바를 찾았습니다", Toast.LENGTH_SHORT).show()
            }

            // TODO: 검색 결과를 메인 화면으로 전달
            // 현재는 단순히 뒤로가기
            finish()

        } catch (e: Exception) {
            android.util.Log.e("SearchFilterActivity", "검색 실행 실패: ${e.message}")
            Toast.makeText(this, "검색 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateSearchResultCount()
    }
}