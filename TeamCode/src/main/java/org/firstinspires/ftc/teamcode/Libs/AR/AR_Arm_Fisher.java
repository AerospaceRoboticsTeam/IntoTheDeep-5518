package org.firstinspires.ftc.teamcode.Libs.AR;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Libs.AR.AR_Joint;
import org.firstinspires.ftc.teamcode.Libs.AR.Archive.AR_Arm;

/**
 * This class create an AR_Arm object that is used to encapsulate all the code used to control and use
 * the 2024-2025 Aerospace Robotics Robot Arm.
 *
 * Instantiate that class for each AR_Arm (Currently just one in our design).
 *
 * Creation Date: 11/3/2024
 */


public class AR_Arm_Fisher
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
    public static double FIRST_JOINT_ACTIVE = -110; // -100, -90
    /** Angle of second Joint Active Position */
    public static double SECOND_JOINT_ACTIVE = -80;
    /** Angle of first Joint Deploy Position */
    public static double FIRST_JOINT_DEPLOY = -150; //155
    /** Angle of second Joint Deploy Position */
    public static double SECOND_JOINT_DEPLOY = 0; // -110
    public static double FIRST_JOINT_DEPLOY_1 = -150; //155
    /** Angle of second Joint Deploy Position */
    public static double SECOND_JOINT_DEPLOY_1 = -125; // -110
    /** Angle of first Joint Grab Position: Should be fined tuned a little more.*/
    public static double FIRST_JOINT_GRAB = -80; // 100
    /** Angle of second Joint Grab Position */
    public static double SECOND_JOINT_GRAB = -115; // -160, -140
    public static double FIRST_JOINT_READY = -80; // 100
    /** Angle of second Joint Grab Position */
    public static double SECOND_JOINT_READY = -105; // -130, -140
    /** Angle of first Joint Deploy Position */
    public static double FIRST_JOINT_HANG = -120;
    /** Angle of second Joint Deploy Position */
    public static double SECOND_JOINT_HANG = -130;
    // Todo: Secondary Priority: Perfect Specimen Grabbing Angle (Ideally no decimals)
    public static double FIRST_JOINT_SPECIMEN_GRAB = -51;  // Unsure what it was used for
    public static double SECOND_JOINT_SPECIMEN_GRAB = -83; // Unsure what it was used for

    // Todo: MAIN PRIORITY - TUNE EACH VALUE OF PIDF IN CHRONOLOGICAL ORDER OF P, I, D, AND F FOR BOTH MOTORS.
    // Current Test: P1 is over-shooting a little, needs to be slightly reduced.
    public static double P1 = 0.003, I1 = 0.05, D1 = 0.0001;
    public static double F1 = 0.05;

    public static double P2 = 0.001, I2 = 0.05, D2 = 0.0001;
    public static double F2 = 0.05;

    public static int START = 5;
    public static int ACTIVE = 1;
    public static int GRAB = 2;
    public static int DEPLOY = 3;
    public static int DEPLOY_1 = 7;
    public static int HANG = 4;
    public static int GRAB_SPECIMEN = 5;
    public static int READY = 6;
    public static int NONE = 0;
    public boolean pressed = false;

    // Create a "AR_Joint" instance for each joint of the "AR_Arm".
    private AR_Joint jointFirst;
    private AR_Joint jointSecond;

    // Variables to save the desired angle of the two AR_JOINTs.
    private double targetFirst;
    private double targetSecond;
    private CRServo leftGripper;
    private CRServo rightGripper;
    private ColorSensor sensor;
    //private TouchSensor touch;

    private LinearOpMode bot;

    public AR_Joint getJointFirst() {
        return jointFirst;
    }

    AR_Light light;

    private int lastState = START;
    private int currentState = START;

    /**
     * Constructor. Gets the arm ready for moving.
     *
     * @param iBot Handle to the LinearOpMode.
     */
    public AR_Arm_Fisher( LinearOpMode iBot )
    {
        // Take the passed in value and assign to class variables.
        this.bot = iBot;

        // Declare instances of the two joints.
        this.jointFirst = new AR_Joint(this.bot, "first_joint", P1, I1, D1, F1, false);
        this.jointSecond = new AR_Joint(this.bot, "second_joint", P2, I2, D2, F2, false);
        leftGripper = bot.hardwareMap.crservo.get("left_gripper");
        rightGripper = bot.hardwareMap.crservo.get("right_gripper");
        this.sensor = bot.hardwareMap.get(ColorSensor.class, "color_sensor");
        this.light = new AR_Light("status_light", iBot);
        // Set FTC Dashboard Telemetry
    }

    public int getDetectedColor() {
        int red = sensor.red();
        int green = sensor.green();
        int blue = sensor.blue();

        if (red > green && red > blue && red > 175) {
            return 0; // Red detected
        } else if (blue > red && blue > green) {
            return 1; // Blue detected
        } else if (red > 500 && green > 500 && blue < 500) {
            return 2; // Yellow detected (Red + Green strong, Blue weak)
        } else {
            return -1; // No clear detection
        }
    }

    public void turnBlue(){
        light.blueLight();
    }
    public void turnRed(){
        light.redLight();
    }
    public void turnYellow(){
        light.yellowLight();
    }

    public void turnGreen(){
        light.greenLight();
    }
    // Todo: Check and correct this program to see if the default/neutral color is white, it was observed to be red...

    public void turnNeutral(){
        light.whiteLight();
    }

    public void updateLight(){
        light.updateLight();
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

    // The setArmCustomPos method is designed to help develop any code for specific angles in emergency scenarios.
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

    public void setArmDeploy1Pos( ) {
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        this.targetFirst = FIRST_JOINT_DEPLOY_1;
        this.targetSecond = SECOND_JOINT_DEPLOY_1;

        if (currentState != AR_Arm_Fisher.DEPLOY_1) {
            lastState = currentState;
            currentState = AR_Arm_Fisher.DEPLOY_1;
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
        // Todo: Fine Tune Angles for Grabbing with Hover Room: With a buffer set to ensure there is no interference with the bottom rim of the submersible.
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


        // Todo: Somehow the power should be set to zero after movement because we don't want to waste battery power holding
        // the arm in the lowered position. This will only work if the arm has a rest which it doesn't right now.
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
    public void setArmHangPos( )
    {
        // This is used for both aiding in hanging the robot during endgame and also to hang specimen (not 100% confirmed and approved for strategy yet).
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        this.targetFirst = FIRST_JOINT_HANG;
        this.targetSecond = SECOND_JOINT_HANG;

        if( currentState != AR_Arm_Fisher.HANG ) {
            lastState = currentState;
            currentState = AR_Arm_Fisher.HANG;

        }
    }
    public void setArmReadyPos( )
    {
        // This is used for both aiding in hanging the robot during endgame and also to hang specimen (not 100% confirmed and approved for strategy yet).
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        this.targetFirst = FIRST_JOINT_READY;
        this.targetSecond = SECOND_JOINT_READY;

        if( currentState != AR_Arm_Fisher.READY ) {
            lastState = currentState;
            currentState = AR_Arm_Fisher.READY;

        }
    }

    public void getTelemetry(){
        bot.telemetry.addData("First Joint: ", (jointFirst.getTelemetry()*(360/5281.1)));
        bot.telemetry.addData("Second Joint: ", (jointSecond.getTelemetry()*(360/5281.1)));
        bot.telemetry.addData("Red", sensor.red());
        bot.telemetry.addData("Blue", sensor.blue());
        bot.telemetry.addData("Green", sensor.green());
    }

    public void grab()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(-.2);
        rightGripper.setPower(.2);
    }
    public void drop()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(.2);
        rightGripper.setPower(-.2);
    }
    public void rest()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(0);
        rightGripper.setPower(0);
    }

    // .
    public void setArmSpecimenGrab(){
        this.targetFirst = FIRST_JOINT_SPECIMEN_GRAB;
        this.targetSecond = SECOND_JOINT_SPECIMEN_GRAB;
        if (currentState != AR_Arm_Fisher.GRAB_SPECIMEN) {
            lastState = currentState;
            currentState = AR_Arm_Fisher.GRAB_SPECIMEN;
        }
            
    }
}
//    public void lockInward(){
//        AR_PIDController setJointContinuous ;
//        setJointContinuous.setJointContinuous(true);
//    }

