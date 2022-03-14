package io.openvidu.server.game;

import io.openvidu.server.core.Participant;

public class Characters {
    //역할 이름
    private String jobName;
    //생존 여부
    private boolean isAlive = true;
    //역할 맡는 플레이어
    private Participant participant;
    //보호막 적용중 여부
    private boolean isProtected = false;

    public Characters(String jobName, Participant participant) {
        this.jobName = jobName;
        this.participant = participant;
    }

    public String getJobName() {
        return jobName;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
