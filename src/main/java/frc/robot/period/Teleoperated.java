package frc.robot.period;

import javax.lang.model.util.ElementScanner6;

import frc.molib.XboxController;
import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Intake;

@SuppressWarnings("unused")
public class Teleoperated {
	private static final XboxController ctlDriver = new XboxController(0);
	private static final XboxController ctlOperator = new XboxController(0);

	private static final class SpeedMultiplier {
		public static final double LOW = 0.25;
		public static final double STANDARD = 0.75;
		public static final double HIGH = 1.0;
	}

	//DO NO INSTANTIATE
	private Teleoperated() {}

	public static void init() {
		Console.logMsg("Teleoperated Initialized");
	}

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

		if(ctlDriver.getLeftBumper() || ctlOperator.getLeftBumper())
			Intake.raiseArm();
		else if(ctlDriver.getLeftBumper() || ctlOperator.getRightBumper())
			Intake.lowerArm();
		else
			Intake.disableArm();

		if(ctlDriver.getAButton() || ctlOperator.getAButton())
			Intake.reverseIntake();
		else if(ctlDriver.getRightTrigger() || ctlOperator.getRightTrigger())
			Intake.enableIntake();
		else
			Intake.disableIntake();

		Drivetrain.periodic();
		Intake.periodic();
	}
}
