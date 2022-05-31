package CPU;

import java.util.*;

public class AGAT extends CPUScheduler {

    protected Queue<Process> readyQueue = new LinkedList<Process>();
    private final ArrayList<Process> Test_out = new ArrayList<Process>();
    private final ArrayList<Process> proc_2 = new ArrayList<Process>();

    public float calculate_v1() {
        float x1;
        int Result = processes.get(processes.size() - 1).getArrivalTime();
        if (Result > 10) {
            x1 = Result / 10F;
        } else {
            x1 = 1;
        }
        return x1;
    }

    public float calculate_v2() {
        float x2;
        int Result = 0;
        int n = processes.size();
        Vector<Integer> remainingBursts = new Vector<>();
        for (Process process : processes) {
            remainingBursts.add(process.getRemainingTime());
        }
        int mx = remainingBursts.get(0);
        int i = 0;
        while (i < n) {
            Result = Math.max(mx, remainingBursts.get(i));
            i++;
        }
        if (Result > 10) {
            x2 = Result / 10F;
        } else {
            x2 = 1;
        }
        return x2;
    }

    public double agatFactor(int priority, int arrivalTime, int burstTime) {

        float v1 = calculate_v1();
        float v2 = calculate_v2();
        double out = ((10 - priority) + Math.ceil(arrivalTime / v1) + Math.ceil(burstTime / v2));
        System.out.println("Factor: " + out + " ----------------V2: " + v2);
        return out;
    }

    public boolean isFinished() {
        for (Process process : processes) {
            if (process.getQuantum() != 0) {
                return false;
            }
        }

        return true;
    }

    public Process getLeastAG(int _time) {
        double minAG_ = Integer.MAX_VALUE;
        int index_ = 0;

        int r = 0;
        while ((r < this.processes.size()) && (this.processes.get(r).getArrivalTime() <= _time)) {
            if (this.processes.get(r).getAGAT_Factor() < minAG_ && this.processes.get(r).getQuantum() != 0) {
                minAG_ = this.processes.get(r).getAGAT_Factor();
                index_ = r;
            }
            r++;
        }

        return this.processes.get(index_);
    }

    public int getProcessIndex(Process process) {
        int i = 0;
        while (i < processes.size()) {
            if (processes.get(i).getProcessName().equals(process.getProcessName())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean isLastProcess(Process pro, int Time) {
        int index = this.getProcessIndex(pro);
        boolean bool = true;

        if (index == this.processes.size() - 1) {
            return true;
        }

        index++;
        while ((index < this.processes.size()) && this.processes.get(index).getArrivalTime() <= Time) {
            if (this.processes.get(index).getAGAT_Factor() != 0) {
                bool = false;
                break;
            }
            index++;
        }

        return bool;
    }

    public Process getBestProcess(int _time, int fristIndex) {
        Process p = processes.get(fristIndex);

        if (this.isLastProcess(p, _time) && readyQueue.size() > 0) {
            Process temp_ = readyQueue.poll();

            while (true) {
                assert temp_ != null;
                if (temp_.getRemainingTime() != 0) {
                    return temp_;
                }

                temp_ = readyQueue.poll();
            }

        } else {
            return getLeastAG(_time);
        }
    }

    public void setProcesses() {
        // Set Remaining Time for all processes (Remaining Time = burstTime), AG-Factor
        for (Process process : this.processes) {
            process.setRemainingTime(process.getBurstTime());
            process.setAGAT_Factor(agatFactor(process.getPriority(), process.getArrivalTime(), process.getBurstTime()));
        }
    }

    public void setProcessTime() {
        for (Process process : this.processes) {
            // Set AG-Factor
            if (process.getRemainingTime() != 0) {
                System.out.println(process.getProcessName() + "  Arrival >> " + process.getArrivalTime() + " -------------------Remaining time: " + process.getRemainingTime());
                process.setAGAT_Factor(agatFactor(process.getPriority(), process.getArrivalTime(), process.getRemainingTime()));
                continue;
            }
            process.setAGAT_Factor(0);
        }
    }

    public int nonPreemptiveAG(Process pro) {
        int nonPreemptiveAGTime = (int) Math.ceil(pro.getQuantum() * 0.4);

        return Math.min(nonPreemptiveAGTime, pro.getRemainingTime());

    }

    public int preemptiveAG(Process pro, int _time, int exe_Quantum) {
        int process_Time = 0;

        while (pro.getRemainingTime() > 0) {
            Process p = this.getLeastAG(_time);

            if (!Objects.equals(p.getProcessName(), pro.getProcessName())) {
                break;
            }

            pro.setRemainingTime(pro.getRemainingTime() - 1);
            _time++;
            process_Time++;

            if (pro.getQuantum() == process_Time + exe_Quantum) {
                break;
            }

        }

        return process_Time;
    }

    public int getceilOfMeanQuantum() {
        double sum = 0.0;
        int i = 0;

        for (Process p : processes) {
            sum += p.getQuantum();

            if (p.getQuantum() != 0) {
                ++i;
            }
        }

        return (int) Math.ceil((sum / i) * 0.1);

    }

    public void printResults() {
        int _totalTurnaroundTime_ = 0;
        int _totalWaitingTime_ = 0;

        for (Process p : Test_out) {
            // Set Turnaround Time
            p.setTurnaroundTime(p.getWaitingTime() + p.getBurstTime());

            System.out.println(p);
        }

        for (Process p : proc_2) {

            _totalTurnaroundTime_ += p.getTurnaroundTime();
            _totalWaitingTime_ += p.getWaitingTime();
        }

        System.out.println("AVG - Turnaround Time: " + (double) _totalTurnaroundTime_ / proc_2.size());
        System.out.println("AVG - Waiting Time: " + (double) _totalWaitingTime_ / proc_2.size());
    }

    @Override
    public void process() {

        // Sort processes (arrival time - ascending order)
        Collections.sort(this.processes);

        // Set Remaining Time for all processes (Remaining Time = burstTime), AG-Factor.
        this.setProcesses();

        int preIndex = -1;
        Process current = null;
        int currentIndex;
        int time = 0;
        Process previous = null;
        time = this.processes.get(0).getArrivalTime();
         
        while (!this.isFinished()){

            if (preIndex == -1) {
                current = this.processes.get(0);
                currentIndex = 0;
            } else {
                System.out.println("----------------------------------------");
                setProcessTime();
                System.out.println("----------------------------------------");
                current = this.getBestProcess(time, preIndex);
                currentIndex = this.getProcessIndex(current);

            }
            if (current == previous) {
                readyQueue.add(processes.get(currentIndex));
                preIndex = this.getProcessIndex(current) + 1;
                continue;
            }

            if (this.processes.get(currentIndex).quantum != 0) {
                // Set Service Time
                this.processes.get(currentIndex).setServiceTime(time);
            }

            int nonPreemptiveAGTime = this.nonPreemptiveAG(current);
            time += nonPreemptiveAGTime;

            this.processes.get(currentIndex).remainingTime -= nonPreemptiveAGTime;

            int preemptiveAGTime = this.preemptiveAG(current, time, nonPreemptiveAGTime);
            time += preemptiveAGTime;

            // Update Quantum
            if (current.remainingTime == 0) {
                this.proc_2.add(current);
                // The running process finished its job
                this.processes.get(currentIndex).setQuantum(0);
            } else if ((nonPreemptiveAGTime + preemptiveAGTime) == current.quantum) {
                // The running process used all its quantum time
                this.processes.get(currentIndex).quantum += getceilOfMeanQuantum();
                readyQueue.add(current);
            } else {
                int total = (this.processes.get(currentIndex).quantum - (nonPreemptiveAGTime + preemptiveAGTime));
                this.processes.get(currentIndex).quantum += total;

                readyQueue.add(current);
            }
            // Set waiting Time
            int wT = this.processes.get(currentIndex).getServiceTime() - this.processes.get(currentIndex).arrivalTime;
            this.processes.get(currentIndex).waitingTime += wT;
            this.processes.get(currentIndex).arrivalTime = time;

            preIndex = this.getProcessIndex(current);
            Test_out.add(current);
            previous = current;

        }

        // Print Results with AVG
        this.printResults();
    }
}
