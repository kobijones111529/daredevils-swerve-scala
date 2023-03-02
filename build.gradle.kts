import edu.wpi.first.deployutils.deploy.artifact.FileTreeArtifact
import edu.wpi.first.gradlerio.deploy.roborio.FRCJavaArtifact
import edu.wpi.first.gradlerio.deploy.roborio.RoboRIO

plugins {
	scala
	id("edu.wpi.first.GradleRIO") version "2023.4.2"
}

val robotMainClass = "frc.robot.main"

// Define my targets (RoboRIO) and artifacts (deployable files)
// This is added by GradleRIO's backing project DeployUtils.
deploy {
	targets.register("roborio", RoboRIO::class) {
		// Team number is loaded either from the .wpilib/wpilib_preferences.json
		// or from command line. If not found an exception will be thrown.
		// You can use getTeamOrDefault(team) instead of getTeamNumber if you
		// want to store a team number in this file.
		team = project.frc.teamNumber
		debug.set(project.frc.getDebugOrDefault(false))

		artifacts.register("frcJava", FRCJavaArtifact::class) { }
		artifacts.register("frcStaticFileDeploy", FileTreeArtifact::class) {
			files.set(project.fileTree("src/main/deploy"))
			directory.set("/home/lvuser/deploy")
		}
	}
}

val deployArtifact = deploy.targets["roborio"].artifacts["frcJava"] as FRCJavaArtifact

wpi.java.debugJni.set(false)

val includeDesktopSupport = true

dependencies {
	implementation("org.scala-lang:scala3-library_3:3.2.2")

	wpi.java.deps.wpilib().forEach { implementation(it) }
	wpi.java.vendor.java().forEach { implementation(it) }

	wpi.java.deps.wpilibJniDebug(edu.wpi.first.toolchain.NativePlatforms.roborio).forEach {
		add("roborioDebug", it)
	}
	wpi.java.vendor.jniDebug(edu.wpi.first.toolchain.NativePlatforms.roborio).forEach {
		add("roborioDebug", it)
	}

	wpi.java.deps.wpilibJniRelease(edu.wpi.first.toolchain.NativePlatforms.roborio).forEach {
		add("roborioRelease", it)
	}
	wpi.java.vendor.jniRelease(edu.wpi.first.toolchain.NativePlatforms.roborio).forEach {
		add("roborioRelease", it)
	}

	wpi.java.deps.wpilibJniDebug(edu.wpi.first.toolchain.NativePlatforms.desktop).forEach { nativeDebug(it) }
	wpi.java.vendor.jniDebug(edu.wpi.first.toolchain.NativePlatforms.desktop).forEach { nativeDebug(it) }
	wpi.sim.enableDebug().forEach { simulationDebug(it) }

	wpi.java.deps.wpilibJniRelease(edu.wpi.first.toolchain.NativePlatforms.desktop).forEach { nativeRelease(it) }
	wpi.java.vendor.jniRelease(edu.wpi.first.toolchain.NativePlatforms.desktop).forEach { nativeRelease(it) }
	wpi.sim.enableRelease().forEach { simulationRelease(it) }

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
	useJUnitPlatform()
	systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
}

// Simulation configuration (e.g. environment variables).
wpi.sim.addGui().defaultEnabled.set(true)
wpi.sim.addDriverstation()

// Setting up my Jar File. In this case, adding all libraries into the main jar ('fat jar')
// in order to make them all available at runtime. Also adding the manifest so WPILib
// knows where to look for our Robot Class.
tasks.jar {
	from({ configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) } })
	manifest(edu.wpi.first.gradlerio.GradleRIOPlugin.javaManifest(robotMainClass))
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// Configure jar and deploy tasks
deployArtifact.setJarTask(tasks.jar)
wpi.java.configureExecutableTasks(tasks.jar.get())
wpi.java.configureTestTasks(tasks.test.get())

// Configure string concat to always inline compile
tasks.withType<JavaCompile> {
	options.compilerArgs.add("-XDstringConcat=inline")
}
