/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upennlib.metridoc.impl.file;

/**
 *
 * @author tbarker
 */
public enum DefaultIteratorNames {

    XLS(DefaultIteratorNames.DEFAULT_EXCEL),
    TXT(DefaultIteratorNames.DEFAULT_TEXT),
    CSV(DefaultIteratorNames.DEFAULT_TEXT);

    public static final String DEFAULT_EXCEL = "defaultExcelIterator";
    public static final String DEFAULT_TEXT = "defaultTextIterator";
    private String description;

    private DefaultIteratorNames(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
