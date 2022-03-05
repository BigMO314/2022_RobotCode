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
	private static final Option<DriveStyles> optDriveStyle = new Option<DriveStyles>(tblTeleoperated, "Driving Control Style", DriveStyles.CHEESY);

	private static DriveStyles mSelectedDriveStyle;

	private static final XboxController ctlDriver = new XboxController(0);
	private static final XboxController ctlOperator = new XboxController(1);

	private Teleoperated() {}

	/**
	 * Get ready for the Teleoperated Period.
	 * Collect any data from the dashboard, initialize any last minute systems, etc.
	 */
	public static void init() {
		Console.logMsg("Teleoperated Period Initializing...");

		//Get selected options from Dashboard
		mSelectedDriveStyle = optDriveStyle.get();

		//Configure Brake/Coast mode for all systems
		Drivetrain.configDriveNeutralMode(NeutralMode.Brake);
		Manipulator.configArmNeutralMode(NeutralMode.Brake);
		Manipulator.configIntakeNeutralMode(NeutralMode.Coast);
	}

	/**
	 * Dashboard initialization run once after a connection to NetworkTables has been established
	 */
	public static void initDashboard() {
		optDriveStyle.init();
	}

	/**
	 * Set Power to the Drivetrain through left and right values
	 * @param leftPower		Left side power
	 * @param rightPower	Right side power
	 */
	private static void setTankDrive(double leftPower, double rightPower) {
		Drivetrain.setDrive(leftPower, rightPower);
	}
	
	/**
	 * Set power to the Drivetrain through a throttle and steering value
	 * @param throttle	Forward power 
	 * @param steering	Turning power
	 */
	private static void setArcadeDrive(double throttle, double steering) {
		Drivetrain.setDrive(throttle + steering, throttle - steering);
	}

	/**
	 * Run periodically to update subsystems
	 */
	public static void periodic() {
		//Switch between Drive control styles
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
		
		//Arm controls
		if(ctlDriver.getRightBumper() || ctlOperator.getRightBumper())
			Manipulator.raiseArm();
		else if(ctlDriver.getLeftBumper() || ctlOperator.getLeftBumper())
			Manipulator.lowerArm();
		else
			Manipulator.disableArm();

		//Intake controls
		if(ctlDriver.getAButton() || ctlOperator.getAButton())
			Manipulator.reverseIntake();
		else if(ctlDriver.getRightTrigger() || ctlOperator.getRightTrigger())
			Manipulator.enableIntake();
		else
			Manipulator.disableIntake();

		//Subsystem updates
		Drivetrain.periodic();
		Manipulator.periodic();
	}
}
