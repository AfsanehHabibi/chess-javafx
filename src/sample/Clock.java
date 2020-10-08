package sample;

import java.io.Serializable;

public class Clock implements Serializable{
    private int hour;
    private int minute;
    private int seconds;
    private boolean flag=true;
    public Clock(String s){
        String[] strings=s.split(" ");
        if(strings[1].equals("hour")){
            hour=Integer.parseInt(strings[0]);
            minute=0;
            seconds=0;
        }
        else {
            minute = Integer.parseInt(strings[0]);
            hour = 0;
            seconds = 0;
        }
    }

    public Clock(Clock clock) {
        this.seconds=clock.seconds;
        this.hour=clock.hour;
        this.minute=clock.minute;
    }

    public void stopThread(){
        flag=false;
    }
    @Override
    public String toString() {
        if(hour==0)
        return minute+" min";
        else
            return hour+" hour";
    }
    public String showTime(){
        return hour+" : "+minute+" : "+seconds;
    }

    public boolean sendData(){
        return flag;
    }
    public boolean passSecond(){
        if(flag) {
            if (seconds > 0) {
                seconds--;
                return true;
            } else if (minute > 0) {
                minute--;
                seconds = 59;
                return true;
            } else if (hour > 0) {
                hour--;
                minute = 59;
                seconds = 59;
                return true;
            }
        }
            return false;
    }

    public int getMillis() {
        return hour*60*60*1000+minute*60*1000+seconds*1000;
    }
}
