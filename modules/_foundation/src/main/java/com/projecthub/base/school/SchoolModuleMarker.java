package com.projecthub.base.school;


import org.springframework.modulith.ApplicationModule;

@ApplicationModule(displayName = "School Management", allowedDependencies = "base")
public class SchoolModuleMarker {
    public SchoolModuleMarker() {
    }
} // ✅ Correct module boundary
