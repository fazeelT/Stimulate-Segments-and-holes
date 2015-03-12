import java.util.*;
import java.lang.*;
import java.io.*;

public class MUTOP1 {

    public static void main(String[] args) {
        Memory memory = null; // the memory object
        int timeOfDay = 0; // the simulated wall clock, begins with value zero
        int placements = 0; // the number of placements completed, begins with value zero
        long totalSpaceTime = 0; // the sum of placed segmentSize(i) x sementLifetime(i)

        Scanner sc = new Scanner(System.in); // switch the comments before submitting
        /*Scanner sc = null;
         try {
         sc = new Scanner(new File("src/p115sd5.txt"));
         } catch (Exception E) {
         System.out.println("File not found");

         }*/
        String line = "";
        boolean done = false;

        while (!done) {
            line = sc.nextLine();
            String[] tokens = line.split(" ");
            switch (tokens[0]) {
                case "N": {
                    System.out.println("Muhammad Tola");
                    break;
                }
                case "C": {
                    memory = new Memory(Integer.parseInt(tokens[1])); // create a new Memory object
                    break;
                }
                case "E": {
                    done = true;
                    break;
                }
                case "A": {
                    int size = Integer.parseInt(tokens[1]);
                    int lifeTime = Integer.parseInt(tokens[2]);
                    timeOfDay++;
                    memory.removeSegmentsDueToDepart(timeOfDay);
                    while (!memory.place(size, timeOfDay, lifeTime, true)) { // timeToDepart=timeOfDay+lifeTime
                        timeOfDay++;
                        memory.removeSegmentsDueToDepart(timeOfDay);
                    }
                    placements++;
                    // then print the confirmation message

                    break;
                }
                case "P": {
                    memory.printLayout();
                    break;
                }

                case "R": {
                    int s = Integer.parseInt(tokens[1]);    //Taking all input 
                    int u = Integer.parseInt(tokens[2]);    //saving them into 
                    int v = Integer.parseInt(tokens[3]);    //variables for 
                    int w = Integer.parseInt(tokens[4]);    //further process.
                    int x = Integer.parseInt(tokens[5]);

                    memory = new Memory(s); // initialize variables in main()
                    timeOfDay = 0;
                    placements = 0;

                    Random ran = new Random(); // pseudo random number generator
                    while (placements < x) {
                        timeOfDay++;
                        memory.removeSegmentsDueToDepart(timeOfDay);
                        int newSegSize = u + ran.nextInt(v - u + 1);
                        int newSegLifetime = 1 + ran.nextInt(w);
                        totalSpaceTime += newSegSize * newSegLifetime;
                        while (!memory.place(newSegSize, timeOfDay, newSegLifetime, false)) {
                            timeOfDay++;
                            memory.removeSegmentsDueToDepart(timeOfDay);
                        }
                        placements++;
                    }
                    System.out.format("Number of placements made = %6d\n", placements);
                    float meanOccupancy = (float) totalSpaceTime / timeOfDay;
                    System.out.format("Mean occupancy of memory = %8.2f\n", meanOccupancy);
                    break;
                }
            } // end of switch
        }

    }
}

class Node {

    boolean segment; // equals false if this Node represents a hole
    int location; // position in memory of first byte
    int size;
    int timeToDepart; // only valid when this Node represents a segment
    Node next;
    /*
     constructor for a segment
     */

    Node(int locn, int sz, int endOfLife, Node nxt) {
        segment = true;
        location = locn;
        size = sz;
        timeToDepart = endOfLife;
        next = nxt;
    }
    /*
     constructor for a hole
     */

    Node(int locn, int sz, Node nxt) {
        segment = false;
        location = locn;
        size = sz;
        next = nxt;
    }

}

class Memory {

    Node head; // reference to first Node in memory - could be a hole or a segment
    Node lastPlacement; // references the last segment placed, or a hole if that segment is removed
    int location = 0;
    /*
     constructor for Memory, generates a single hole Node of the given size
     */

    Memory(int size) {
        head = new Node(location, size, null);                                //Create Memory
        lastPlacement = head;                                                 //Setting lastPlacement to head
    }
    /*
     attempt to place a request using the Next Fit policy. Returns false if there isn't a hole big enough.
     Prints a confirmation if placed and verbose==true
     */

    boolean place(int size, int timeOfDay, int lifetime, boolean verbose) {
        Node temp, ref = lastPlacement;
        Node prev = null;

        do {
            if (ref == head && head.segment == false && head.size >= size) {  //if head segment expired
                head.size -= size;                                            //Preparing the hole after placement
                if (head.size != 0) {
                    head.location += size;
                    head = new Node(location = 0, size, (lifetime + timeOfDay), head);
                    ref = head;
                    prev = ref;
                } else {                                                      //if size of hole=size of segment
                    head = new Node(location = 0, size, (lifetime + timeOfDay), head.next);
                    prev = ref;
                }
                if (verbose == true) //checking to see if print is needed
                {
                    System.out.println("Segment of size " + size + " placed at time "
                            + timeOfDay + " at location " + location
                            + ", departs at " + (lifetime + timeOfDay));
                }

                lastPlacement = head;                                         //setting the lastPLacement
                return true;
            } else if (ref.segment == false && ref.size >= size) {
                ref.segment = true;
                ref.timeToDepart = lifetime + timeOfDay;
                int newSize = ref.size - size;                                //Setting up the hole to be placed
                ref.size = size;
                if (newSize != 0) {                                           //If segment is smaller than the hole
                    temp = new Node(location = (ref.location + ref.size), newSize, ref.next);

                    ref.next = temp;                                //Attaching the hole in front

                }

                if (verbose == true) {
                    System.out.println("Segment of size " + size + " placed at time "
                            + timeOfDay + " at location " + location
                            + ", departs at " + (lifetime + timeOfDay));
                }

                lastPlacement = ref;
                return true;
            } else if (ref.next != null && ref.next.segment == false && ref.next.size >= size) {//Checkign for next segment
                prev = ref;
                ref = ref.next;
                ref.size -= size;                                             //Settimg up the hole
                if (ref.size != 0) {                                          //If hole is needed
                    temp = new Node(location = ref.location, size, (lifetime + timeOfDay), ref);
                    prev.next = temp;
                    ref.location += size;
                    ref = prev.next;
                } else {                                                      //if hole size==segment size
                    temp = new Node(location = ref.location, size, (lifetime + timeOfDay), ref.next);
                    ref.next = null;                                          //garbage cleaning
                    ref = temp;
                    prev.next = ref;                                          //setting prev-ref pair
                }
                if (verbose == true) {
                    System.out.println("Segment of size " + size + " placed at time "
                            + timeOfDay + " at location " + location
                            + ", departs at " + (lifetime + timeOfDay));
                }

                lastPlacement = ref;
                return true;
            } else {                                                          //Loop forward
                prev = ref;                                     
                ref = ref.next;
            }
            if (ref == null) {                                                //If we reach end 
                ref = head;                                                   //of list
            }
        } while (ref != lastPlacement);                                       //Loop to run from and to lastPlacement
        return false;
    }

    /*
     remove segments whose time to depart has occurred
     */
    void removeSegmentsDueToDepart(int timeOfDay) {
        if (head.segment == true && head.timeToDepart == timeOfDay) {           //Remove head due to departure
            if (head.next != null && head.next.segment == false) {
                if (head.next == lastPlacement) {                               //Checking to see if lastPlacement hole 
                    lastPlacement = head;                                       //and head are merging and moving lastPlacement back
                }                                                               //to merged hole
                head.segment = false;                                           //Segment Removed
                head.size += head.next.size;                                    //Merging holes
                head.next = head.next.next;                                     //Skipping the intermediate holes
            } else {    
                head.segment = false;
            }
        }
        Node prev = head;
        Node ref = prev.next;

        while (ref != null) {                                                   //Iterating through till the tail
            if (ref.segment == true && ref.timeToDepart == timeOfDay) {         //If segment is found
                if (prev.segment == false && ref.next != null && ref.next.segment == false) {//Cheking for previous and following hole
                    prev.size += (ref.size + ref.next.size);                    //Merging consecutive holes
                    prev.next = ref.next.next;                                  //Removing intermediate holes
                    if (lastPlacement == ref || lastPlacement == ref.next) {    //If lastPlaced segment expires
                        lastPlacement = prev;                                   //LastPlacement moves back to the merged hole
                    }
                    ref.next.next = null;                                       //Taking care of garbage
                    ref.next = null;                                            //Taking care of garbage
                    ref = prev.next;    
                } else if (prev.segment == false) {                             //Cheking for previous hole
                    prev.size += ref.size;                                      //Merging Consecutive holes
                    prev.next = ref.next;                                       //Skipping over the intermediate hole
                    if (lastPlacement == ref) {                                 //If lastPlaced segment expires
                        lastPlacement = prev;                                   //LastPlacement moves back to the merged hole
                    }
                    ref.next = null;
                    ref = prev.next;
                } else if (ref.next != null && ref.next.segment == false) {     //Cheking for following hole
                    prev = ref;
                    ref = prev.next;
                    prev.size += ref.size;                                      //Merging Holes
                    prev.next = ref.next;                                       //Skipping over the intermediate hole
                    prev.segment = false;                                       //Segment Removed
                    ref.next = null;
                    if (ref == lastPlacement) {                                 //If lastPlaced segment expires
                        lastPlacement = prev;                                   //LastPlacement moves back to the merged hole
                    }
                    ref = prev.next;
                } else {
                    ref.segment = false;
                }
            } else {
                prev = ref;                                                     //Moving to the next link
            }
            ref = prev.next;

        }

    }

    /*
     print a 3-column tab-separated list of all segments in Memory
     */
    void printLayout() {
        Node searchNode = head;
        while (searchNode != null) {                                        //Iterating through to the end.
            if (searchNode.segment == true) {
                System.out.format("%2d      %2d     %2d\n",                 //Print format
                        searchNode.location, searchNode.size,
                        searchNode.timeToDepart);
            }
            searchNode = searchNode.next;                                   //Looping to the next link
        }   
    }
}
