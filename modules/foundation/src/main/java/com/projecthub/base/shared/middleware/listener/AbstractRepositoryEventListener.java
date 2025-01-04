/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.projecthub.base.shared.middleware.listener;


class AbstractRepositoryEventListener<T> {

    protected void onBeforeCreate(T entity) {
        // Custom logic before creating an entity
    }

    protected void onBeforeSave(T entity) {
        // Custom logic before saving an entity
    }

    protected void onBeforeDelete(T entity) {
        // Custom logic before deleting an entity
    }

    protected void onAfterCreate(T entity) {
        // Custom logic after creating an entity
    }

    protected void onAfterSave(T entity) {
        // Custom logic after saving an entity
    }

    protected void onAfterDelete(T entity) {
        // Custom logic after deleting an entity
    }

}
