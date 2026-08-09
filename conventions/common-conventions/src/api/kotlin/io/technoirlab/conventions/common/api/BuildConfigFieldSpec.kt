package io.technoirlab.conventions.common.api

import java.io.Serializable

data class BuildConfigFieldSpec<T : Serializable>(
    val name: String,
    val type: Class<T>,
    val value: T?,
    val variant: String?
) : Serializable {

    /**
     * @suppress
     */
    companion object {
        private const val serialVersionUID: Long = 2151125696433872655L
    }
}
