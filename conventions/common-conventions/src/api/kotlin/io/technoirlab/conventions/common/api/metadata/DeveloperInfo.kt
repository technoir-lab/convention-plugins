package io.technoirlab.conventions.common.api.metadata

import java.io.Serializable

data class DeveloperInfo(
    val id: String?,
    val name: String,
    val email: String?,
    val organization: String?,
    val organizationUrl: String?
) : Serializable {

    /**
     * @suppress
     */
    companion object {
        private const val serialVersionUID: Long = -1066512827450854604L
    }
}
