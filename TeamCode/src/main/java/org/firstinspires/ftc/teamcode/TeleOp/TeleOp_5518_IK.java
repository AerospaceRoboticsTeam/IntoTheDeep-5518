package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.TouchSensor;

import android.util.Log;

import org.firstinspires.ftc.teamcode.Libs.AR.AR_Arm_Fisher;
import org.firstinspires.ftc.teamcode.Libs.AR.MecanumDrive_5518;

@TeleOp(name = "TeleOp_5518_IK", group = "TeleOp")
public class TeleOp_5518_IK extends LinearOpMode
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

        while (opModeIsActive())
        {
            //telemetry.addData("currentState", currentState);
            //telemetry.addData("lastState", lastState);
            // This call is made every loop and will read the current control pad values (for driving)
            // and update the drivetrain with those values.
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
                arm.setArmStartPos();
                telemetry.addData("Arm Status", "Set Arm Start");
            }
            else if (gamepad2.dpad_down) {
                arm.setArmGrabPos();
                telemetry.addData("Arm Status", "Set Arm Grab");
            }
            else if (gamepad2.square) {
                arm.setArmMidPos();
                telemetry.addData("Arm Status", "Set Arm Mid");
            }
            arm.updateArmPos();

            if (gamepad2.left_trigger > 0.1) {
                arm.grab();
            } else if (gamepad2.right_trigger > 0.1){
                arm.drop();
            }
            else{
                arm.rest();
            }

            if (touch.isPressed()){
                arm.turnGreen();
            }
            else if(arm.getDetectedColor()==1){
                arm.turnBlue();
            }
            else if(arm.getDetectedColor()==0){
                arm.turnRed();
            }
            else{
                arm.turnYellow();
            }
            telemetry.addData("Pressed", touch.isPressed());
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
