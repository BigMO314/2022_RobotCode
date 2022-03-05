package frc.robot.period;

import static frc.robot.Robot.tblPeriods;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTable;
import frc.molib.dashboard.Entry;
import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

public class Test {
	private static final NetworkTable tblTest = tblPeriods.getSubTable("Test");
	
	private static final Entry<Boolean> entArm_Enabled = new Entry<Boolean>(tblTest, "Arm Enabled");
	private static final Entry<Boolean> entIntake_Enabled = new Entry<Boolean>(tblTest, "Intake Enabled");
	private static final Entry<Double> entArm_Power = new Entry<Double>(tblTest, "Arm Power");
	private static final Entry<Double> entIntake_Power = new Entry<Double>(tblTest, "Intake Power");

	private Test() {}

	public static void init() {
		Console.logMsg("Test Period Initializing...");

		entArm_Enabled.set(false);
		entIntake_Enabled.set(false);

		Manipulator.init();
		Drivetrain.init();

		Manipulator.disable();
		Drivetrain.disable();
		
		Manipulator.configArmNeutralMode(NeutralMode.Brake);
	}

	/**
	 * Preload dashboard values
	 */
	public static void initDashboard() {
		entArm_Enabled.set(false);
		entIntake_Enabled.set(false);
		entArm_Power.set(0.0);
		entIntake_Power.set(0.0);
	}

	public static void periodic() {
		if(entArm_Enabled.get())
			Manipulator.setArmPower(entArm_Power.get());
		else
			Manipulator.disableArm();

		if(entIntake_Enabled.get())
			Manipulator.setIntakePower(entIntake_Power.get());
		else	
			Manipulator.disableIntake();


		Drivetrain.periodic();
		Manipulator.periodic();
	}
}
