package com.projecthub.architecture

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ContextConfiguration
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter
import org.springframework.modulith.test.ApplicationModuleTest
import com.projecthub.ModulithApplication
import com.projecthub.architecture.ApplicationModuleVerification

@ApplicationModuleTest
@SpringBootTest
@ContextConfiguration(classes = [ModulithApplication::class])
class ModuleBoundariesTest {

    @Autowired
    private lateinit var modules: ApplicationModules

    @Autowired
    lateinit var verifier: ApplicationModuleVerification

    @Test
    fun `verify module boundaries`() {
        modules.verify()
    }

    @Test
    fun `document module boundaries`() {
        val documenter = Documenter(modules)
        val outputLocation = "build/modulith-docs"

        // Generate documentation in various formats
        documenter
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml()
            .writeModuleCanvases()
            .withOutputFolder(outputLocation)
            .write()
    }

    @Test
    fun `verify domain layer boundaries`() {
        verifier.verifyDependencyRules()
    }

    @Test
    fun `verify application layer does not depend on infrastructure`() {
        val violations = modules.getModuleByName("application")
            .orElseThrow()
            .getDependencies()
            .filter { it.target.name == "infrastructure" }

        assert(violations.isEmpty()) {
            "Application layer should not depend on infrastructure: ${violations.joinToString()}"
        }
    }

    @Test
    fun `no cycles between modules`() {
        verifier.verifyNoCycles()
    }
}
