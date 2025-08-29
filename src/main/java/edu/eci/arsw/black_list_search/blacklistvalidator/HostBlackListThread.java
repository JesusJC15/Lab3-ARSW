package edu.eci.arsw.black_list_search.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;

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
    private final List<Integer> localOcurrences = new LinkedList<>();
    private int checkedLists = 0;

    public HostBlackListThread(String ip, int start, int end, HostBlacklistsDataSourceFacade skds) {
        this.ip = ip;
        this.start = start;
        this.end = end;
        this.skds = HostBlacklistsDataSourceFacade.getInstance();
    }

    @Override
    public void run(){
        for (int i = start; i < end; i++) {
            if (skds.isInBlackListServer(i, ip)) {
                localOcurrences.add(i);
            }
            checkedLists++;
        }
    }

    /**
     * Returns the list of blacklists where the host's IP address was found.
     * @return List of integers representing the blacklists.
     */
    public List<Integer> getLocalOcurrences() {
        return localOcurrences;
    }

    /**
     * Returns the number of blacklists checked by this thread.
     * @return Number of checked blacklists.
     */
    public int getCheckedLists() {
        return checkedLists;
    }
}
