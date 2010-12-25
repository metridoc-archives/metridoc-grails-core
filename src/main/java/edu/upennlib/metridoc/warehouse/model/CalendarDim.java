/**
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
/*
 * Property of the University of Pennsylvania Libraries
 */

package edu.upennlib.metridoc.warehouse.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Thomas Barker
 */
public class CalendarDim implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Date fullDate;
    private int calendarDay;
    private int calendarMonth;
    private int calendarWeek;
    private int calendarYear;
    private int dayOfMonth;
    private int dayOfWeek;
    private int calendarQuarter;
    private int fiscalQuarter;
    private int fiscalYear;
    private int numericFullDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public int getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(int calendarDay) {
        this.calendarDay = calendarDay;
    }

    public int getCalendarMonth() {
        return calendarMonth;
    }

    public void setCalendarMonth(int calendarMonth) {
        this.calendarMonth = calendarMonth;
    }

    public int getCalendarQuarter() {
        return calendarQuarter;
    }

    public void setCalendarQuarter(int calendarQuarter) {
        this.calendarQuarter = calendarQuarter;
    }

    public int getCalendarWeek() {
        return calendarWeek;
    }

    public void setCalendarWeek(int calendarWeek) {
        this.calendarWeek = calendarWeek;
    }

    public int getCalendarYear() {
        return calendarYear;
    }

    public void setCalendarYear(int calendarYear) {
        this.calendarYear = calendarYear;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getFiscalQuarter() {
        return fiscalQuarter;
    }

    public void setFiscalQuarter(int fiscalQuarter) {
        this.fiscalQuarter = fiscalQuarter;
    }

    public int getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(int fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getFullDate() {
        return fullDate;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setFullDate(Date fullDate) {
        this.fullDate = fullDate;
    }

    public int getNumericFullDate() {
        return numericFullDate;
    }

    public void setNumericFullDate(int numericFullDate) {
        this.numericFullDate = numericFullDate;
    }

    @Override
    public String toString() {
        return "CalendarDefault{" + "fullDate=" + fullDate + ", calendarDay=" + calendarDay + ", calendarMonth=" +
                calendarMonth + ", calendarWeek=" + calendarWeek + ", calendarYear=" + calendarYear + ", dayOfMonth=" +
                dayOfMonth + ", dayOfWeek=" + dayOfWeek + ", calendarQuarter=" + calendarQuarter + ", fiscalQuarter=" +
                fiscalQuarter + ", fiscalYear=" + fiscalYear + ", numericFullDate=" + numericFullDate + '}';
    }
}
