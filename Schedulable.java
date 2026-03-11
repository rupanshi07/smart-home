public interface Schedulable {
    void setSchedule(String onTime, String offTime);
    String getSchedule();
    boolean isScheduleActive();
}
