/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.projecthub.base.shared.middleware.listener;


class AbstractRepositoryEventListener<T> {

    protected void onBeforeCreate(final T entity) {
        // Custom logic before creating an entity
    }

    protected void onBeforeSave(final T entity) {
        // Custom logic before saving an entity
    }

    protected void onBeforeDelete(final T entity) {
        // Custom logic before deleting an entity
    }

    protected void onAfterCreate(final T entity) {
        // Custom logic after creating an entity
    }

    protected void onAfterSave(final T entity) {
        // Custom logic after saving an entity
    }

    protected void onAfterDelete(final T entity) {
        // Custom logic after deleting an entity
    }

}
