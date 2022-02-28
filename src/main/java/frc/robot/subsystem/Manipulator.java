package frc.robot.subsystem;

import static frc.robot.Robot.tblSubsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import frc.molib.dashboard.Entry;
import frc.molib.sensors.LimitSwitch;
import frc.molib.utilities.Console;

public class Manipulator {
	private static final class PowerConstants {
		public static final double ARM_RAISE = 0.5;
		public static final double ARM_LOWER = -0.5;

		public static final double INTAKE_ENABLE = 1.0;
		public static final double INTAKE_REVERSE = -1.0;
	}

	private static final WPI_TalonFX mtrArm = new WPI_TalonFX(4);
	private static final WPI_VictorSPX mtrIntake = new WPI_VictorSPX(5);

	private static final LimitSwitch limArm_U = new LimitSwitch(0);
	private static final LimitSwitch limArm_L = new LimitSwitch(1);

	private static final NetworkTable tblManipulator = tblSubsystems.getSubTable("Manipulator");
	private static final Entry<Double> entArm_Power = new Entry<Double>(tblManipulator, "Arm Power");
	private static final Entry<Double> entIntake_Power = new Entry<Double>(tblManipulator, "Intake Power");
	private static final Entry<Boolean> entArm_U_AtLimit = new Entry<Boolean>(tblManipulator, "Arm at Upper Limit");
	private static final Entry<Boolean> entArm_L_AtLimit = new Entry<Boolean>(tblManipulator, "Arm at Lower Limit");

	private static double mArmPower = 0.0;
	private static double mIntakePower = 0.0;

	private Manipulator() {}

	public static void init() {
		Console.logMsg("Manipulator Initializing...");
	
		mtrArm.setInverted(true);
		mtrIntake.setInverted(false);
	}

	/**
	 * Preload dashboard values
	 */
	public static void initDashboard() {
		
	}

	public static double getArmPower() { return mArmPower; }
	public static double getIntakePower() { return mIntakePower; }
	public static boolean isArmAtUpperLimit() { return !limArm_U.get(); }
	public static boolean isArmAtLowerLimit() { return !limArm_L.get(); }

	public static void setArmPower(double power) { mArmPower = power; }
	public static void setIntakePower(double power) { mIntakePower = power; }

	public static void raiseArm() { setArmPower(PowerConstants.ARM_RAISE); }
	public static void disableArm() { setArmPower(0.0); }
	public static void lowerArm() { setArmPower(PowerConstants.ARM_LOWER); }

	public static void enableIntake() { setIntakePower(PowerConstants.INTAKE_ENABLE); }
	public static void disableIntake() { setIntakePower(0.0); }
	public static void reverseIntake() { setIntakePower(PowerConstants.INTAKE_REVERSE); }

	public static void disable() { 
		disableArm();
		disableIntake();
	}

	public static void periodic() {
		if((isArmAtLowerLimit() && mArmPower < 0.0) || (isArmAtUpperLimit() && mArmPower > 0.0)) 
			disableArm();

		mtrArm.set(ControlMode.PercentOutput, mArmPower);
		mtrIntake.set(ControlMode.PercentOutput, mIntakePower);

		entArm_Power.set(getArmPower());
		entIntake_Power.set(getIntakePower());
		entArm_U_AtLimit.set(isArmAtUpperLimit());
		entArm_L_AtLimit.set(isArmAtLowerLimit());
	}
}
