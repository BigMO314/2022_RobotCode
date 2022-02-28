package frc.robot.period;

import static frc.robot.Robot.tblPeriods;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.networktables.NetworkTable;
import frc.molib.XboxController;
import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

@SuppressWarnings("unused")
public class Teleoperated {
	private static final NetworkTable tblTeleoperated = tblPeriods.getSubTable("Teleoperated");

	private static final XboxController ctlDriver = new XboxController(0);
	private static final XboxController ctlOperator = new XboxController(1);

	private static final class SpeedMultiplier {
		public static final double LOW = 0.25;
		public static final double STANDARD = 0.75;
		public static final double HIGH = 1.0; //Unused
	}

	private Teleoperated() {}

	public static void init() {
		Console.logMsg("Teleoperated Period Initializing...");
	}

	/**
	 * Preload dashboard values
	 */
	public static void initDashboard() {

	}

	private static void setTankDrive(double leftPower, double rightPower) {
		Drivetrain.setDrive(leftPower, rightPower);
	}
	
	private static void setArcadeDrive(double throttle, double steering) {
		Drivetrain.setDrive(throttle + steering, throttle - steering);
	}

	public static void periodic() {
		if(ctlDriver.getLeftTrigger())
			setArcadeDrive(ctlDriver.getLeftY() * SpeedMultiplier.LOW, ctlDriver.getRightX() * SpeedMultiplier.LOW);
		else
			setArcadeDrive(ctlDriver.getLeftY() * SpeedMultiplier.STANDARD, ctlDriver.getRightX() * SpeedMultiplier.STANDARD);

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
