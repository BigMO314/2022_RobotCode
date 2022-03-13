package frc.robot.period;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

/**
 * The class that handles all robot control while the robot is disabled. 
 * <i>All calls to this class should be done statically</i>
 */
public class Disabled {
	private Disabled() {}

	/**
	 * Update system configuration while disabled
	 */
	public static void init() {
		Manipulator.configArmNeutralMode(NeutralMode.Coast);
		Manipulator.configIntakeNeutralMode(NeutralMode.Coast);
		Drivetrain.configDriveNeutralMode(NeutralMode.Coast);
	}

	/**
	 * Dashboard initialization run once after a connection to NetworkTables has been established
	 */
	public static void initDashboard() {

	}

	/**
	 * Run periodically to push new values to the Dashboard
	 */
	public static void updateDashboard() {

	}

	/**
	 * Run periodically to update subsystems
	 */
	public static void periodic() {

	}
}
