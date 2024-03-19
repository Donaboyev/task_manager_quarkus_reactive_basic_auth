package com.abbosidev.infrastructure.util

import io.smallrye.mutiny.Uni

fun <T> Uni<T>.ifNullFailWith(supplier: () -> Throwable): Uni<T> = this.onItem().ifNull().failWith(supplier)