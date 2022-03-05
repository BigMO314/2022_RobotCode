package frc.robot.period;

import static frc.robot.Robot.tblPeriods;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTable;
import frc.molib.XboxController;
import frc.molib.dashboard.Option;
import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

@SuppressWarnings("unused")
public class Teleoperated {
	private static enum DriveStyles {
		CHEESY("Cheesy Drive"),
		ARCADE("Arcade Drive"),
		TANK("Tank Drive");

		public final String label;
		private DriveStyles(String label) { this.label = label;}
		@Override public String toString() { return label; }
	}

	private static final class SpeedMultiplier {
		public static final double LOW = 0.25;
		public static final double STANDARD = 0.75;
		public static final double HIGH = 1.0; //Unused
	}

	private static final NetworkTable tblTeleoperated = tblPeriods.getSubTable("Teleoperated");
	private static final Option<DriveStyles> optDriveStyle = new Option<DriveStyles>(tblTeleoperated, "Drive Style", DriveStyles.CHEESY);

	private static DriveStyles mSelectedDriveStyle;

	private static final XboxController ctlDriver = new XboxController(0);
	private static final XboxController ctlOperator = new XboxController(1);

	private Teleoperated() {}

	public static void init() {
		Console.logMsg("Teleoperated Period Initializing...");

		mSelectedDriveStyle = optDriveStyle.get();

		Manipulator.configArmNeutralMode(NeutralMode.Brake);
	}

	/**
	 * Preload dashboard values
	 */
	public static void initDashboard() {
		optDriveStyle.init();
	}

	private static void setTankDrive(double leftPower, double rightPower) {
		Drivetrain.setDrive(leftPower, rightPower);
	}
	
	private static void setArcadeDrive(double throttle, double steering) {
		Drivetrain.setDrive(throttle + steering, throttle - steering);
	}

	public static void periodic() {
		switch(mSelectedDriveStyle) {
			case CHEESY:
				if(ctlDriver.getLeftTrigger())
					setArcadeDrive(ctlDriver.getLeftY() * SpeedMultiplier.LOW, ctlDriver.getRightX() * SpeedMultiplier.LOW);
				else
					setArcadeDrive(ctlDriver.getLeftY() * SpeedMultiplier.STANDARD, ctlDriver.getRightX() * SpeedMultiplier.STANDARD);
				break;
			case ARCADE:
				if(ctlDriver.getLeftTrigger())
					setArcadeDrive(ctlDriver.getLeftY() * SpeedMultiplier.LOW, ctlDriver.getLeftX() * SpeedMultiplier.LOW);
				else
					setArcadeDrive(ctlDriver.getLeftY() * SpeedMultiplier.STANDARD, ctlDriver.getLeftX() * SpeedMultiplier.STANDARD);
				break;
			case TANK:
				if(ctlDriver.getLeftTrigger())
					setTankDrive(ctlDriver.getLeftY() * SpeedMultiplier.LOW, ctlDriver.getRightY() * SpeedMultiplier.LOW);
				else
					setTankDrive(ctlDriver.getLeftY() * SpeedMultiplier.STANDARD, ctlDriver.getRightY() * SpeedMultiplier.STANDARD);
				break;
			default:
				Drivetrain.disable();
		}
		
		if(ctlDriver.getRightBumper() || ctlOperator.getRightBumper())
			Manipulator.raiseArm();
		else if(ctlDriver.getLeftBumper() || ctlOperator.getLeftBumper())
			Manipulator.lowerArm();
		else
			Manipulator.disableArm();

		if(ctlDriver.getAButton() || ctlOperator.getAButton())
			Manipulator.reverseIntake();
		else if(ctlDriver.getRightTrigger() || ctlOperator.getRightTrigger())
			Manipulator.enableIntake();
		else
			Manipulator.disableIntake();

		Drivetrain.periodic();
		Manipulator.periodic();
	}
}
