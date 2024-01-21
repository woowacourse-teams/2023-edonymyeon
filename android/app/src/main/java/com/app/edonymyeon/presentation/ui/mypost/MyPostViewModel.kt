package com.app.edonymyeon.presentation.ui.mypost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionUiModel
import com.app.edonymyeon.presentation.uimodel.MyPostUiModel
import com.app.edonymyeon.presentation.util.onFailureWithApiException
import com.domain.edonymyeon.model.Date
import com.domain.edonymyeon.model.MonthRange
import com.domain.edonymyeon.model.Page
import com.domain.edonymyeon.model.Post
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class MyPostViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val postRepository: PostRepository,
    authRepository: AuthRepository,
) : BaseViewModel(authRepository) {

    private var currentPage = Page()
    private var isLastPage = false

    private val _posts = MutableLiveData<List<MyPostUiModel>>()
    val posts: LiveData<List<MyPostUiModel>>
        get() = _posts

    private val _yearMonth = MutableLiveData<HashMap<Int, MutableList<Int>>>()
    val yearMonth: LiveData<HashMap<Int, MutableList<Int>>>
        get() = _yearMonth

    private val _price = MutableLiveData<String>()
    val price: LiveData<String>
        get() = _price

    fun getMyPosts(notificationId: Long) {
        viewModelScope.launch(exceptionHandler) {
            profileRepository.getMyPosts(currentPage.value, notificationId).onSuccess {
                _posts.value = posts.value.orEmpty() + it.posts.map { post ->
                    post.toUiModel()
                }
                currentPage = currentPage.increasePage()
                isLastPage = it.isLast
            }.onFailureWithApiException {
                throw it
            }
        }
    }

    fun clearResult() {
        currentPage = currentPage.initPage()
        _posts.value = emptyList()
    }

    fun hasNextPage(): Boolean {
        return !isLastPage
    }

    fun postPurchaseConfirm(id: Long, purchasePrice: Int, year: Int, month: Int) {
        updateConfirmConsumption(
            id,
            ConsumptionUiModel(
                type = PURCHASE_TYPE,
                purchasePrice = purchasePrice,
                year = year,
                month = month,
            ),
        )
        viewModelScope.launch(exceptionHandler) {
            profileRepository.postPurchaseConfirm(id, purchasePrice, year, month).onSuccess {
            }.onFailureWithApiException {
                throw it
            }
        }
    }

    fun postSavingConfirm(id: Long, year: Int, month: Int) {
        updateConfirmConsumption(
            id,
            ConsumptionUiModel(type = SAVING_TYPE, purchasePrice = 0, year = year, month = month),
        )
        viewModelScope.launch(exceptionHandler) {
            profileRepository.postSavingConfirm(id, year, month).onSuccess {
            }
                .onFailureWithApiException {
                    throw it
                }
        }
    }

    fun deleteConfirm(id: Long) {
        updateConfirmConsumption(
            id,
            ConsumptionUiModel(type = NONE_TYPE, purchasePrice = 0, year = 0, month = 0),
        )
        viewModelScope.launch(exceptionHandler) {
            profileRepository.deleteConfirm(id).onSuccess {
            }
                .onFailureWithApiException {
                    throw it
                }
        }
    }

    fun getYearMonth(id: Long) {
        val post = _posts.value?.find { it.id == id }
        val createdAt = post?.createdAt?.createdAt ?: ""
        val createdYearMonth = getYearMonthFromDate(createdAt)
        val nowYearMonth = getYearMonthFromDate(LocalDateTime.now().toString())
        val yearMonthList = getYearMonthList(createdYearMonth, nowYearMonth)

        _yearMonth.value = createYearMonthMap(yearMonthList)
    }

    fun setPurchasePriceTextChanged(price: CharSequence, start: Int, before: Int, count: Int) {
        _price.value = price.toString()
    }

    fun setPurchasePrice(price: String) {
        _price.value = price
    }

    private fun updateConfirmConsumption(postId: Long, consumption: ConsumptionUiModel) {
        _posts.value = _posts.value?.map { post ->
            if (post.id == postId) {
                post.copy(consumption = consumption)
            } else {
                post
            }
        }
    }

    fun getPostPrice(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            postRepository.getPostDetail(id).onSuccess {
                _price.value = (it as Post).price.toString()
            }.onFailureWithApiException {
                throw it
            }
        }
    }

    private fun getYearMonthFromDate(dateString: String): YearMonth {
        return Date(dateString).yearMonth
    }

    private fun getYearMonthList(start: YearMonth, end: YearMonth): List<String> {
        return MonthRange(start, end).yearMonthList
    }

    private fun createYearMonthMap(yearMonthList: List<String>): HashMap<Int, MutableList<Int>> {
        val map = hashMapOf<Int, MutableList<Int>>()
        yearMonthList.forEach { yearMonth ->
            val (year, month) = yearMonth.split("-").map { it.toInt() }
            map.computeIfAbsent(year) { mutableListOf() }.add(month)
        }
        return map
    }

    companion object {
        private const val PURCHASE_TYPE = "PURCHASE"
        private const val SAVING_TYPE = "SAVING"
        private const val NONE_TYPE = "NONE"
    }
}
