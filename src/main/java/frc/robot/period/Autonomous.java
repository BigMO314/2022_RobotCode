package frc.robot.period;

import static frc.robot.Robot.tblPeriods;

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
		TEST("Test"); 

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
	}

	/**
	 * Preload dashboard values
	 */
	public static void initDashboard() {
		optPosition.init();
		optSequence.init();
	}

	private static void autonFORWARD() {
		switch(mStage){
			case 0:
				Console.logMsg("Starting Sequence [TEST]");
				tmrStage.reset();
				tmrStage.start();
				mStage++;
				break;
			case 1:
				Console.logMsg("Driving forward at 20% power.");
				Drivetrain.setDrive(0.2, 0.2);
				mStage++;
				break;
			case 2:
				if(tmrStage.hasElapsed(2.0)) mStage++;
				break;
			case 3:
				Console.logMsg("Two seconds have passed. Stop driving.");
				Drivetrain.disable();
				mStage++;
				break;
			case 4:
				Console.logMsg("Ending Sequence [TEST]");
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
			case TEST:
				autonFORWARD();
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
