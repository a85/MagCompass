package com.rickreation.filters.kalman;

public class KalmanState {
    private double q; //Process noise covariance
    private double r; //Measurement noise covariance
    private double x; //value
    private double p; //estimation error covariance
    private double k; //kalman gain

    public KalmanState(double q, double r, double p, double x) {
        this.q = q;
        this.r = r;
        this.p = p;
        this.x = x;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public void update(double measurement) {
        //Prediction update
        p = p + q;
        k = (p / (p + r));
        x = x + k * (measurement - x);
        p = (1 - k) * p;
    }
}
