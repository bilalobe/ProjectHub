package com.projecthub.domain

interface Specification<T> {
    fun isSatisfiedBy(candidate: T): Boolean

    fun and(other: Specification<T>): Specification<T> = AndSpecification(this, other)
    fun or(other: Specification<T>): Specification<T> = OrSpecification(this, other)
    fun not(): Specification<T> = NotSpecification(this)
}

private class AndSpecification<T>(
    private val left: Specification<T>,
    private val right: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean =
        left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate)
}

private class OrSpecification<T>(
    private val left: Specification<T>,
    private val right: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean =
        left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate)
}

private class NotSpecification<T>(
    private val specification: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean =
        !specification.isSatisfiedBy(candidate)
}
