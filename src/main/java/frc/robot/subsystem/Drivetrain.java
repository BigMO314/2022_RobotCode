package frc.robot.subsystem;

import static frc.robot.Robot.tblSubsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.networktables.NetworkTable;
import frc.molib.dashboard.Entry;
import frc.molib.utilities.Console;

public class Drivetrain {
	private static final WPI_TalonFX mtrDrive_L1 = new WPI_TalonFX(0);
	private static final WPI_TalonFX mtrDrive_L2 = new WPI_TalonFX(1);
	private static final WPI_TalonFX mtrDrive_R1 = new WPI_TalonFX(2);
	private static final WPI_TalonFX mtrDrive_R2 = new WPI_TalonFX(3);

	private static final NetworkTable tblDrivetrain = tblSubsystems.getSubTable("Drivetrain");
	private static final Entry<Double> entDrive_L_Power = new Entry<Double>(tblDrivetrain, "Left Power");
	private static final Entry<Double> entDrive_R_Power = new Entry<Double>(tblDrivetrain, "Right Power");

	private static double mLeftPower = 0.0;
	private static double mRightPower = 0.0;

	private Drivetrain() {}

	public static void init() {
		Console.logMsg("Drivetrain Initializing...");

		mtrDrive_L1.setInverted(false);
		mtrDrive_L2.setInverted(false);
		mtrDrive_R1.setInverted(true);
		mtrDrive_R2.setInverted(true);
	}

	/**
	 * Preload dashboard values
	 */
	public static void initDashboard() {
		
	}

	public static void disable() { setDrive(0.0, 0.0); }

	public static void setDrive(double leftPower, double rightPower) {
		mLeftPower = leftPower;
		mRightPower = rightPower;
	}

	public static void periodic() {
		mtrDrive_L1.set(ControlMode.PercentOutput, mLeftPower);
		mtrDrive_L2.set(ControlMode.PercentOutput, mLeftPower);
		mtrDrive_R1.set(ControlMode.PercentOutput, mRightPower);
		mtrDrive_R2.set(ControlMode.PercentOutput, mRightPower);

		entDrive_L_Power.set(mLeftPower);
		entDrive_R_Power.set(mRightPower);
	}
}
