package com.app.edonymyeon.presentation.ui.postdetail.listener

import com.app.edonymyeon.presentation.uimodel.CommentUiModel

interface CommentClickListener {
    fun onDeleteComment(commentId: Long)
    fun onReportComment(commentId: Long)
    fun onImageClick(comment: CommentUiModel)
}
