/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.black_list_search.blacklistvalidator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.eci.arsw.black_list_search.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    public static final int BLACK_LIST_ALARM_COUNT=5;

    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int nThreads){
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int totalServers = skds.getRegisteredServersCount();

        int serversPerThread = totalServers / nThreads;
        List<HostBlackListThread> threads = new LinkedList<>();

        List<Integer> sharedOcurrences = Collections.synchronizedList(new LinkedList<>());
        AtomicInteger sharedOcurrencesCount = new AtomicInteger(0);
        AtomicBoolean stopFlag = new AtomicBoolean(false);
        
        for(int i=0; i<nThreads; i++){
            int start = i * serversPerThread;
            int end = (i == nThreads - 1) ? totalServers : start + serversPerThread;
            HostBlackListThread thread = new HostBlackListThread(ipaddress, start, end, skds, sharedOcurrences, sharedOcurrencesCount, stopFlag);
            threads.add(thread);
            thread.start();
        }
        

        int checkedListsCount = 0;
        for(HostBlackListThread thread : threads) {
            try {
                thread.join();
                if (threads instanceof HostBlackListThread) {
                    checkedListsCount += ((HostBlackListThread) thread).getCheckedLists();
                }
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, "Thread interrupted", ex);
            }
        }

        if (sharedOcurrencesCount.get() >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }
        
        LOG.log(Level.INFO, "Checked Black Lists: {0} of {1}", new Object[]{checkedListsCount, totalServers});
        
        return new LinkedList<>(sharedOcurrences);
    }  
}
