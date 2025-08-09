package com.haenaem.hamba.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HambaData(
    val id: Int = 0,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val lunchPrice: Int = 0,
    val dinnerPrice: Int = 0,
    val openTime: String = "",
    val closeTime: String = "",
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val description: String = "",
    val imageUrls: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

enum class PriceRange(val displayName: String, val minPrice: Int, val maxPrice: Int) {
    UNDER_10K("1만원 이하", 0, 10000),
    RANGE_10K_20K("1-2만원", 10000, 20000),
    OVER_20K("2만원 이상", 20000, Int.MAX_VALUE),
    ALL("전체", 0, Int.MAX_VALUE)
}

data class SearchFilter(
    var query: String = "",
    var priceRange: PriceRange = PriceRange.ALL,
    var distance: Int = 5,
    var minRating: Float = 0.0f
)