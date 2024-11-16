package com.projecthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.model.Class;
import com.projecthub.service.ClassService;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    private final ClassService classService;

    @Autowired
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public List<Class> getAllClasses() {
        return classService.getAllClasses();
    }

    @GetMapping("/school/{schoolId}")
    public List<Class> getClassesBySchoolId(@PathVariable Long schoolId) {
        return classService.getClassesBySchoolId(schoolId);
    }

    @GetMapping("/{id}")
    public Class getClassById(@PathVariable Long id) {
        return classService.getClassById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));
    }

    @PostMapping
    public Class createClass(@RequestBody Class Class) {
        return classService.saveClass(Class);
    }

    @PutMapping("/{id}")
    public Class updateClass(@PathVariable Long id, @RequestBody Class Class) {
        Class.setId(id);
        return classService.saveClass(Class);
    }

    @DeleteMapping("/{id}")
    public void deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
    }
}