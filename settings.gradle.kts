pluginManagement {
	repositories {
		mavenLocal()
		gradlePluginPortal()
		val frcYear = "2023"
		val frcHome =
			if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
				val publicFolder = System.getenv("PUBLIC") ?: "C:\\Users\\Public"
				val homeRoot = File(publicFolder, "wpilib")
				File(homeRoot, frcYear)
			} else {
				val userFolder = System.getProperty("user.home")
				val homeRoot = File(userFolder, "wpilib")
				File(homeRoot, frcYear)
			}
		val frcHomeMaven = File(frcHome, "maven")
		maven {
			name = "frcHome"
			url = uri(frcHomeMaven)
		}
	}
}
