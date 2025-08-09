package com.haenaem.hamba.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haenaem.hamba.R
import com.haenaem.hamba.data.HambaData
import com.haenaem.hamba.repository.HambaRepository

class HambaDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var btnFavorite: TextView

    // 상세 정보 표시용 Views
    private lateinit var tvHambaName: TextView
    private lateinit var tvRatingStars: TextView
    private lateinit var tvRatingText: TextView
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

        // 상세 정보 표시용 TextView들
        tvHambaName = findViewById(R.id.tv_hamba_name)
        tvRatingStars = findViewById(R.id.tv_rating_stars)
        tvRatingText = findViewById(R.id.tv_rating_text)
        tvAddress = findViewById(R.id.tv_address)
        tvPrice = findViewById(R.id.tv_price)
        tvOperatingHours = findViewById(R.id.tv_operating_hours)
        tvReview = findViewById(R.id.tv_review)
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
        // Intent로 전달받은 함바 정보 또는 ID 확인
        val hambaData = intent.getParcelableExtra<HambaData>("hamba_data")
        val hambaId = intent.getIntExtra("hamba_id", -1)

        currentHamba = when {
            hambaData != null -> hambaData
            hambaId != -1 -> hambaRepository.getHambaById(hambaId)
            else -> null
        }

        currentHamba?.let { hamba ->
            android.util.Log.d("HambaDetailActivity", "함바 정보 로드: ${hamba.name}")
            isFavorite = hamba.isFavorite
            updateFavoriteButton()
            displayHambaInfo(hamba)
        } ?: run {
            android.util.Log.d("HambaDetailActivity", "함바 정보를 찾을 수 없음")
            Toast.makeText(this, "함바 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayHambaInfo(hamba: HambaData) {
        // 함바 이름
        tvHambaName.text = hamba.name

        // 평점 표시
        val starRating = generateStarRating(hamba.rating)
        tvRatingStars.text = starRating
        tvRatingText.text = "${hamba.rating} (${hamba.reviewCount}명 평가)"

        // 주소
        tvAddress.text = hamba.address

        // 가격 정보
        val priceText = if (hamba.lunchPrice > 0 && hamba.dinnerPrice > 0) {
            "점심: ${formatPrice(hamba.lunchPrice)} / 저녁: ${formatPrice(hamba.dinnerPrice)}"
        } else if (hamba.lunchPrice > 0) {
            "점심: ${formatPrice(hamba.lunchPrice)}"
        } else if (hamba.dinnerPrice > 0) {
            "저녁: ${formatPrice(hamba.dinnerPrice)}"
        } else {
            "가격 정보 없음"
        }
        tvPrice.text = priceText

        // 운영시간
        val hoursText = if (hamba.openTime.isNotEmpty() && hamba.closeTime.isNotEmpty()) {
            "평일: ${hamba.openTime} - ${hamba.closeTime}\n주말: ${hamba.openTime} - ${hamba.closeTime}"
        } else {
            "운영시간 정보 없음"
        }
        tvOperatingHours.text = hoursText

        // 리뷰/설명
        tvReview.text = if (hamba.description.isNotEmpty()) {
            hamba.description
        } else {
            "아직 리뷰가 없습니다."
        }

        android.util.Log.d("HambaDetailActivity", "화면에 함바 정보 표시 완료: ${hamba.name}")
    }

    private fun generateStarRating(rating: Float): String {
        val fullStars = rating.toInt()
        val hasHalfStar = (rating - fullStars) >= 0.5f
        val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

        val stars = StringBuilder()
        repeat(fullStars) { stars.append("★") }
        if (hasHalfStar) stars.append("☆")
        repeat(emptyStars) { stars.append("☆") }

        return stars.toString()
    }

    private fun formatPrice(price: Int): String {
        return "${String.format("%,d", price)}원"
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