package frc.robot.period;

import static frc.robot.Robot.tblPeriods;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Timer;
import frc.molib.dashboard.Option;
import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

public class Autonomous {
	/**
	 * Possible starting positions on the field
	 */
	private static enum Positions {
		LEFT("Left"),
		RIGHT("Right");

		public final String label;
		private Positions(String label) { this.label = label; }
		@Override public String toString() { return label; }
	}

	/**
	 * Possible Autonomous sequences to be run
	 */
	private static enum Sequences { 
		DO_NOTHING("Do Nothing"),
		SCORE("Score and Run"); 

		public final String label;
		private Sequences(String label) { this.label = label; }
		@Override public String toString() { return label; }
	}

	private static final NetworkTable tblAutonomous = tblPeriods.getSubTable("Autonomous");
	private static final Option<Positions> optPosition = new Option<Positions>(tblAutonomous, "Position", Positions.LEFT);
	private static final Option<Sequences> optSequence = new Option<Sequences>(tblAutonomous, "Sequence", Sequences.DO_NOTHING);

	private static Positions mSelectedPosition;
	private static Sequences mSelectedSequence;
	private static Timer tmrStage = new Timer();
	private static int mStage = 0;

	private Autonomous() {}

	/**
	 * Get ready for Autonomous Period. Collect any data from the dashboard, initialize any last minute systems, etc.
	 */
	public static void init() {
		Console.logMsg("Autonomous Period Initializing...");

		mSelectedPosition = optPosition.get();
		mSelectedSequence = optSequence.get();

		tmrStage.reset();
		mStage = 0;

		Console.logMsg("Starting Position: " + mSelectedPosition.toString());
		Console.logMsg("Autonomous Sequence: " + mSelectedSequence.toString());

		Manipulator.configArmNeutralMode(NeutralMode.Brake);
	}

	/**
	 * Preload dashboard values
	 */
	public static void initDashboard() {
		optPosition.init();
		optSequence.init();
	}

	private static void autonSCORE(){
		switch(mStage) {
			case 0:
				Console.logMsg("Starting Sequence [SCORE]");
				tmrStage.reset();
				tmrStage.start();
				mStage++;
				break;
			case 1:
				Console.logMsg("Reversing Intkae for 1 second...");
				Manipulator.reverseIntake();
				mStage++;
				break;
			case 2:
				if(tmrStage.hasElapsed(1.0)) mStage++;
				break;
			case 3:
				Console.logMsg("One second has passed. disable Intake.");
				Manipulator.disableIntake();
				Console.logMsg("Driving backward for one second...");
				Drivetrain.setDrive(-0.5, -0.5);
				tmrStage.reset();
				mStage++;
				break;
			case 4:
				if(tmrStage.hasElapsed(1.0)) mStage++;
				break;
			case 5:
				Console.logMsg("One second has passes. Stop Driving.");
				Drivetrain.disable();
				Console.logMsg("Sequence Completed [SCORE]");
				mStage++;
				break;
			default:
				Drivetrain.disable();
				Manipulator.disable();
				break;
		}
	}


	public static void periodic() {
		switch(mSelectedSequence) {
			case SCORE:
				autonSCORE();
				break;
			default: //If "DO_NOTHING" is selected or selected sequence does not appear here, just stop the robot
				Drivetrain.disable();
				Manipulator.disable();
				break;
		}

		Drivetrain.periodic();
		Manipulator.periodic();
	}
}
