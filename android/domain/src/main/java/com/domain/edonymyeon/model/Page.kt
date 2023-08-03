package com.domain.edonymyeon.model

@JvmInline
value class Page(val value: Int = DEFAULT_PAGE) {
    init {
        require(value >= DEFAULT_PAGE) { "page must be positive" }
    }

    fun initPage(): Page {
        return Page(DEFAULT_PAGE)
    }

    fun increasePage(): Page {
        return Page(value + INCREASE_POINT)
    }

    companion object {
        private const val INCREASE_POINT = 1
        private const val DEFAULT_PAGE = 0
    }
}
