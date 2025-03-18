package com.projecthub.domain

import org.jmolecules.ddd.types.AggregateRoot as JMoleculesAggregateRoot

interface AggregateRoot<T : BaseEntity> : JMoleculesAggregateRoot<T, UUID>
