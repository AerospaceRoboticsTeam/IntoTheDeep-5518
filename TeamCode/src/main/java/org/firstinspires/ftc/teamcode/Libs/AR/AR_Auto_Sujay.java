package org.firstinspires.ftc.teamcode.Libs.AR;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "AR_Auto_Sujay", group = "Robot")
public class AR_Auto_Sujay extends LinearOpMode {
    private LinearOpMode iBot;
    private AR_Arm_Fisher_Test arm;
    //private AutonomousDrivetrain_Sujay drivetrain;

    public void runOpMode() {
        waitForStart();
        while (opModeIsActive()){
            iBot = this;
            //drivetrain = new AutonomousDrivetrain_Sujay(iBot);
            arm =  new AR_Arm_Fisher_Test(iBot);
            //Start
            //drivetrain.turnToHeading(iBot, .5, 90);
            //sleep(2000);
            //drivetrain.moveRobot(30,0);
            //Reach Bucket
            //sleep(2000);
            deploy();
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

    public void deploy(){
        arm.setArmDeployPos();
        while (arm.isBusy()){
            arm.updateArmPos();
        }
        /*
        //arm.drop();
        sleep(1500);
        //arm.rest();
        arm.setArmActivePos();
        sleep(1500);
        arm.setArmStartPos();
        sleep(1000);
         */
    }

    public void getSample (double startTravel, int returnTurn){
        //drivetrain.turnToHeading(iBot, .5, returnTurn);
        sleep(1500);
        //drivetrain.driveStraight(iBot, .5, startTravel, 0);
        //arm.grab();
        sleep(1500);
        //drivetrain.driveStraight(iBot, .5, -startTravel, 0);
        sleep(1500);
        //drivetrain.turnToHeading(iBot, .5, -returnTurn);
        //arm.rest();
        sleep(1500);
    }
}
