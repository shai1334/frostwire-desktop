package com.limegroup.gnutella.gui.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.frostwire.gui.filters.TableLineFilter;
import com.limegroup.gnutella.settings.SearchSettings;

/**
 * Filters out certain rows from the data model.
 *
 * @author Sumeet Thadani, Sam Berlin
 */
public class TableRowFilteredModel extends ResultPanelModel {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7810977044778830969L;

    /**
     * The filter to use in this row filter.
     */
    private final TableLineFilter<SearchResultDataLine> FILTER;
    
    /**
     * The Junk Filter
     */
    private TableLineFilter<SearchResultDataLine> junkFilter = AllowFilter.instance();
    
    /**
     * A list of all filtered results.
     */
    protected final List<SearchResultDataLine> HIDDEN;
    
    /**
     * The number of sources in the hidden list.
     */
    private int _numSources;
    
    private int _numResults;

    /**
     * Constructs a TableRowFilter with the specified TableLineFilter.
     */
    public TableRowFilteredModel(TableLineFilter<SearchResultDataLine> f) {
        super();

        if(f == null)
            throw new NullPointerException("null filter");

        FILTER = f;
        HIDDEN = new ArrayList<SearchResultDataLine>();
        _numSources = 0;
        _numResults = 0;
    }
    
    /**
     * Returns true if Table is sorted which means either
     * it is really sorted OR 'move junk to bottom' is
     * selected which is also some kind of sorting!
     */
    public boolean isSorted() {
        return super.isSorted() || SearchSettings.moveJunkToBottom();
    }


    /**
     * Gets the amount of filtered sources.
     */
    public int getFilteredSources() {
        return super.getTotalSources();
    }
    
    /**
     * Gets the total amount of sources.
     */
    public int getTotalSources() {
        return getFilteredSources() + _numSources;
    }
    
    /**
     * Determines whether or not this line should be added.
     */
    public int add(SearchResultDataLine tl, int row) {
        boolean isNotJunk = junkFilter.allow(tl);
        boolean allow = allow(tl);
             
        if(isNotJunk || !SearchSettings.hideJunk()) {
            if (allow) {
                return super.add(tl, row);
            } else {
                HIDDEN.add(tl);
                _numSources += tl.getSeeds();
                _numResults += 1;
            }
        } else {
            _numSources += tl.getSeeds();
            _numResults += 1;
        }
        return -1;
    }
    
    /**
     * Intercepts to clear the hidden map.
     */
    protected void simpleClear() {
        _numSources = 0;
        _numResults = 0;
        HIDDEN.clear();
        super.simpleClear();
    }
    
    /**
     * Notification that the filters have changed.
     */
    void filtersChanged() {
        rebuild();
        fireTableDataChanged();
    }
	
    /**
     * Sets the Junk Filter. Pass null as argument to disable the filter
     */
    void setJunkFilter(TableLineFilter<SearchResultDataLine> junkFilter) {
        if (junkFilter != null) {
            this.junkFilter = junkFilter;
        } else {
            this.junkFilter = AllowFilter.instance();
        }
    }
    
    /**
     * Determines whether or not the specified line is allowed by the filter.
     */
    private boolean allow(SearchResultDataLine line) {
        return FILTER.allow(line);
    }
    
    /**
     * Rebuilds the internal map to denote a new filter.
     */
    private void rebuild(){
        List<SearchResultDataLine> existing = new ArrayList<SearchResultDataLine>(_list);
        List<SearchResultDataLine> hidden = new ArrayList<SearchResultDataLine>(HIDDEN);
        simpleClear();
        
        // For stuff in _list, we can just re-add the DataLines as-is.
        if(isSorted()) {
            for(int i = 0; i < existing.size(); i++) {
                addSorted(existing.get(i));
            }
        } else {
            for(int i = 0; i < existing.size(); i++) {
                add(existing.get(i));
            }
        }
        
        // Merge the hidden TableLines
        Map<String, SearchResultDataLine> mergeMap = new HashMap<String, SearchResultDataLine>();
        for(int i = 0; i < hidden.size(); i++) {
            SearchResultDataLine tl = hidden.get(i);
            //SearchResult sr = tl.getInitializeObject();
            //String urn = sr.getHash();
            
            if(isSorted()) {
                addSorted(tl);
            } else {
                add(tl);
            }
            
//            TableLine tableLine = mergeMap.get(urn);
//            if (tableLine == null) {
//                mergeMap.put(urn, tl); // re-use TableLines
//            } else {
//                tableLine.addNewResult(sr, METADATA);
//            }
        }
        
        // And add them
        if(isSorted()) {
            for(SearchResultDataLine line : mergeMap.values())
                addSorted(line);
        } else {
            for(SearchResultDataLine line : mergeMap.values())
                add(line);
        }
    }

    public int getFilteredResults() {
        return super.getTotalResults();
    }
    
   public int getTotalResults() {
        return getFilteredResults() + _numResults;
    }
   
	public List<SearchResultDataLine> getAllData() {
		List<SearchResultDataLine> results = new ArrayList<SearchResultDataLine>(HIDDEN);
		results.addAll(_list);
		return results;
	}

}
