package com.haenaem.hamba.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haenaem.hamba.data.HambaData
import com.haenaem.hamba.data.PriceRange
import com.haenaem.hamba.data.SearchFilter
import kotlin.math.*

class HambaRepository private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: HambaRepository? = null

        fun getInstance(context: Context): HambaRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HambaRepository(context.applicationContext).also { INSTANCE = it }
            }
        }

        private const val PREF_NAME = "hamba_data"
        private const val KEY_HAMBA_LIST = "hamba_list"
        private const val KEY_NEXT_ID = "next_id"
    }

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private var nextId: Int
        get() = sharedPref.getInt(KEY_NEXT_ID, 1)
        set(value) = sharedPref.edit().putInt(KEY_NEXT_ID, value).apply()

    fun getAllHambas(): List<HambaData> {
        val json = sharedPref.getString(KEY_HAMBA_LIST, null)
        return if (json != null) {
            val type = object : TypeToken<List<HambaData>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun addHamba(hamba: HambaData): HambaData {
        val currentList = getAllHambas().toMutableList()
        val newHamba = hamba.copy(id = nextId)
        currentList.add(newHamba)
        saveHambaList(currentList)
        nextId += 1
        return newHamba
    }

    fun getHambaById(id: Int): HambaData? {
        return getAllHambas().find { it.id == id }
    }

    fun toggleFavorite(hambaId: Int): Boolean {
        val hamba = getHambaById(hambaId) ?: return false
        val updatedHamba = hamba.copy(isFavorite = !hamba.isFavorite)
        val currentList = getAllHambas().toMutableList()
        val index = currentList.indexOfFirst { it.id == hambaId }
        return if (index != -1) {
            currentList[index] = updatedHamba
            saveHambaList(currentList)
            true
        } else {
            false
        }
    }

    fun searchHambas(filter: SearchFilter): List<HambaData> {
        var results = getAllHambas()

        if (filter.query.isNotEmpty()) {
            results = results.filter { hamba ->
                hamba.name.contains(filter.query, ignoreCase = true) ||
                        hamba.address.contains(filter.query, ignoreCase = true) ||
                        hamba.description.contains(filter.query, ignoreCase = true)
            }
        }

        if (filter.priceRange != PriceRange.ALL) {
            results = results.filter { hamba ->
                val avgPrice = (hamba.lunchPrice + hamba.dinnerPrice) / 2
                avgPrice >= filter.priceRange.minPrice && avgPrice <= filter.priceRange.maxPrice
            }
        }

        if (filter.minRating > 0) {
            results = results.filter { it.rating >= filter.minRating }
        }

        return results
    }

    private fun saveHambaList(hambaList: List<HambaData>) {
        val json = gson.toJson(hambaList)
        sharedPref.edit().putString(KEY_HAMBA_LIST, json).apply()
    }
}