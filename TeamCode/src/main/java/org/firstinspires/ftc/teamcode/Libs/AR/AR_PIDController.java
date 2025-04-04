package org.firstinspires.ftc.teamcode.Libs.AR;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * This class creates a PID Controller to use with each joint.
 *
 * Instantiate this class for each JOINT in the ARM object.
 *
 * Creation Date: 11/3/2024
 */

// So far works, please do not make any major changes, only to the PID values passed in the initialization stage.
public class AR_PIDController
{
    private PIDController controller;
    private double p, i, d;
    private double f;
    private final double ticksPerDegree = 5281.1 / 360;  // For GoBilda 30 RPM Motor
    LinearOpMode bot;
    public DcMotor motor;
    private String jointName;
    private int loopCount;  // Used for debugging to count the number of PID loops.
    public boolean fuzzyLogicActive = false;
    private double previousError = 0;  // Store the previous error for calculating error rate

    /**
     * Constructor. Perform the setup of the PID Controller.
     */
    public AR_PIDController(LinearOpMode iBot, DcMotor iMotor, String iJointName, double iP, double iI, double iD, double iF, boolean activateFuzzyLogic) {
        this.bot = iBot;
        this.motor = iMotor;
        this.jointName = iJointName;
        p = iP;
        i = iI;
        d = iD;
        f = iF;
        setFuzzyLogicActive(activateFuzzyLogic);
        controller = new PIDController(p, i, d);
        loopCount = 0; // Set to 0 when initialized
    }

    public void setFuzzyLogicActive(boolean active) {
        fuzzyLogicActive = active;
    }

    private void fuzzyLogicAutotune(double error, double errorRate) {
        // Todo: After-Comp Priority: Fix Auto-Tuner - It causes more twitching, and drastically changes values. The increments need to be changed, and time gaps need to be provided.
        if (error > 5 && errorRate > 2) {
            p = 1.0; i = 0.5; d = 0.2;  // High gains for fast response
        } else if (error > 2 && errorRate > 1) {
            p = 0.8; i = 0.4; d = 0.15; // Medium gains
        } else if (error < 1 && errorRate < 0.5) {
            p = 0.5; i = 0.2; d = 0.1;  // Low gains for stability
        } else {
            p = 0.6; i = 0.3; d = 0.1;  // Default values if fuzzy logic is not activated
        }
    }

    /**
     * This function takes a target value and moves the joint to that position.
     */
    private void setJointContinuous(boolean direction){
        if (direction){
            this.motor.setPower(1);}
        else{
            this.motor.setPower(-1);
        }
}
    public void loop(double target, int iCurrentState, int iLastState)
    { // Input in degrees
        if (fuzzyLogicActive) {
            double error = target - (motor.getCurrentPosition() / ticksPerDegree);  // Current error
            double errorRate = error - previousError;  // Calculate error rate
            previousError = error;  // Update previous error value for the next loop iteration
            fuzzyLogicAutotune(error, errorRate);  // Adjust PID parameters using fuzzy logic
        }

        this.controller.setPID(p, i, d);
        double armPos = 0;

        if (this.jointName.equals("first_joint")) {
            armPos = this.motor.getCurrentPosition() + (AR_Arm_Fisher.FIRST_JOINT_START * ticksPerDegree); // armPos is in Ticks
        }
        else if (this.jointName.equals("second_joint")) {
            armPos = this.motor.getCurrentPosition() + (AR_Arm_Fisher.SECOND_JOINT_START * ticksPerDegree); // armPos is in Ticks
        }

        double pid = this.controller.calculate( armPos, target * ticksPerDegree );  // target is in degrees
        double ff = Math.cos( Math.toRadians( target * ticksPerDegree ) ) * f;  // Feedforward
        double power = pid + ff;  // Total power from PID and feedforward

        if ((this.jointName.equals("first_joint")) && (iLastState == AR_Arm_Fisher.DEPLOY) && ((armPos / ticksPerDegree) < target - 40)) {
            power = power * 0.05;  // Throttle power back for arm descent.
        }
        if ((this.jointName.equals("first_joint")) && (iCurrentState == AR_Arm_Fisher.START)) {
            power = 0;  // Throttle power back for arm descent.
        }

// ToDo: Not sure why this code was added...
//       if ((this.jointName.equals("first_joint")) && (iLastState == AR_Arm_Fisher.START) && ((armPos / ticksPerDegree) < target - 10)) {
//           power = power * 0.00;
//       }
//       if ((this.jointName.equals("first_joint")) && (iLastState == AR_Arm_Fisher.ACTIVE) && ((armPos / ticksPerDegree) < target - 10)) {
//           power = power * 0.08;
//       }
//       power = power *.75;

        this.motor.setPower(power);

        this.bot.telemetry.addData("Power", " (" + this.jointName + ") " + power);
        this.bot.telemetry.addData("Pos(Ticks)", " (" + this.jointName + ") " + armPos);
        this.bot.telemetry.addData("Pos", " (" + this.jointName + ") " + armPos / ticksPerDegree);
        this.bot.telemetry.addData("Target", " (" + this.jointName + ") " + target);
        this.bot.telemetry.addData("States", iCurrentState + "(" + iLastState + ")");

/*        if( target != 0 ) {
            double armPosDegrees = ( armPos / ticksPerDegree );
            Log.i("AR_Experimental", this.jointName + "," + loopCount + "," + armPos + "," + armPosDegrees  + "," + target + "," + pid + "," + ff + "," + power );
        }
        loopCount = loopCount + 1;
 */
    }
}
