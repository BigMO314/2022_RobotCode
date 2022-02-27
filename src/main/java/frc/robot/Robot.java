// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.molib.buttons.ButtonManager;
import frc.molib.utilities.Console;
import frc.robot.period.Autonomous;
import frc.robot.period.Teleoperated;
import frc.robot.period.Test;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Intake;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static final NetworkTable tblMO = NetworkTableInstance.getDefault().getTable("MO Data");
	public static final NetworkTable tblPeriods = tblMO.getSubTable("Control Periods");

	/**
	 * This function is run when the robot is first started up and should be used for any
	 * initialization code.
	 */
	@Override
	public void robotInit() {
		Console.logMsg("Waiting for NetworkTable Connection...");
		while(!NetworkTableInstance.getDefault().isConnected()) {}
		Console.logMsg("NetworkTables connected. Initializing Robot...");

		Test.initDashboard();
		Autonomous.initDashboard();
		Teleoperated.initDashboard();

		Drivetrain.init();
		Intake.init();

		Console.logMsg("Robot Initialized");
	}

	@Override
	public void robotPeriodic() {
		ButtonManager.updateValues();
	}

	@Override
	public void autonomousInit() {
		Autonomous.init();
	}

	@Override
	public void autonomousPeriodic() {
		Autonomous.periodic();
	}

	@Override
	public void teleopInit() {
		Teleoperated.init();
	}

	@Override
	public void teleopPeriodic() {
		Teleoperated.periodic();
	}

	@Override
	public void disabledInit() {}

	@Override
	public void disabledPeriodic() {}

	@Override
	public void testInit() {
		Test.init();
	}

	@Override
	public void testPeriodic() {
		Test.periodic();
	}
}
