package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.TeleOp.Test.AR_Arm_Fisher_Test;
import org.firstinspires.ftc.teamcode.Libs.AR.AutonomousDrivetrain_Sujay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import android.util.Log;

@Autonomous(name = "AR_Auto_Update", group = "Robot")
public class AR_Auto_Update extends LinearOpMode {
    private static final Logger log = LoggerFactory.getLogger(AR_Auto_Update.class);
    public static final int DEPLOY = 3, GRAB = 2, ACTIVE = 1, START = 0, point0 = 4, point1 = 5, point2 = 6;
    public static int currentState = 0, lastState = 0;
    // Ideal Final State Machine: private int[] stateMachine = {START, ACTIVE, point0, DEPLOY, ACTIVE, point1};
    private int[] stateMachine = {START, ACTIVE, DEPLOY, ACTIVE};
    private LinearOpMode iBot;
    private AR_Arm_Fisher_Test arm;
    private AutonomousDrivetrain_Sujay drivetrain;

    public void runOpMode() {
        iBot = this;
        drivetrain = new AutonomousDrivetrain_Sujay(iBot);
        arm = new AR_Arm_Fisher_Test(iBot);
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            //Start
            drivetrain.turnToHeading(iBot, .5, -90);
            //sleep(2000);
            drivetrain.driveStraight(iBot, .5, 20, 0);
            //Reach Bucket
            //arm.setArmDeployPos();
            //getSample(10,-90);
            //deploy();
            //getSample(12, -70);
            //deploy();
            //getSample(14, -60);
            //deploy();
            //getSample(10, 0);
            //sleep(2000);
            //getSample(5, 70);
            //sleep(5000);
        }
    }
}