package CPU;

import java.util.*;

// a utility class to help compare the processes depending on their priorities
class PriorityComparator implements Comparator<Process> {
    @Override
    public int compare(Process o1, Process o2) {
        return Integer.compare(o1.getPriority(),o2.getPriority());
    }
}
// a utility class to help compare the processes depending on their arrival time
class ArrivalTimeComparator implements Comparator<Process> {
    @Override
    public int compare(Process o1, Process o2) {
        return Integer.compare(o1.getArrivalTime(),o2.getArrivalTime());
    }
}


public class PriorityScheduler extends CPUScheduler{

    public List<Process> orderedProcesses = new ArrayList();
    public int timePassed = 0;

    @Override
    public void process() {
        //Sorting processes depending on their arrival time
        while(!processes.isEmpty()) {
            // sort by arrival time
            processes.sort(new ArrivalTimeComparator());
            // list to take the arrived processes and check their priority
            List<Process> priorityOrderedProcesses = new ArrayList<>();
            // filling priorityOrderedProcesses with processes with arrival time = 0
            for (Process process : processes) {
                if (process.getChangeableArrivalTime() == 0)
                    priorityOrderedProcesses.add(process);
                else
                    break;
            }
            // order the processes by priority
            priorityOrderedProcesses.sort(new PriorityComparator());
            // if there is at least one process waiting to be executed
            if(!priorityOrderedProcesses.isEmpty()) {
                Process current = priorityOrderedProcesses.get(0);

                // calculate the different times for the process
                current.setFinishTime(timePassed+current.getBurstTime());
                current.setWaitingTime(timePassed-current.getArrivalTime());
                current.setTurnaroundTime(current.getFinishTime()-current.getArrivalTime());

                // process executed
                this.orderedProcesses.add(current);
                // removing from priority and ordered list
                this.processes.remove(current);
                priorityOrderedProcesses.remove(0);
                // update arrival time
                for (Process process : processes) {
                    process.setChangeableArrivalTime(process.getChangeableArrivalTime() - current.getBurstTime());
                    if (process.getChangeableArrivalTime() < 0)
                        process.setChangeableArrivalTime(0);
                }
                timePassed += current.getBurstTime();
            }else{
                // if no process arrived subtract 1 from their arrival time (time passes)
                for(Process process:processes){
                    process.setChangeableArrivalTime( process.getChangeableArrivalTime() - 1 );
                    timePassed++;
                }
            }

            //System.out.println(priorityOrderedProcesses);
        }
        //System.out.println(orderedProcesses);
        //System.out.println(timePassed);
    }

    public List<Process> getOrderedProcesses() {
        return orderedProcesses;
    }

    /*public static void main(String[] args){
        Process p1 = new Process("1", 0 , 4,1);
        Process p2 = new Process("2", 0 , 3,2);
        Process p3 = new Process("3", 6 , 7,1);
        Process p4 = new Process("4", 11, 4,3);
        Process p5 = new Process("5", 12, 2,2);
        CPUScheduler scheduler = new PriorityScheduler();
        scheduler.add(p1);
        scheduler.add(p2);
        scheduler.add(p3);
        scheduler.add(p4);
        scheduler.add(p5);
        scheduler.process();
    }*/
}
