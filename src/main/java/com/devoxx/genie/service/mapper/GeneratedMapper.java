package com.devoxx.genie.service.mapper;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>Annotation to put on Mapstruct mappers for generated classes to keep the annotation.</p>
 *
 * @see <a href="https://github.com/mapstruct/mapstruct/issues/1528">Improvement: @Generated annotation to mark code that should be ignored by JaCoCo</a>
 * @see <a href="https://github.com/mapstruct/mapstruct/issues/1574>Add annotations to Generated code</a>
 */
@Retention(CLASS)
public @interface GeneratedMapper {
}
