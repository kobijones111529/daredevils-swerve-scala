package frc.robot

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.{Command, CommandScheduler}

object Robot extends TimedRobot:
	private lazy val robotContainer = RobotContainer()
	private var scheduledAuto: Option[Command] = None

	override def robotInit(): Unit =
		robotContainer // Force evaluation

	override def robotPeriodic(): Unit =
		CommandScheduler.getInstance().run()

	override def autonomousInit(): Unit =
		scheduledAuto = robotContainer.auto.map(auto =>
			auto.schedule()
			auto
		)

	override def autonomousPeriodic(): Unit = ()

	override def teleopInit(): Unit =
		scheduledAuto = scheduledAuto.flatMap(auto =>
			auto.cancel()
			None
		)

	override def teleopPeriodic(): Unit = ()

	override def testInit(): Unit =
		CommandScheduler.getInstance().cancelAll()

	override def testPeriodic(): Unit = ()

	override def disabledPeriodic(): Unit = ()

	override def simulationPeriodic(): Unit = ()
