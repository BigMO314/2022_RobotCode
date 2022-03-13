// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.molib.buttons.ButtonManager;
import frc.molib.utilities.Console;
import frc.robot.period.Autonomous;
import frc.robot.period.Disabled;
import frc.robot.period.Teleoperated;
import frc.robot.period.Test;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

public class Robot extends TimedRobot {
	public static final NetworkTable tblMain = NetworkTableInstance.getDefault().getTable("MO Data");
	public static final NetworkTable tblPeriods = tblMain.getSubTable("Control Periods");
	public static final NetworkTable tblSubsystems = tblMain.getSubTable("Subsystems");

	private static UsbCamera camDrive;

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
		camDrive = CameraServer.startAutomaticCapture("Camera 1", 0);
		camDrive.setFPS(10);
		camDrive.setResolution(160, 100);

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
		Disabled.init();
	}

	@Override
	public void disabledPeriodic() {
		Disabled.periodic();
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
