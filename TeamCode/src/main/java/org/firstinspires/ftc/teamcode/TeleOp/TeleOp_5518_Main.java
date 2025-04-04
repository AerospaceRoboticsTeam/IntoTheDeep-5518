package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.Libs.AR.AR_Arm_Fisher;
import org.firstinspires.ftc.teamcode.Libs.AR.MecanumDrive_5518;

@TeleOp(name = "TeleOp_5518_Main", group = "TeleOp")
public class TeleOp_5518_Main extends LinearOpMode
{
    private MecanumDrive_5518 mecanumDrive;
    private AR_Arm_Fisher arm;
    private TouchSensor touch;


    //@Override
    public void runOpMode()
    {
        // Initialize the drivetrain
        mecanumDrive = new MecanumDrive_5518(this);
        arm = new AR_Arm_Fisher(this);
        this.touch = hardwareMap.get(TouchSensor.class, "touch");

        waitForStart();
        if (isStopRequested()) return;
        // Sets arm position to the default state for initialization, preventing the arm from driving inwards. If you smell smoke, the motor is probably overheating and driving inwards.
        arm.setArmStartPos();
        while (opModeIsActive())
        {
            //telemetry.addData("currentState", currentState);
            //telemetry.addData("lastState", lastState);
            // This call is made every loop and will read the current control pad values (for driving)
            // and update the drivetrain with those values. Enable if you need insights on the state machine.

            mecanumDrive.drive();
            if (gamepad1.left_trigger != 0) {
                mecanumDrive.setBoost(1);
            }
            else {
                mecanumDrive.setBoost(0.5);
            }

            if (gamepad2.dpad_up) {
                arm.setArmDeployPos();
                telemetry.addData("Arm Status", "Set Arm Deploy");
            }
            else if (gamepad2.dpad_right) {
                arm.setArmActivePos();
                telemetry.addData("Arm Status", "Set Arm Active");
            }
            else if (gamepad2.dpad_left) {
                arm.setArmReadyPos();
                telemetry.addData("Arm Status", "Set Arm Ready");
            }
            else if (gamepad2.dpad_down) {
                arm.setArmGrabPos();
                telemetry.addData("Arm Status", "Set Arm Grab");
            }
            // Todo: Test if this can hang specimen consistently
            else if (gamepad2.square) {
                arm.setArmHangPos();
                telemetry.addData("Arm Status", "Set Arm To Hanging and/or Ascent Mode");
            }
            else if (gamepad2.circle) {
                arm.setArmStartPos();
                telemetry.addData("Arm Status", "Set Arm Start");
            }
            /*
            // Todo: Test if this specimen grab works, and if time is available after other priorities, attempt to test and fine tune angles for this.
            else if (gamepad2.triangle){
                arm.setArmSpecimenGrab();
                telemetry.addData("Arm Status", "Set Arm For Specimen Grab");
            }
             */
            // Based on the inputs, the state changes and updates the position of the arm accordingly.
            arm.updateArmPos();

            if (gamepad2.left_trigger > 0.1) {
                arm.grab();
            } else if (gamepad2.right_trigger > 0.1){
                arm.drop();
            }
            else{
                arm.rest();
            }

            if (touch.getValue() == 1){
                arm.turnGreen();
            }
            else if(arm.getDetectedColor()==0) {
                arm.turnRed();
            }
            else if(arm.getDetectedColor()==1){
                arm.turnBlue();
            }
            else if (arm.getDetectedColor()==2){
                arm.turnYellow();
            }
            else {
                arm.turnNeutral();
            }
            telemetry.addData("Pressed", touch.getValue());
            arm.updateLight();

            //**************************************************************************************
            // ---------------------Gamepad 2 Controls ---------------------------------------------

            //**************************************************************************************
            //--------------------- TELEMETRY Code -------------------------------------------------
            // Useful telemetry data in case needed for testing and to find heading of robot
            mecanumDrive.getTelemetryData();
            arm.getTelemetry();
            telemetry.update();
        }
    }
}
