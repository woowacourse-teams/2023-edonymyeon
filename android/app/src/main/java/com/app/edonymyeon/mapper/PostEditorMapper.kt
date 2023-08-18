package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.request.PostEditorRequest
import com.domain.edonymyeon.model.PostEditor

fun PostEditor.toDataModel(): PostEditorRequest {
    return PostEditorRequest(
        title = this.title,
        content = this.content,
        price = this.price,
    )
}
