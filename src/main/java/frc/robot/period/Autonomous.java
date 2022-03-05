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
	 * Possible Autonomous sequences to be run
	 */
	private static enum Sequences { 
		DO_NOTHING("Do Nothing") {
			@Override public void run() {
				Drivetrain.disable();
				Manipulator.disable();
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
						Console.logMsg("Driving backward at 50% for one second...");
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
						Console.logMsg("Sequence Completed \"" + Sequences.SCORE.toString() + "\"");
						mStage++;
						break;
					default:
						Drivetrain.disable();
						Manipulator.disable();
						break;
				}
			}
		}; 

		public abstract void run();

		public final String label;
		private Sequences(String label) { this.label = label; }
		@Override public String toString() { return label; }
	}

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

	private static final NetworkTable tblAutonomous = tblPeriods.getSubTable("Autonomous");
	private static final Option<Sequences> optSequence = new Option<Sequences>(tblAutonomous, "Sequence", Sequences.DO_NOTHING);
	private static final Option<Positions> optPosition = new Option<Positions>(tblAutonomous, "Position", Positions.LEFT);

	private static Sequences mSelectedSequence;
	private static Positions mSelectedPosition;
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
		mSelectedSequence = optSequence.get();
		mSelectedPosition = optPosition.get();

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
