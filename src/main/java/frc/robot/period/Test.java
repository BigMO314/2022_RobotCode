package frc.robot.period;

import static frc.robot.Robot.tblPeriods;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTable;
import frc.molib.dashboard.Entry;
import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

/**
 * The class that handles all robot control during Test mode.
 * <i>All calls to this class should be done statically</i>
 */
public class Test {
	private static final NetworkTable tblTest = tblPeriods.getSubTable("Test");
	
	private static final Entry<Boolean> entArm_Enabled = new Entry<Boolean>(tblTest, "Arm Enabled");
	private static final Entry<Boolean> entIntake_Enabled = new Entry<Boolean>(tblTest, "Intake Enabled");
	private static final Entry<Double> entArm_Power = new Entry<Double>(tblTest, "Arm Power");
	private static final Entry<Double> entIntake_Power = new Entry<Double>(tblTest, "Intake Power");

	private Test() {}

	/**
	 * Get ready for the Test Period.
	 * Collect any data from the dashboard, initialize any last minute systems, etc.
	 */
	public static void init() {
		Console.logMsg("Test Period Initializing...");

		//Disable Arm and Intake on Test Period start
		entArm_Enabled.set(false);
		entIntake_Enabled.set(false);
		
		//Configure Brake/Coast mode for all subsystems
		Drivetrain.configDriveNeutralMode(NeutralMode.Brake);
		Manipulator.configArmNeutralMode(NeutralMode.Brake);
		Manipulator.configIntakeNeutralMode(NeutralMode.Coast);

		//Disable all subsystems to start
		Manipulator.disable();
		Drivetrain.disable();
	}

	/**
	 * Dashboard initialization run once after a connection to NetworkTables has been established
	 */
	public static void initDashboard() {
		entArm_Enabled.set(false);
		entIntake_Enabled.set(false);
		entArm_Power.set(0.0);
		entIntake_Power.set(0.0);
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
		//Arm Controls
		if(entArm_Enabled.get())
			Manipulator.setArmPower(entArm_Power.get());
		else
			Manipulator.disableArm();

		//Intake Controls
		if(entIntake_Enabled.get())
			Manipulator.setIntakePower(entIntake_Power.get());
		else	
			Manipulator.disableIntake();

		//Subsystem Updates
		Drivetrain.periodic();
		Manipulator.periodic();
	}
}
