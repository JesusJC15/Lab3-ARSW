package edu.eci.arsw.black_list_search.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import edu.eci.arsw.black_list_search.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 * Thread to check if a host's IP address is in a blacklist.
 * It checks a range of blacklists from start to end.
 * @author Natalia Espinel - Jesus Jauregui
 */
public class HostBlackListThread extends Thread{
    private final String ip;
    private final int start;
    private final int end;
    private final HostBlacklistsDataSourceFacade skds;
    private final List<Integer> sharedOcurrences;
    private final AtomicInteger sharedOcurrencesCount;
    private final AtomicBoolean stopFlag;
    private int checkedLists = 0;

    public HostBlackListThread(String ip, int start, int end, HostBlacklistsDataSourceFacade skds, List<Integer> sharedOcurrences, AtomicInteger sharedOcurrencesCount, AtomicBoolean stopFlag) {
        this.ip = ip;
        this.start = start;
        this.end = end;
        this.skds = skds;
        this.sharedOcurrences = sharedOcurrences;
        this.sharedOcurrencesCount = sharedOcurrencesCount;
        this.stopFlag = stopFlag;
    }

    @Override
    public void run(){
        for (int i = start; i < end && !stopFlag.get(); i++) {
            if (skds.isInBlackListServer(i, ip)) {
                synchronized (sharedOcurrences) {
                    sharedOcurrences.add(i);
                }
                int currentCount = sharedOcurrencesCount.incrementAndGet();

                if (currentCount >= HostBlackListsValidator.BLACK_LIST_ALARM_COUNT) {
                    stopFlag.set(true);
                }
            }
            checkedLists++;
        }
    }

    /**
     * Returns the number of blacklists checked by this thread.
     * @return Number of checked blacklists.
     */
    public int getCheckedLists() {
        return checkedLists;
    }
}
