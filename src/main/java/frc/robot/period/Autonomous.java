package frc.robot.period;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Timer;
import frc.molib.dashboard.Option;
import frc.molib.utilities.Console;
import frc.robot.Robot;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Intake;

@SuppressWarnings("unused")
public class Autonomous {
	private static enum Positions {
		LEFT("Left"),
		RIGHT("Right");

		public final String label;
		private Positions(String label) { this.label = label; }
		@Override public String toString() { return label; }
	}
	private static enum Sequences { 
		NONE("Do Nothing"),
		TEST("Test"); 

		public final String label;
		private Sequences(String label) { this.label = label; }
		@Override public String toString() { return label; }
	}

	private static final NetworkTable tblAutonPeriod = Robot.tblPeriods.getSubTable("Autonomous");
	private static final Option<Positions> optPosition = new Option<Positions>(tblAutonPeriod, "Position", Positions.LEFT);
	private static final Option<Sequences> optSequence = new Option<Sequences>(tblAutonPeriod, "Sequence", Sequences.NONE);

	private static Positions mSelectedPosition;
	private static Sequences mSelectedSequence;
	private static Timer tmrStage = new Timer();
	private static int mStage = 0;

	private Autonomous() {}

	public static void initDashboard() {
		optPosition.init();
		optSequence.init();
	}

	public static void init() {
		mSelectedPosition = optPosition.get();
		mSelectedSequence = optSequence.get();
		tmrStage.reset();
		mStage = 0;

		Console.logMsg("Selected Auton: " + mSelectedSequence.toString() + " - " + mSelectedPosition.toString());
		Console.logMsg("Autonomous Initialized");
	}

	private static void autonNONE() {
		switch(mStage){
			case 0:
				Console.logMsg("Starting Sequence [NONE]");
				mStage++;
				break;
			case 1:
				Console.logMsg("Ending Sequence [NONE]");
				mStage++;
				break;
			default:
				Drivetrain.disable();
				Intake.disable();
				break;
		}
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
				Intake.disable();
				break;
		}
	}


	public static void periodic() {
		switch(mSelectedSequence) {
			case NONE:
				autonNONE();
				break;
			case TEST:
				autonFORWARD();
				break;
			default:
				Drivetrain.disable();
				Intake.disable();
				break;
		}

		Drivetrain.periodic();
		Intake.periodic();
	}
}
