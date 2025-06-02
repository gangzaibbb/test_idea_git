import java.util.*;

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int completionTime;
    int turnaroundTime;
    double weightedTurnaroundTime;

    public Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    @Override
    public String toString() {
        return String.format("%s\t%d\t%d\t%d\t%d\t%.2f",
                name, arrivalTime, burstTime, completionTime,
                turnaroundTime, weightedTurnaroundTime);
    }
}

public class Task2 {

    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("A", 0, 3));
        processes.add(new Process("B", 2, 6));
        processes.add(new Process("C", 4, 4));
        processes.add(new Process("D", 6, 5));
        processes.add(new Process("E", 8, 2));

        System.out.println("FCFS 调度算法:");
        fcfs(new ArrayList<>(processes));

        System.out.println("\nRR 调度算法 (时间片=1):");
        rr(new ArrayList<>(processes), 1);

        System.out.println("\nSJF 调度算法:");
        sjf(new ArrayList<>(processes));

        System.out.println("\nHRN 调度算法:");
        hrn(new ArrayList<>(processes));
    }

    // 先来先服务调度算法
    public static void fcfs(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        for (Process p : processes) {
            if (currentTime < p.arrivalTime) {
                currentTime = p.arrivalTime;
            }
            p.completionTime = currentTime + p.burstTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.weightedTurnaroundTime = (double)p.turnaroundTime / p.burstTime;
            currentTime = p.completionTime;
        }

        printResults(processes);
    }

    // 轮转调度算法
    public static void rr(List<Process> processes, int quantum) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        Queue<Process> queue = new LinkedList<>();
        int currentTime = 0;
        int index = 0;

        while (!queue.isEmpty() || index < processes.size()) {
            // 添加所有到达的进程到队列
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                if (index < processes.size()) {
                    currentTime = processes.get(index).arrivalTime;
                    continue;
                } else {
                    break;
                }
            }

            Process p = queue.poll();
            int timeSlice = Math.min(quantum, p.remainingTime);
            p.remainingTime -= timeSlice;
            currentTime += timeSlice;

            // 检查是否有新进程到达，添加到队列
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            // 未执行完继续添加到队列
            if (p.remainingTime > 0) {
                queue.add(p);
            } else {
                p.completionTime = currentTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.weightedTurnaroundTime = (double)p.turnaroundTime / p.burstTime;
            }
        }

        printResults(processes);
    }

    // 最短作业优先调度算法
    public static void sjf(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        PriorityQueue<Process> queue = new PriorityQueue<>(
                Comparator.comparingInt(p -> p.burstTime));

        int currentTime = 0;
        int index = 0;

        while (!queue.isEmpty() || index < processes.size()) {
            // 添加所有到达的进程到队列
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                if (index < processes.size()) {
                    currentTime = processes.get(index).arrivalTime;
                    continue;
                } else {
                    break;
                }
            }

            Process p = queue.poll();
            currentTime += p.burstTime;
            p.completionTime = currentTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.weightedTurnaroundTime = (double)p.turnaroundTime / p.burstTime;
        }

        printResults(processes);
    }

    // 最高响应比优先调度算法
    public static void hrn(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        List<Process> queue = new ArrayList<>();
        int currentTime = 0;
        int index = 0;

        while (!queue.isEmpty() || index < processes.size()) {
            // 添加所有到达的进程到队列
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                if (index < processes.size()) {
                    currentTime = processes.get(index).arrivalTime;
                    continue;
                } else {
                    break;
                }
            }

            // 计算响应比并选择最高的
            Process selected = null;
            double maxRatio = -1;
            for (Process p : queue) {
                double ratio = (currentTime - p.arrivalTime + p.burstTime) / (double)p.burstTime;
                if (ratio > maxRatio) {
                    maxRatio = ratio;
                    selected = p;
                }
            }

            queue.remove(selected);
            currentTime += selected.burstTime;
            selected.completionTime = currentTime;
            selected.turnaroundTime = selected.completionTime - selected.arrivalTime;
            selected.weightedTurnaroundTime = (double)selected.turnaroundTime / selected.burstTime;
        }

        printResults(processes);
    }

    // 打印结果
    public static void printResults(List<Process> processes) {
        System.out.println("进程\t到达\t服务\t完成\t周转\t带权周转");
        for (Process p : processes) {
            System.out.println(p);
        }

        double avgTurnaround = processes.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double avgWeightedTurnaround = processes.stream().mapToDouble(p -> p.weightedTurnaroundTime).average().orElse(0);

        System.out.printf("平均周转时间: %.2f\n", avgTurnaround);
        System.out.printf("平均带权周转时间: %.2f\n", avgWeightedTurnaround);
    }
}