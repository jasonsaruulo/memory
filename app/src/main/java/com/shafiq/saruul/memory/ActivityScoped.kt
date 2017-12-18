package com.shafiq.saruul.memory

import javax.inject.Scope

/**
 * In Dagger, an unscoped component cannot depend on a scoped component. As
 * {@link AppComponent} is a scoped component ({@code @Singleton}, we create a custom
 * scope to be used by all fragment components. Additionally, a component with a specific scope
 * cannot have a sub component with the same scope.
 */
@kotlin.annotation.MustBeDocumented
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScoped
