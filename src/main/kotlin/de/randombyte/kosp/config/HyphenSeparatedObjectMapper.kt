package de.randombyte.kosp.config

import com.google.common.base.CaseFormat
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.Setting

/**
 * Sets the path of a [Setting] to its lowercase-hyphen-separated target field name.
 *
 * Example, both lines of code produce the same config:
 * ```kotlin
 * @Setting("test-number") val testNumber: Int = 42
 * @Setting val testNumber: Int = 42
 * ```
 */
class HyphenSeparatedObjectMapper<T>(clazz: Class<T>?) : ObjectMapper<T>(clazz) {
    override fun collectFields(cachedFields: MutableMap<String, FieldData>, clazz: Class<in T>) {
        clazz.declaredFields
                .filter { it.isAnnotationPresent(Setting::class.java) }
                .forEach { field ->
                    val setting = field.getAnnotation(Setting::class.java)

                    val path = if (setting.value.isEmpty()) {
                        CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.name)
                    } else setting.value

                    val data = FieldData(field, setting.comment)
                    field.isAccessible = true
                    if (!cachedFields.containsKey(path)) cachedFields.put(path, data)
                }
    }
}