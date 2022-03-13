package frc.robot.period;

import static frc.robot.Robot.tblPeriods;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Timer;
import frc.molib.dashboard.Option;
import frc.molib.utilities.Console;
import frc.robot.subsystem.Drivetrain;
import frc.robot.subsystem.Manipulator;

/**
 * The class that handles all robot control during the Autonomous period of the game.
 * <i>All calls to this class should be done statically</i>
 */
public class Autonomous {
	/**
	 * Possible starting positions on the field
	 */
	private static enum Positions {
		LEFT("Left Side"),
		RIGHT("Right Side");

		public final String label;
		private Positions(String label) { this.label = label; }
		@Override public String toString() { return label; }
	}

	/**
	 * Possible Autonomous sequences to be run
	 */
	private static enum Sequences { 
		DO_NOTHING("Do Nothing") {
			@Override public void run() {
				Drivetrain.disable();
				Manipulator.disable();
			}
		}, 
		DRIVE("Just Drive") {
			@Override public void run() {
				switch(mStage) {
					case 0:
						Console.logMsg("Starting Sequence \"" + Sequences.DRIVE.toString() + "\" - " + mSelectedPosition.toString());
						tmrStage.reset();
						tmrStage.start();
						mStage++;
						break;
					case 1:
						Console.logMsg("Waiting 5 seconds...");
						mStage++;
						break;
					case 2:
						if(tmrStage.hasElapsed(5.0)) mStage++;
						break;
					case 3:
						Console.logMsg("Begin lowering arm and driving forward at 40% for 1.25 seconds...");
						Manipulator.lowerArm();
						Drivetrain.setDrive(0.4, 0.4);
						tmrStage.reset();
						mStage++;
						break;
					case 4:
						if(tmrStage.hasElapsed(1.25)) mStage++;
						break;
					case 5:
						Console.logMsg("Time has elapsed, stop driving. Waiting for Arm or time-out...");
						Drivetrain.disable();
						mStage++;
						break;
					case 6:
						if(Manipulator.isArmAtLowerLimit() || tmrStage.hasElapsed(3.0)) mStage++;
						break;
					case 7:
						Console.logMsg("Arm at lower limit, stop lowering.");
						Manipulator.disableArm();
						Console.logMsg("Sequence Completed \"" + Sequences.DRIVE.toString() + "\"");
						mStage++;
						break;
					default:
						Drivetrain.disable();
						Manipulator.disable();
				}
			}
		},
		SCORE("Simple Score") {
			@Override public void run() {
				switch(mStage) {
					case 0:
						Console.logMsg("Starting Sequence \"" + Sequences.SCORE.toString() + "\" - " + mSelectedPosition.toString());
						tmrStage.reset();
						tmrStage.start();
						mStage++;
						break;
					case 1:
						Console.logMsg("Reversing Intkae for 1.0 second...");
						Manipulator.reverseIntake();
						tmrStage.reset();
						mStage++;
						break;
					case 2:
						if(tmrStage.hasElapsed(1.0)) mStage++;
						break;
					case 3:
						Console.logMsg("Time has elapsed, disable Intake.");
						Manipulator.disableIntake();
						Console.logMsg("Waiting 5 seconds...");
						tmrStage.reset();
						mStage++;
						break;
					case 4:
						if(tmrStage.hasElapsed(5.0)) mStage++;
						break;
					case 5:
						Console.logMsg("Driving backward at 40% for 1.25 seconds...");
						Drivetrain.setDrive(-0.4, -0.4);
						tmrStage.reset();
						mStage++;
						break;
					case 6:
						if(tmrStage.hasElapsed(1.25)) mStage++;
						break;
					case 7:
						Console.logMsg("Time has elapsed, stop driving.");
						Drivetrain.disable();
						Console.logMsg("Sequence Completed \"" + Sequences.SCORE.toString() + "\"");
						mStage++;
						break;
					default:
						Drivetrain.disable();
						Manipulator.disable();
				}
			}
		}; 

		public abstract void run();

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
	 * Get ready for the Autonomous Period.
	 * Collect any data from the dashboard, initialize any last minute systems, etc.
	 */
	public static void init() {
		Console.logMsg("Autonomous Period Initializing...");

		//Get selected options from Dashboard
		mSelectedPosition = optPosition.get();
		mSelectedSequence = optSequence.get();

		//Reset stage data
		tmrStage.reset();
		mStage = 0;

		//Configure Brake/Coast mode for all systems
		Drivetrain.configDriveNeutralMode(NeutralMode.Brake);
		Manipulator.configArmNeutralMode(NeutralMode.Brake);
		Manipulator.configIntakeNeutralMode(NeutralMode.Coast);
	}

	/**
	 * Dashboard initialization run once after a connection to NetworkTables has been established
	 */
	public static void initDashboard() {
		optPosition.init();
		optSequence.init();
	}

	/**
	 * Run periodically to push new values to the Dashboard
	 */
	public static void updateDashboard() {
		
	}

	/**
	 * Run periodically to update subsystems
	 */
	public static void periodic() {
		//Run the selected sequence
		if(mSelectedSequence != null) {
			mSelectedSequence.run();
		} else {
			//If no sequence is selected, disable all systems
			Drivetrain.disable();
			Manipulator.disable();
		}

		//Subsystem updates
		Drivetrain.periodic();
		Manipulator.periodic();
	}
}
