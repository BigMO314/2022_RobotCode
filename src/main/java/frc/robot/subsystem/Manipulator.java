package frc.robot.subsystem;

import static frc.robot.Robot.tblSubsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.molib.dashboard.Entry;
import frc.molib.utilities.Console;

public class Manipulator {
	private static final class PowerConstants {
		public static final double ARM_RAISE = 0.75;
		public static final double ARM_LOWER = -0.75;

		public static final double INTAKE_ENABLE = 1.0;
		public static final double INTAKE_REVERSE = -1.0;
	}

	private static final WPI_TalonFX mtrArm = new WPI_TalonFX(4);
	private static final WPI_VictorSPX mtrIntake = new WPI_VictorSPX(5);

	private static final DigitalInput limArm_L = new DigitalInput(0);
	private static final DigitalInput limArm_U1 = new DigitalInput(1);
	private static final DigitalInput limArm_U2 = new DigitalInput(2);

	private static final DigitalInput phoArm_L = new DigitalInput(3);
	private static final DigitalInput phoArm_U = new DigitalInput(4);

	private static final NetworkTable tblManipulator = tblSubsystems.getSubTable("Manipulator");
	private static final Entry<Double> entArm_Power = new Entry<Double>(tblManipulator, "Arm Power");
	private static final Entry<Double> entIntake_Power = new Entry<Double>(tblManipulator, "Intake Power");
	private static final Entry<Boolean> entArm_U_AtLimit = new Entry<Boolean>(tblManipulator, "Arm at Upper Limit");
	private static final Entry<Boolean> entArm_L_AtLimit = new Entry<Boolean>(tblManipulator, "Arm at Lower Limit");
	private static final Entry<Double> entArm_Position = new Entry<Double>(tblManipulator, "Arm Position");

	private static final Entry<Double> entArm_Current = new Entry<Double>(tblManipulator, "Arm Current");
	private static final Entry<Double> entArm_MaxCurrent = new Entry<Double>(tblManipulator, "Arm Max Current");

	private static double mArmMaxCurrent = 0.0;

	private static double mArmPower = 0.0;
	private static double mIntakePower = 0.0;

	private Manipulator() {}

////Initialization

	/**
	 * Initialization code run when the robot is started
	 */
	public static void init() {
		Console.logMsg("Manipulator Initializing...");
	
		mtrArm.setInverted(true);
		mtrIntake.setInverted(true);

		mtrArm.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

		configArmNeutralMode(NeutralMode.Coast);

		mtrArm.configForwardSoftLimitEnable(true);
		mtrArm.configReverseSoftLimitEnable(true);
		mtrArm.configForwardSoftLimitThreshold(10000.0);
		mtrArm.configReverseSoftLimitThreshold(-300000.0);

		//mtrArm.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(true, 10.0, 15.0, 0.125));
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
		entArm_Power.set(getArmPower());
		entIntake_Power.set(getIntakePower());
		entArm_U_AtLimit.set(isArmAtUpperLimit());
		entArm_L_AtLimit.set(isArmAtLowerLimit());
		entArm_Position.set(getArmPosition());

		if(mtrArm.getStatorCurrent() > mArmMaxCurrent) mArmMaxCurrent = mtrArm.getStatorCurrent();
		entArm_Current.set(mtrArm.getStatorCurrent());
		entArm_MaxCurrent.set(mArmMaxCurrent);
		
	}

////Configuration

	/**
	 * Configure the arm motor's behavior when unpowered
	 * @param mode {@link NeutralMode}
	 */
	public static void configArmNeutralMode(NeutralMode mode) {
		mtrArm.setNeutralMode(mode);
	}

	/**
	 * Configure the intake motor's behavior when unpowered
	 * @param mode {@link NeutralMode}
	 */
	public static void configIntakeNeutralMode(NeutralMode mode) {
		mtrIntake.setNeutralMode(mode);
	}

////Arm Control
	public static double getArmPower() { return mArmPower; }
	public static double getIntakePower() { return mIntakePower; }
	public static boolean isArmAtUpperLimit() { return !limArm_U1.get() || !limArm_U2.get() || phoArm_U.get(); }
	public static boolean isArmAtLowerLimit() { return !limArm_L.get() || phoArm_L.get(); }
	public static double getArmPosition() { return mtrArm.getSelectedSensorPosition(); }

	public static void setArmPower(double power) { mArmPower = power; }
	public static void setIntakePower(double power) { mIntakePower = power; }

	public static void raiseArm() { setArmPower(PowerConstants.ARM_RAISE); }
	public static void disableArm() { setArmPower(0.0); }
	public static void lowerArm() { setArmPower(PowerConstants.ARM_LOWER); }

////Intake Control
	public static void enableIntake() { setIntakePower(PowerConstants.INTAKE_ENABLE); }
	public static void disableIntake() { setIntakePower(0.0); }
	public static void reverseIntake() { setIntakePower(PowerConstants.INTAKE_REVERSE); }

////Subsystem Methods

	/**
	 * Disable the entire Manipulator Subsystem
	 */
	public static void disable() { 
		disableArm();
		disableIntake();
	}

	/**
	 * Run periodically to update this subsystem
	 */
	public static void periodic() {
		if((isArmAtLowerLimit() && mArmPower < 0.0) || (isArmAtUpperLimit() && mArmPower > 0.0)) 
			disableArm();

		if(isArmAtUpperLimit()) mtrArm.setSelectedSensorPosition(0.0);
		if(isArmAtLowerLimit()) mtrArm.setSelectedSensorPosition(-300000.0);

		mtrArm.set(ControlMode.PercentOutput, mArmPower);
		mtrIntake.set(ControlMode.PercentOutput, mIntakePower);

		entArm_Power.set(getArmPower());
		entIntake_Power.set(getIntakePower());
		entArm_U_AtLimit.set(isArmAtUpperLimit());
		entArm_L_AtLimit.set(isArmAtLowerLimit());
	}
}
