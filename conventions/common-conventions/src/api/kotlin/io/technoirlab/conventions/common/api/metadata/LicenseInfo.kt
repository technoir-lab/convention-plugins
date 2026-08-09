package io.technoirlab.conventions.common.api.metadata

import java.io.Serializable

data class LicenseInfo(
    val name: String,
    val url: String,
    val distribution: String?
) : Serializable {

    /**
     * @suppress
     */
    companion object {
        private const val serialVersionUID: Long = 7062271691274790485L
    }
}
