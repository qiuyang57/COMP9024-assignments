package net.datastructures;

import java.io.*;
import java.util.Scanner;

public class TaskScheduler {
    static void scheduler(String file1, String file2, int m) {
        /* 1. Read text file file1
         * 2. Insert all the tasks into a heap based priority queue with key of release time
         * In terms of heap based priority queue, the time complexity of insertion for n tasks is O(nlogn). */
        HeapPriorityQueue<Integer, Task> release_queue = new HeapPriorityQueue<Integer, Task>();
        HeapPriorityQueue<Integer, Task> task_ready_queue = new HeapPriorityQueue<Integer, Task>();
        HeapPriorityQueue<Integer, String> core_ready_queue = new HeapPriorityQueue<Integer, String>();
        String data = null;
        try {
            Scanner s = new Scanner(new BufferedReader(new FileReader(file1)));
            int i = 0;
            String task_name=null;
            Integer execution_time = null;
            Integer release_time=null;
            Integer deadline_time=null;
            while (s.hasNext()){
                if (i==0){
                    task_name = s.next();
                    if (!task_name.matches("[a-zA-Z]+\\d+")){
                        System.out.format("input error when reading the name of the task\n");
                        System.exit(0);
                    }
                }
                else{
                    try{
                        Integer info = Integer.parseInt(s.next());
                        if(i==1){execution_time = info;}
                        if(i==2){release_time= info;}
                        if(i==3){
                            deadline_time = info;
                            Task task = new Task(task_name,execution_time,release_time,deadline_time);
                            release_queue.insert(task.getRelease_time(),task);
                        }
                    }catch(NumberFormatException e){
                        System.out.format("input error when reading the attribute of the task %s\n",task_name);
                        System.exit(0);
                    }
                }
                i++;
                if (i==4){i = 0;}
            }
        }
        catch (FileNotFoundException e){
            System.out.format("%s does not exist",file1);
            System.exit(0);
        }
        /* Main Process*/
        /* init*/
        Integer scheduling_point = 0;
        int index = 1;
        boolean core_init_flag=true;
        /* Loop through all the tasks, each tasks only need to be processed once.
           The total time complexity for this whole part is
            n = n1+n2+...+ni
           O(n1logn1)+O(n2logn2)+...+O(ni+logni)+O(nlogn)+O(Constant)=O(n1logn1+n2logn2+...nilogni+nlogn)
                        <O(n1logn+n2logn+...+nilogn+nlogn)=O((n1+n2+...+ni)logn+nlogn)=O(2nlogn)=O(nlogn)
         *  */
        while (!release_queue.isEmpty()||!task_ready_queue.isEmpty()){
            /* For current scheduling point, put all released tasks into a priority queue,
               and the key is deadline time of released tasks.
               The released tasks is a subset of all the tasks.
               Suppose the task number of subset is n1.
               The time complexity is O(n1log(n1)) */
            while (!release_queue.isEmpty()&&release_queue.min().getKey().compareTo(scheduling_point)<=0){
                Entry<Integer,Task> time_task = release_queue.removeMin();
                task_ready_queue.insert(time_task.getValue().getDeadline_time(),time_task.getValue());
            }
            /* Pair one released task with highest priority with a available core.
             * Get the released task with highest priority takes O(Constant)
             * Get a available core takes O(Constant)
             * */
            while ((core_init_flag||scheduling_point.compareTo(core_ready_queue.min().getKey())>=0)&&!task_ready_queue.isEmpty()){
                String core_ready;
                if(index<m+1){
                    core_ready = "Core"+Integer.toString(index);;
                    if (index==m){core_init_flag=false;}
                    index++;
                }
                else{
                    Entry<Integer,String> time_core = core_ready_queue.removeMin();
                    core_ready = time_core.getValue();
                }
                Task task_processing = task_ready_queue.removeMin().getValue();
                Integer finish_time = scheduling_point+task_processing.getExecution_time();
                /* Reset the core available time and insert core into priority queue again.
                * One insertion takes O(logm), m is total number of cores in the priority queue.
                * So the total insertion takes O(nlogm)
                * And if cores is less than tasks, O(nlogm)<O(nlogn).
                * If cores is more than tasks, only tasks number of insertion is needed. In other words, m=n, O(nlogm)=O(nlogn)
                * Therefore, we can consider insertion for cores takes O(nlogn)
                * */
                if (finish_time.compareTo(task_processing.getDeadline_time())<=0) {
                    core_ready_queue.insert(scheduling_point + task_processing.getExecution_time(), core_ready);
                    if (data==null) {
                        data = String.format("%s %s %s ", task_processing.getName(), core_ready, scheduling_point.toString());
                    }
                    else{
                        data += String.format("%s %s %s ", task_processing.getName(), core_ready, scheduling_point.toString());
                    }
                }
                else{
                    System.out.println("No feasible schedule exists");
                    return;
                }
            }
            /* determine next scheduling point according to 3 priority queue state*/
            /* Pure logic, no loop, time complexity for this part is a constant*/
            if (!core_init_flag) {
                if (scheduling_point.compareTo(core_ready_queue.min().getKey()) < 0 && task_ready_queue.isEmpty()) {
                    if (release_queue.isEmpty()){scheduling_point = core_ready_queue.min().getKey();}
                    else {
                        Integer earliest_release_time = release_queue.min().getKey();
                        Integer earliest_ready_time = core_ready_queue.min().getKey();
                        scheduling_point = earliest_release_time.compareTo(earliest_ready_time) >= 0 ? earliest_release_time : earliest_ready_time;
                    }
                } else if (scheduling_point.compareTo(core_ready_queue.min().getKey()) >= 0&&task_ready_queue.isEmpty()) {
                    if (release_queue.isEmpty()){
                        scheduling_point = core_ready_queue.min().getKey();
                    }
                    else{
                        scheduling_point = release_queue.min().getKey();}
                } else {
                    scheduling_point = core_ready_queue.min().getKey();
                }
            }
            else{
                if (task_ready_queue.isEmpty()&&!core_ready_queue.isEmpty()){
                    Integer earliest_release_time = release_queue.min().getKey();
                    Integer earliest_ready_time = core_ready_queue.min().getKey();
                    scheduling_point = earliest_release_time.compareTo(earliest_ready_time) >= 0 ? earliest_release_time : earliest_ready_time;
                }
                else if (core_ready_queue.isEmpty()){
                    scheduling_point = release_queue.min().getKey();
                }
                else {scheduling_point = task_ready_queue.min().getKey();
                }
            }

        }
        /* Write the data into file2 */
        try {
            PrintWriter writer = new PrintWriter(file2, "UTF-8");
            writer.println(data);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /* In conclusion, the whole algorithm time complexity is O(nlogn)+O(nlogn)+O(Constant)=O(nlogn) */

    public static void main(String[] args) throws Exception{

        TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule1", 4);
        /** There is a feasible schedule on 4 cores */
        TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule2", 3);
        /** There is no feasible schedule on 3 cores */
        TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule3", 5);
        /** There is a feasible scheduler on 5 cores */
        TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule4", 4);
        /** There is no feasible schedule on 4 cores */

        /** There is a feasible scheduler on 2 cores */
        TaskScheduler.scheduler("samplefile3.txt", "feasibleschedule5", 2);
        /** There is a feasible scheduler on 2 cores */
        TaskScheduler.scheduler("samplefile4.txt", "feasibleschedule6", 2);

        }
}

/* Task Class */
class Task{
    private String name;
    private Integer execution_time;
    private Integer release_time;
    private Integer deadline_time;

    Task(String task_name, Integer execution_time, Integer release_time, Integer deadline_time) {
        this.name = task_name;
        this.execution_time = execution_time;
        this.release_time = release_time;
        this.deadline_time = deadline_time;
    }

    String getName(){
        return this.name;
    }
    Integer getExecution_time(){
        return this.execution_time;
    }

    Integer getRelease_time(){
        return this.release_time;
    }

    Integer getDeadline_time(){
        return this.deadline_time;
    }
}

