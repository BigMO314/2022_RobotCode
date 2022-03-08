// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.molib.buttons.ButtonManager;
import frc.molib.utilities.Console;
import frc.robot.period.Autonomous;
import frc.robot.period.Teleoperated;
import frc.robot.period.Test;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static final NetworkTable tblMain = NetworkTableInstance.getDefault().getTable("MO Data");
	public static final NetworkTable tblPeriods = tblMain.getSubTable("Control Periods");
	public static final NetworkTable tblSubsystems = tblMain.getSubTable("Subsystems");

	private static UsbCamera cam1;

	/**
	 * This function is run when the robot is first started up and should be used for any
	 * initialization code.
	 */
	@Override
	public void robotInit() {
		Console.logMsg("*****Robot Initialization Starting*****");
		Console.logMsg("Establishing NetworkTable Connection...");
		while(!NetworkTableInstance.getDefault().isConnected()) {}
		NetworkTableInstance.getDefault().deleteAllEntries();

		Console.logMsg("*****Initializing Subsystems***********");
		Drivetrain.init();
		Manipulator.init();

		Console.logMsg("*****Initializing Dashboard************");
		Autonomous.initDashboard();
		Teleoperated.initDashboard();
		Test.initDashboard();

		Drivetrain.initDashboard();
		Manipulator.initDashboard();

		Console.logMsg("*****Initializing Cameras**************");
		cam1 = CameraServer.startAutomaticCapture("Camera 1", 0);
		cam1.setFPS(15);
		cam1.setResolution(160, 100);

		Console.logMsg("*****Robot Initialization Complete*****");
	}

	@Override
	public void robotPeriodic() {
		ButtonManager.updateValues();

		Autonomous.updateDashboard();
		Teleoperated.updateDashboard();
		Test.updateDashboard();
		
		Drivetrain.updateDashboard();
		Manipulator.updateDashboard();
	}

	@Override
	public void disabledInit() {
		Manipulator.configArmNeutralMode(NeutralMode.Coast);
	}

	@Override
	public void disabledPeriodic() {

	}

	@Override
	public void testInit() {
		Test.init();
	}

	@Override
	public void testPeriodic() {
		Test.periodic();
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
}
