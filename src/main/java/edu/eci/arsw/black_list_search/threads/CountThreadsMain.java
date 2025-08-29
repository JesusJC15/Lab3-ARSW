/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.black_list_search.threads;

/**
 * * A simple main class to run multiple threads.
 * @author hcadavid
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
        CountThread thread1 = new CountThread(0, 99);
        CountThread thread2 = new CountThread(100, 199);
        CountThread thread3 = new CountThread(200, 299);

        thread1.run();
        thread2.run();
        thread3.run();
    }
    
}
