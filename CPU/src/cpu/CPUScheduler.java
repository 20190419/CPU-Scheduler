package CPU;


import java.util.ArrayList;

public abstract class CPUScheduler {
    protected ArrayList<Process> processes =new ArrayList<>();
    protected int timeQuantum;
    protected String[] order;

    public boolean add(Process input)
    {
        return processes.add(input);
    }

    public void setTimeQuantum(int timeQuantum)
    {
        this.timeQuantum = timeQuantum;
    }

    public ArrayList<Process> getProcesses() {
        return processes;
    }

    public String[] getOrder() {
        return order;
    }

    public void printOrder() {
        System.out.print("process order :");
        for (int i=0;i<order.length;i++){
            if(i>0){
                if(order[i]==order[i-1])
                    continue;}
            System.out.print(order[i]+" ");
        }
    }


    public int getTimeQuantum()
    {
        return timeQuantum;
    }

    // return total waiting time
    public int getTotalBurstTime(){
        int totalBT=0;
        for(int i=0;i<processes.size();i++){
            totalBT+=processes.get(i).getBurstTime();
        }
        return totalBT;
    }
    public int getTotalWaitingTime()
    {
        int sum = 0;

        for (int i=0;i<processes.size();i++)
        {
            sum += processes.get(i).getWaitingTime();
        }

        return sum;
    }
    public int getTotalTurnAroundTime()
    {
        int sum = 0;

        for (int i=0;i<processes.size();i++)
        {
            sum += processes.get(i).getTurnaroundTime();
        }

        return sum;
    }
    public float getAverageWaitingTime()
    {
        float avg = 0.0F;
        avg=getTotalWaitingTime();
        return avg / processes.size();
    }
    public float getAverageTurnAroundTime()
    {
        float avg = 0.0F;
        avg=getTotalTurnAroundTime();
        return avg / processes.size();
    }
    public abstract void process();


}
