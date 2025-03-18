package com.projecthub.domain

abstract class ValueObject {
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
    abstract override fun toString(): String
}
