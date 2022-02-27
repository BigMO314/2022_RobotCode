package frc.robot.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import frc.molib.sensors.LimitSwitch;
import frc.molib.utilities.Console;

@SuppressWarnings("unused")
public class Intake {
	private static final WPI_TalonFX mtrArm = new WPI_TalonFX(4);
	private static final WPI_VictorSPX mtrIntake = new WPI_VictorSPX(5);

	private static final LimitSwitch limArm_U = new LimitSwitch(0);
	private static final LimitSwitch limArm_L = new LimitSwitch(1);

	private static double mArmPower = 0.0;
	private static double mIntakePower = 0.0;

	private Intake() {}

	public static void init() {
		mtrArm.setInverted(false);
		mtrIntake.setInverted(false);

		Console.logMsg("Intake Initialized");
	}

	public static double getArmPower() { return mArmPower; }
	public static double getIntakePower() { return mIntakePower; }
	public static boolean isArmAtUpperLimit() { return limArm_U.get(); }
	public static boolean isArmAtLowerLimit() { return limArm_L.get(); }

	public static void setArmPower(double power) { mArmPower = power; }
	public static void setIntakePower(double power) { mIntakePower = power; }

	public static void raiseArm() { setArmPower(1.0); }
	public static void disableArm() { setArmPower(0.0); }
	public static void lowerArm() { setArmPower(-1.0); }

	public static void enableIntake() { setIntakePower(1.0); }
	public static void disableIntake() { setIntakePower(0.0); }
	public static void reverseIntake() { setIntakePower(-1.0); }

	public static void disable() { 
		disableArm();
		disableIntake();
	}

	public static void periodic() {
		if((isArmAtLowerLimit() && mArmPower < 0.0) || (isArmAtUpperLimit() && mArmPower > 0.0)) 
			disableArm();

		mtrArm.set(ControlMode.PercentOutput, mArmPower);
		mtrIntake.set(ControlMode.PercentOutput, mIntakePower);
	}
}
