/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.black_list_search.blacklistvalidator;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        
        System.out.println("IP: 200.24.34.55");
        hblv.checkHost("200.24.34.55", 10);

        System.out.println("IP: 202.24.34.55");
        hblv.checkHost("202.24.34.55", 10);

        System.out.println("IP: 212.24.24.55");
        hblv.checkHost("212.24.24.55", 10);
        
    }
    
}
