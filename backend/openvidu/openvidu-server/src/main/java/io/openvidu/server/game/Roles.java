package io.openvidu.server.game;

public class Roles {

    private String jobName;
    private boolean isChange;
    private Integer count;
    private Integer maxCount;

    Roles(String jobName, boolean isChange, Integer count, Integer maxCount) {
        this.jobName = jobName;
        this.isChange = isChange;
        this.count = count;
        this.maxCount = maxCount;
    }

    public String getJobName() {
        return jobName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public boolean isChange() {
        return isChange;
    }


}
