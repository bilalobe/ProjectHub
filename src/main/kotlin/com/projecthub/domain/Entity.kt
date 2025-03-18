package com.projecthub.domain

import org.jmolecules.ddd.types.Entity as JMoleculesEntity

interface Entity<T : BaseEntity> : JMoleculesEntity<T, UUID>
