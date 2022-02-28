package frc.robot.period;

import static frc.robot.Robot.tblPeriods;

import edu.wpi.first.networktables.NetworkTable;
import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

@SuppressWarnings("unused")
public class Test {
	private static final NetworkTable tblTest = tblPeriods.getSubTable("Test");

	private Test() {}

	public static void init() {
		Console.logMsg("Test Period Initializing...");
	}

	/**
	 * Preload dashboard values
	 */
	public static void initDashboard() {
		
	}

	public static void periodic() {
		Drivetrain.periodic();
		Manipulator.periodic();
	}
}
