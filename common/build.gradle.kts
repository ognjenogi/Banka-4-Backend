plugins {
	`java-library`
	id("io.spring.dependency-management")
	id("banka4.test-conventions")
	id("banka4.code-style-conventions")
}

group = "rs.banka4"
version = "0.0.1-SNAPSHOT"


dependencyManagement {
	imports {
		mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
	}
}

repositories {
	mavenCentral()
}
