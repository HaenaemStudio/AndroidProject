package com.haenaem.hamba.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haenaem.hamba.R
import com.haenaem.hamba.data.HambaData
import com.haenaem.hamba.repository.HambaRepository

class AddHambaActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnFavorite: TextView

    // 상세 정보 표시용 Views
    private lateinit var tvHambaName: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvOperatingHours: TextView
    private lateinit var tvReview: TextView

    private lateinit var hambaRepository: HambaRepository
    private var currentHamba: HambaData? = null
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hamba_detail)

        hambaRepository = HambaRepository.getInstance(this)

        initializeViews()
        setupClickListeners()
        loadHambaDetail()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back)
        btnFavorite = findViewById(R.id.btn_favorite)
    }

    private fun setupClickListeners() {
        // 뒤로가기 버튼
        btnBack.setOnClickListener {
            finish() // 현재 Activity 종료하고 이전 화면으로 돌아가기
        }

        // 즐겨찾기 버튼
        btnFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun loadHambaDetail() {
        // TODO: Intent로 전달받은 함바 ID를 통해 상세 정보 로드
        val hambaId = intent.getIntExtra("hamba_id", -1)

        if (hambaId != -1) {
            android.util.Log.d("HambaDetailActivity", "함바 ID: $hambaId 의 상세 정보 로드")
            // 실제로는 데이터베이스나 서버에서 정보를 가져와야 함
        } else {
            android.util.Log.d("HambaDetailActivity", "기본 함바 정보 표시")
        }

        // 현재는 레이아웃에 하드코딩된 정보가 표시됨
    }

    private fun toggleFavorite() {
        currentHamba?.let { hamba ->
            val success = hambaRepository.toggleFavorite(hamba.id)
            if (success) {
                isFavorite = !isFavorite
                currentHamba = hamba.copy(isFavorite = isFavorite)
                updateFavoriteButton()

                val message = if (isFavorite) {
                    "즐겨찾기에 추가되었습니다"
                } else {
                    "즐겨찾기에서 제거되었습니다"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                android.util.Log.d("HambaDetailActivity", "즐겨찾기 상태 변경: $isFavorite")
            } else {
                Toast.makeText(this, "즐겨찾기 상태 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteButton() {
        btnFavorite.text = if (isFavorite) "♥ 즐겨찾기" else "♡ 즐겨찾기"
    }
}