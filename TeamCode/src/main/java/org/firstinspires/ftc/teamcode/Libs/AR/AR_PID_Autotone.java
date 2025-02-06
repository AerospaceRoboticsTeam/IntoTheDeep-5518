package org.firstinspires.ftc.teamcode.Libs.AR;
// Testing-Testing Rithvik/rickyG242
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "AR_PID_Autotone", group = "Linear Opmode")
public class AR_PID_Autotone extends LinearOpMode {
    private DcMotor shoulderMotor;
    private DcMotor elbowMotor;
    private PIDController controller;
    private static final double TARGET_POSITION_SHOULDER = 1000;
    private static final double TARGET_POSITION_ELBOW = 500;

    @Override
    public void runOpMode() {
        shoulderMotor = hardwareMap.get(DcMotor.class, "shoulderMotor");
        elbowMotor = hardwareMap.get(DcMotor.class, "elbowMotor");
        telemetry.addData("Status", "Waiting for start...");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            // Perform Ziegler-Nichols tuning for the shoulder motor
            PIDController shoulderPID = new PIDController(0, 0, 0);  // Initial P=0, I=0, D=0
            double shoulderKu = findUltimateGain(shoulderMotor);
            double shoulderPu = findOscillationPeriod(shoulderMotor, shoulderKu);
            double[] shoulderPIDValues = zieglerNichols(shoulderKu, shoulderPu);
            // Perform Ziegler-Nichols tuning for the elbow motor
            PIDController elbowPID = new PIDController(0, 0, 0);  // Initial P=0, I=0, D=0
            double elbowKu = findUltimateGain(elbowMotor);
            double elbowPu = findOscillationPeriod(elbowMotor, elbowKu);
            double[] elbowPIDValues = zieglerNichols(elbowKu, elbowPu);
            telemetry.addData("Shoulder PID", "P: %.3f, I: %.3f, D: %.3f", shoulderPIDValues[0], shoulderPIDValues[1], shoulderPIDValues[2]);
            telemetry.addData("Elbow PID", "P: %.3f, I: %.3f, D: %.3f", elbowPIDValues[0], elbowPIDValues[1], elbowPIDValues[2]);
            telemetry.update();
            // Move the joints with the Ziegler-Nichols tuned PID values
            moveJoint(shoulderMotor, TARGET_POSITION_SHOULDER, shoulderPIDValues);
            moveJoint(elbowMotor, TARGET_POSITION_ELBOW, elbowPIDValues);
            sleep(5000);
        }
    }

    private double findUltimateGain(DcMotor motor) {
        // Gradually increase P until oscillations begin
        double p = 0.1;
        double lastPosition = motor.getCurrentPosition();
        double currentPosition = motor.getCurrentPosition();
        boolean oscillating = false;
        // Increase P and check for oscillations
        while (!oscillating && opModeIsActive()) {
            motor.setPower(p);
            currentPosition = motor.getCurrentPosition();
            if (Math.abs(currentPosition - lastPosition) < 1) {
                oscillating = true;
            } else {
                p += 0.1;  // Increase the P value until oscillations are detected
            }
            lastPosition = currentPosition;
        }
        return p;  // Ultimate gain (Ku) when oscillations are detected
    }

    private double findOscillationPeriod(DcMotor motor, double ultimateGain) {
        motor.setPower(ultimateGain);
        long startTime = System.currentTimeMillis();
        long period = 0;
        double lastPosition = motor.getCurrentPosition();
        double currentPosition;
        while (opModeIsActive()) {
            currentPosition = motor.getCurrentPosition();
            if (Math.abs(currentPosition - lastPosition) < 1) {
                long currentTime = System.currentTimeMillis();
                period = currentTime - startTime;
                break;
            }
            lastPosition = currentPosition;
        }
        return period / 1000.0;  // Return period in seconds
    }

    private double[] zieglerNichols(double Ku, double Pu) {
        double P = 0.6 * Ku;
        double I = 2 * P / Pu;
        double D = P * Pu / 8;
        return new double[] { P, I, D };
    }

    private void moveJoint(DcMotor motor, double targetPosition, double[] pidValues) {
        PIDController pid = new PIDController(pidValues[0], pidValues[1], pidValues[2]);
        while (opModeIsActive() && Math.abs(motor.getCurrentPosition() - targetPosition) > 5) {
            double output = pid.calculate(targetPosition, motor.getCurrentPosition());
            motor.setPower(output);
            telemetry.addData("Motor", motor.getDeviceName());
            telemetry.addData("Current Pos", motor.getCurrentPosition());
            telemetry.addData("Target Pos", targetPosition);
            telemetry.update();
        }
    }
}
