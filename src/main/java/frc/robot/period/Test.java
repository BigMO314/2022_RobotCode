package frc.robot.period;

import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Intake;

public class Test {
	private Test() {}

	public static void initDashboard() {
		
	}

	public static void init() {
		Console.logMsg("Test Initialized");
	}

	public static void periodic() {
		Drivetrain.periodic();
		Intake.periodic();
	}
}
