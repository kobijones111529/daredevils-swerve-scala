package frc.robot

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler

object Robot : TimedRobot() {
	private lateinit var robotContainer: RobotContainer
	private var scheduledAuto: Command? = null

	override fun robotInit() {
		robotContainer = RobotContainer()
	}

	override fun robotPeriodic() {
		CommandScheduler.getInstance().run()
	}

	override fun autonomousInit() {
		scheduledAuto = robotContainer.auto?.let {
			it.schedule()
			it
		}
	}

	override fun autonomousPeriodic() {}

	override fun teleopInit() {
		scheduledAuto = scheduledAuto?.let {
			it.cancel()
			null
		}
	}

	override fun teleopPeriodic() {}

	override fun testInit() {
		CommandScheduler.getInstance().cancelAll()
	}

	override fun testPeriodic() {}
	override fun simulationPeriodic() {}
	override fun disabledPeriodic() {}
}