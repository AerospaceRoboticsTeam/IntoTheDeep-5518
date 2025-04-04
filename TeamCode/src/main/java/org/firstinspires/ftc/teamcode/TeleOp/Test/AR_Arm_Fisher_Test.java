package org.firstinspires.ftc.teamcode.TeleOp.Test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.Libs.AR.AR_Arm_Fisher;
import org.firstinspires.ftc.teamcode.Libs.AR.AR_Joint;

/**
 * This class create an AR_Arm object that is used to encapsulate all the code used to control and use
 * the 2024-2025 Aerospace Robotics Robot Arm.
 *
 * Instantiate that class for each AR_Arm (Currently just one in our design).
 *
 * Creation Date: 11/3/2024
 */


public class AR_Arm_Fisher_Test
{
    /** Currently, the arm's rest is at approx. 43 degree up pointing straight down. That mean gravity is
     * working the most against the arm (horizontal) at -47 from the rest. So to make the angle align
     * with more realistic angles. the rest should be at 43 degree instead of 0.
     */
    public static double FIRST_JOINT_REST_ANGLE = -40; //degrees

    // These variables are used to customize joint angles for the AR_Arm. All of these
    // variables are available to be adjusted, in real-time, using FTC Dashboard.
    /** Angle of first Joint Starting Position */
    public static double FIRST_JOINT_START = -40;
    /** Angle of second Joint Starting Position */
    public static double SECOND_JOINT_START = 0;
    /** Angle of first Joint Active Position */
    public static double FIRST_JOINT_ACTIVE = -75;
    /** Angle of second Joint Active Position */
    public static double SECOND_JOINT_ACTIVE = -75;
    /** Angle of first Joint Deploy Position */
    public static double FIRST_JOINT_DEPLOY = -150;
    /** Angle of second Joint Deploy Position */
    public static double SECOND_JOINT_DEPLOY = -145;
    /** Angle of first Joint Grab Position */
    public static double FIRST_JOINT_GRAB = -75;
    /** Angle of second Joint Grab Position */
    public static double SECOND_JOINT_GRAB = -150;

    /** Angle of first Joint Deploy Position */
    public static double FIRST_JOINT_MID = -51;
    /** Angle of second Joint Deploy Position */
    public static double SECOND_JOINT_MID = -83;

    public static double P1 = 0.003, I1 = 0.05, D1 = 0.0001;
    public static double F1 = 0.05;

    public static double P2 = 0.001, I2 = 0.05, D2 = 0.0001;
    public static double F2 = 0.05;

    public static int START = 5;
    public static int ACTIVE = 1;
    public static int GRAB = 2;
    public static int DEPLOY = 3;
    public static int MID = 4;
    public static int NONE = 0;
    public boolean pressed = false;

    // Create a "AR_Joint" instance for each joint of the "AR_Arm".
    private AR_Joint jointFirst;
    private AR_Joint jointSecond;

    // Variables to save the desired angle of the two AR_JOINTs.
    private double targetFirst = FIRST_JOINT_START;
    private double targetSecond = SECOND_JOINT_START;

    private ColorSensor sensor;
    //private TouchSensor touch;

    private LinearOpMode bot;

    private int lastState = START;
    private int currentState = START;

    /**
     * Constructor. Gets the arm ready for moving.
     *
     * @param iBot Handle to the LinearOpMode.
     */
    public AR_Arm_Fisher_Test( LinearOpMode iBot )
    {
        // Take the passed in value and assign to class variables.
        this.bot = iBot;

        // Declare instances of the two joints.
        this.jointFirst = new AR_Joint(this.bot, "first_joint", P1, I1, D1, F1, false);
        this.jointSecond = new AR_Joint(this.bot, "second_joint", P2, I2, D2, F2, false);

        // Set FTC Dashboard Telemetry
    }

    /**
     * Return immediately and moves the joints to the desired location.
     */
    public void updateArmPos( )
    {
        // ToDo: I wonder if we need to come up with code to move the joints at different times. For example, maybe we have to move joint 1 20 degrees before moving joint 2 at all.
        // Arm should be tested before adding that code.
        this.jointFirst.moveJoint(this.targetFirst, currentState, lastState);
        this.jointSecond.moveJoint(this.targetSecond, currentState, lastState);
    }

    /**
     * Return immediately and sets the arm joint angles to a custom value.
     *
     * @param firstJoint The position of the first joint in degrees.
     * @param secondJoint The position of the second joint in degrees.
     */
    public void setArmCustomPos(int firstJoint, int secondJoint )
    {
        this.targetFirst = firstJoint; //degrees
        this.targetSecond = secondJoint; //degrees
    }

    /**
     * Return immediately and sets the arm joint angles to the preset location for deploying a specimen into the upper hopper.
     */
    public void setArmDeployPos( ) {
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        this.targetFirst = FIRST_JOINT_DEPLOY;
        this.targetSecond = SECOND_JOINT_DEPLOY;

        if (currentState != AR_Arm_Fisher.DEPLOY) {
            lastState = currentState;
            currentState = AR_Arm_Fisher.DEPLOY;
        }
    }

    /**
     * Return immediately and ets the arm joint angles to the preset location for picking up a specimen.
     */
    public void setArmGrabPos( )
    {
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        this.targetFirst = FIRST_JOINT_GRAB;
        this.targetSecond = SECOND_JOINT_GRAB;

        if( currentState != AR_Arm_Fisher.GRAB ) {
            lastState = currentState;
            currentState = AR_Arm_Fisher.GRAB;
        }
    }

    /**
     * Return immediately and sets the arm joint angles to the preset ready to travel position.
     */
    public void setArmActivePos( )
    {
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        this.targetFirst = FIRST_JOINT_ACTIVE;
        this.targetSecond = SECOND_JOINT_ACTIVE;

        if( currentState != AR_Arm_Fisher.ACTIVE ) {
            lastState = currentState;
            currentState = AR_Arm_Fisher.ACTIVE;
        }
    }

    /**
     * Return immediately and sets the arm joint angles to the start position.
     */
    public void setArmStartPos( )
    {
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        this.targetFirst = FIRST_JOINT_START;
        this.targetSecond = SECOND_JOINT_START;

        if( currentState != AR_Arm_Fisher.START ) {
            lastState = currentState;
            currentState = AR_Arm_Fisher.START;
        }
    }
    public void setArmMidPos( )
    {
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        this.targetFirst = FIRST_JOINT_MID;
        this.targetSecond = SECOND_JOINT_MID;

        if( currentState != AR_Arm_Fisher_Test.MID ) {
            lastState = currentState;
            currentState = AR_Arm_Fisher_Test.MID;
        }
    }

    public boolean isBusy(){
        return (jointFirst.isBusy() && jointSecond.isBusy());
    }

    public void getTelemetry(){
        bot.telemetry.addData("First Joint: ", (jointFirst.getTelemetry()*(360/5281.1)));
        bot.telemetry.addData("Second Joint: ", (jointSecond.getTelemetry()*(360/5281.1)));
    }
}